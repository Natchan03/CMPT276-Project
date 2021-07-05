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
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.*;

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

  // Loads each static page corresponding to its page name
  @RequestMapping("{pageName}")
  public String loadStaticPage(@PathVariable("pageName") String pageName) { 
    System.out.println(pageName);
    return pageName.isEmpty() ? "index" : pageName;
  }

  @GetMapping(
    path = "/signup"
  )
  public String getSignupPage(Map<String, Object> model){
    User user = new User();
    model.put("user", user);
    return "signup";
  }

  @PostMapping(
    path = "/user",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserUserSubmit(Map<String, Object> model, User user) throws Exception {
    
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();

      // Create users table if not exists
      String sql1 = "CREATE TABLE IF NOT EXISTS users " +
      "(id serial,fname varchar(40),lname varchar(40), email varchar(40), password varchar(256), type varchar(20))";
      System.out.println(sql1);
      stmt.executeUpdate(sql1);

      // Check whether the given user already exists. If so, fail the request
      ResultSet rs = stmt.executeQuery("SELECT * FROM users where email=" + "'" + user.getEmail() + "'");
      if (rs.next()){
        return "error";
      }

      // Hash the password
      String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

      // Insert the new user to the table
      String sql = "INSERT INTO users(fname, lname, email, password, type) VALUES ('" + user.getFname() + "','" + user.getLname() +"','" + user.getEmail() + "','" + hashed + "', 'regular')" ;// shouldnt 'regular' be user.getType()? - simar
      System.out.println(sql);
      stmt.executeUpdate(sql);

      // Redirect back to main page
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
          path = "/login"
  )
  public String getLoginPage(Map<String, Object> model){
    User user = new User();
    model.put("user", user);
    return "login";
  }

  // Checks if submitted login info is valid
  @PostMapping(
    path = "/login",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String authenicateLogin(Map<String, Object> model, User user) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();

      // Checks if the given email and hashed password combination is in the table
      ResultSet rs = stmt.executeQuery("SELECT * FROM users where email=" + "'" + user.getEmail() + "'");
      if (rs.next() && BCrypt.checkpw(user.getPassword(), rs.getString("password"))) {
          System.out.println("Logging in to " + user.getEmail());
          return "redirect:/";
      }
      System.out.println("Invalid user and password combination");
      return "redirect:/login";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
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