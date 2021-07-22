/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  // Method called on application start-up to do some initialization
  @PostConstruct
  private void postConstruct() {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      // Create users table if not exists
      String sql1 = "CREATE TABLE IF NOT EXISTS users "
          + "(id serial,fname varchar(40),lname varchar(40), email varchar(40), password varchar(256), type varchar(20))";
      System.out.println(sql1);
      stmt.executeUpdate(sql1);

      // Create notes table if not exists
      String sql2 = "CREATE TABLE IF NOT EXISTS notes "
          + "(id serial,videoId varchar(11),title varchar(40),dateCreated date,ownerId bigint,sharedIds bigint[],"
          + "PRIMARY KEY(id))";
      System.out.println(sql2);
      stmt.executeUpdate(sql2);

      // Create timestamps table if not exists.
      // Note the primary key is a combination of seconds and the foreign key to the id in the notes table
      String sql3 = "CREATE TABLE IF NOT EXISTS timestamps "
          + "(noteId bigint,seconds bigint,content varchar(300),"
          + "PRIMARY KEY(noteId,seconds),CONSTRAINT fk_notes FOREIGN KEY(noteId) REFERENCES notes(id))";
      System.out.println(sql3);
      stmt.executeUpdate(sql3);

      // Check whether the table has admin
      rs = stmt.executeQuery("SELECT * FROM users where email='admin@younote.com'");
      if (rs.next()) {
        // There is already an admin account no need to create new one
        return;
      }

      // Hash the password
      String hashed = BCrypt.hashpw("admin", BCrypt.gensalt());

      // Insert admin to the table
      String sql = "INSERT INTO users(fname, lname, email, password, type) VALUES ('admin','admin','admin@younote.com','"
          + hashed + "', 'admin')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
  }

  // Loads each static page corresponding to its page name
  @RequestMapping("{pageName}")
  public String loadStaticPage(@PathVariable("pageName") String pageName) {
    System.out.println(pageName);
    return pageName.isEmpty() ? "index" : pageName;
  }

  @GetMapping(path = "/signup")
  public String getSignupPage(Map<String, Object> model) {
    User user = new User();
    model.put("user", user);
    return "signup";
  }

  @GetMapping(path = "/admin")
  public String getAdminDashboard(Map<String, Object> model) {

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    ArrayList<User> userList = new ArrayList<User>();

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      // Gets all users
      rs = stmt.executeQuery("SELECT * FROM users");

      while (rs.next()) {

        User user = new User();

        // Construct the user object from the database

        user.setId(rs.getLong("id"));
        user.setFname(rs.getString("fname"));
        user.setLname(rs.getString("lname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setType(rs.getString("type"));

        userList.add(user);
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }

    model.put("userList", userList);
    return "admin";
  }

  @PostMapping(path = "/user", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowserUserSubmit(Map<String, Object> model, User user) throws Exception {
    System.out.println("signup request for: " + user.getEmail());

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      if (user.getFname() == null || user.getFname().trim().isEmpty()) {
        model.put("message", "Invalid First Name");
        return "error";
      }
      if (user.getLname() == null || user.getLname().trim().isEmpty()) {
        model.put("message", "Invalid Last Name");
        return "error";
      }
      if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        model.put("message", "Invalid Email");
        return "error";
      }
      if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
        model.put("message", "Invalid Password");
        return "error";
      }

      if (!Utils.isValidEmailAddress(user.getEmail())){
        model.put("message", "Invalid Email Format (ex:name@example.com)");
        return "error";
      }

      // Check whether the given user already exists. If so, fail the request
      rs = stmt.executeQuery("SELECT * FROM users where email=" + "'" + user.getEmail() + "'");
      if (rs.next()) {
        return "error";
      }

      // Hash the password
      String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

      // Insert the new user to the table
      String sql = "INSERT INTO users(fname, lname, email, password, type) VALUES ('" + user.getFname() + "','"
          + user.getLname() + "','" + user.getEmail() + "','" + hashed + "', 'regular')";
      System.out.println(sql);
      stmt.executeUpdate(sql);

      // Redirect back to main page
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
  }

  @GetMapping(path = "/login")
  public String getLoginPage(Map<String, Object> model) {
    return "login";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return "redirect:/login?logout";
  }

  @GetMapping(path = "/display_user/{id}")
  public String displayUser(Map<String, Object> model, @PathVariable String id){
    User user = new User();
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      // Gets all users
      rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + id);

      while (rs.next()) {
        user.setId(rs.getLong("id"));
        user.setFname(rs.getString("fname"));
        user.setLname(rs.getString("lname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setType(rs.getString("type"));
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    model.put("user", user);
    return "display_user";
  }

  @PostMapping(path = "/delete_account/{id}")
  public String deleteAccount(Map<String, Object> model, @PathVariable String id){
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      stmt.executeUpdate("DELETE FROM users WHERE id = " + id);

    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    return "redirect:/admin";
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }
}