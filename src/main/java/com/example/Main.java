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
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Optional;

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
          + "(id serial,videoId varchar(11),title varchar(40),content varchar(2000),dateCreated date,ownerId bigint,sharedIds bigint[],"
          + "PRIMARY KEY(id))";
      System.out.println(sql2);
      stmt.executeUpdate(sql2);

      // Create timestamps table if not exists.
      // Note the primary key is a combination of seconds and the foreign key to the
      // id in the notes table
      String sql3 = "CREATE TABLE IF NOT EXISTS timestamps " + "(noteId bigint,seconds bigint,content varchar(300),"
          + "PRIMARY KEY(noteId,seconds),CONSTRAINT fk_notes FOREIGN KEY(noteId) REFERENCES notes(id))";
      System.out.println(sql3);
      stmt.executeUpdate(sql3);

      // Create shares table if not exists.
      String sql4 = "CREATE TABLE IF NOT EXISTS shares " + "(id serial, shared_with_id bigint, noteId bigint, "
          + "PRIMARY KEY(id),CONSTRAINT fk_notes FOREIGN KEY(noteId) REFERENCES notes(id))";
      System.out.println(sql4);
      stmt.executeUpdate(sql4);

      // Create shares table if not exists.
      String sql5 = "ALTER TABLE shares ADD COLUMN IF NOT EXISTS is_editable boolean";
      System.out.println(sql5);
      stmt.executeUpdate(sql5);

      // Create shares table if not exists.
      String sql6 = "ALTER TABLE shares DROP CONSTRAINT fk_notes, ADD CONSTRAINT fk_notes FOREIGN KEY (noteId) REFERENCES notes(id) ON DELETE CASCADE";
      System.out.println(sql6);
      stmt.executeUpdate(sql6);

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

      if (!Utils.isValidEmailAddress(user.getEmail())) {
        model.put("message", "Invalid Email Format (ex:name@example.com)");
        return "error";
      }

      // Check whether the given user already exists. If so, fail the request
      rs = stmt.executeQuery("SELECT * FROM users where email=" + "'" + user.getEmail() + "'");
      if (rs.next()) {
        model.put("message", "User Already Exists, Please Login");
        return "error";
      }
      /*
       * if (!rs.next()) { model.put("message", "You have Succesfully Signed Up");
       * return "success"; }
       */

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
  public String getLoginPage(@RequestParam("error") Optional<String> error, Map<String, Object> model) {
    if (error.isPresent()) {

      model.put("errorMessage", "Invalid e-mail or password.");
    }
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
  public String displayUser(Map<String, Object> model, @PathVariable String id) {
    User user = new User();
    ArrayList<Note> noteList = new ArrayList<Note>();
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

      rs = stmt.executeQuery("SELECT * FROM notes WHERE ownerId = " + id);
      while (rs.next()) {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setDateCreated(rs.getDate("dateCreated").toLocalDate());
        // Shared ids tbd
        note.setTitle(rs.getString("title"));
        note.setVideoId(rs.getString("videoId"));
        note.setContent(rs.getString("content"));
        noteList.add(note);
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    model.put("user", user);
    model.put("noteList", noteList);
    return "display_user";
  }

  @PostMapping(path = "/delete_account/{id}")
  public String deleteAccount(Map<String, Object> model, @PathVariable String id) {
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
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    return "redirect:/admin";
  }

  @GetMapping(path = "/userPage")
  public String UserDashboard(Map<String, Object> model) {
    User user = new User();
    model.put("user", user);
    return "userPage";
  }

  @PostMapping(path = "/delete_own_account")
  public String deleteOwnAccount(Map<String, Object> model) {

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      // Gets user currently logged in
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User curUser = (User) principal;

      stmt.executeUpdate("DELETE FROM users WHERE id = " + curUser.getId());

    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    return "redirect:/logout";
  }

  @PostMapping(path = "/delete_note/{pid}/{id}")
  public String AdminDeleteNote(Map<String, Object> model, @PathVariable String pid, @PathVariable String id) {

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      stmt.executeUpdate("DELETE FROM notes WHERE ownerId = " + pid + " AND id = " + id);
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    return ("redirect:/display_user/" + pid);
  }

  @PostMapping(path = "/delete_own_note/{id}")
  public String deleteOwnNote(Map<String, Object> model, @PathVariable String id) {

    // Gets user currently logged in
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User curUser = (User) principal;

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      stmt.executeUpdate("DELETE FROM notes WHERE ownerId = " + curUser.getId() + " AND id = " + id);
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    return ("redirect:/view_notes");
  }

  @PostMapping(path = "/share_note/{id}")
  public String shareNote(Map<String, Object> model, @PathVariable String id, User user,
      @RequestParam(value = "isEditable", required = false) Boolean isEditable) {

    System.out.println("share note" + id + " " + user.getEmail() + " " + isEditable);

    // Gets user currently logged in
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User curUser = (User) principal;

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    if (user.getEmail().equals(curUser.getEmail())) {
      model.put("message", "Cannot share notes with yourself");
      return "error";
    }

    try {

      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      rs = stmt.executeQuery("SELECT * FROM users where email='" + user.getEmail() + "'");
      if (!rs.next()) {
        model.put("message", "User not found");
        return "error";
      }

      Long shared_with_id = rs.getLong("id");

      rs2 = stmt.executeQuery("SELECT * FROM shares where shared_with_id = " + shared_with_id + " and noteId= " + id);
      if (rs2.next()) {
        model.put("message", "This note is already shared with this user");
        return "error";
      }

      stmt.executeUpdate("INSERT into shares (shared_with_id, noteId, is_editable) values (" + shared_with_id + "," + id
          + "," + isEditable + ")");
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
      Utils.DisposeDBHandles(connection, stmt, rs2);
    }
    return ("redirect:/view_notes");
  }

  @PostMapping("/unshare_from_me/{note_id}")
  public String unshareFromMe(Map<String, Object> model, @PathVariable String note_id) {
    // Gets user currently logged in
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User curUser = (User) principal;

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      stmt.executeUpdate("DELETE FROM shares WHERE noteId='" + Long.valueOf(note_id) + "' AND shared_with_id='" + curUser.getId() + "';");
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    return ("redirect:/view_notes");
  }

  @GetMapping(path = "/take_notes")
  public String getTakeNotes(Map<String, Object> model, HttpServletResponse response) {
    response.setHeader("Set-Cookie", "key=value;");
    response.setHeader("Set-Cookie", "SameSite=None;");
    response.setHeader("Set-Cookie", "Secure");
    Note note = new Note();
    model.put("note", note);
    return "take_notes";
  }

  @PostMapping(path = "/take_notes", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleTakeNotesSubmit(Map<String, Object> model, Note note) throws Exception {

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      // Set youtube ID from youtube URL
      String videoUrl = note.getVideoId();
      String actualVideoId = videoUrl.substring(videoUrl.indexOf("watch") + 8);
      actualVideoId = actualVideoId.substring(0, 11);
      note.setVideoId(actualVideoId);

      // Set other parameters
      note.setDateCreated(LocalDate.now());
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User curUser = (User) principal;
      note.setOwnerId(curUser.getId());

      // If note id is not null, update notes with id instead
      Long note_id = note.getId();
      String sql;
      if (note_id != 0) {
        sql = "UPDATE notes SET (videoId, title, content) = ('" + note.getVideoId() + "','" + note.getTitle() +
        "','" + note.getContent() + "') WHERE id='" + note.getId() + "';";
      } else {
        sql = "INSERT INTO notes(videoId, title, content, dateCreated, ownerId) VALUES ('" + note.getVideoId()
              + "','" + note.getTitle() + "','" + note.getContent() + "','" + note.getDateCreated() + "','"
              + note.getOwnerId() + "') ";
      }
      System.out.println(sql);
      // System.out.println(rawContent);
      stmt.executeUpdate(sql);
      return "redirect:/take_notes";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
  }

  @GetMapping(path = "/view_notes")
  public String viewNotes(Map<String, Object> model) {
    ArrayList<Note> noteList = new ArrayList<Note>();
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User curUser = (User) principal;

      // get notes owned by the logged in user
      rs = stmt.executeQuery("SELECT * FROM notes WHERE ownerId = " + curUser.getId() + " ORDER BY id ASC");
      while (rs.next()) {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setTitle(rs.getString("title"));
        note.setVideoId(rs.getString("videoId"));
        note.setDateCreated(rs.getDate("dateCreated").toLocalDate());
        note.setContent(rs.getString("content"));
        note.setisShared(false);
        note.setisEditable(true);
        // Shared ids tbd
        noteList.add(note);
      }
      // get notes shared with logged in user
      rs2 = stmt.executeQuery(
          "SELECT n.*, s.is_editable FROM shares s JOIN notes n ON s.noteId=n.id WHERE s.shared_with_id = "
              + curUser.getId() + "ORDER BY n.id ASC");
      while (rs2.next()) {
        Note note = new Note();
        note.setId(rs2.getLong("id"));
        note.setTitle(rs2.getString("title"));
        note.setVideoId(rs2.getString("videoId"));
        note.setDateCreated(rs2.getDate("dateCreated").toLocalDate());
        note.setContent(rs2.getString("content"));
        note.setisShared(true);
        note.setisEditable(rs2.getBoolean("is_editable"));
        // Shared ids tbd
        noteList.add(note);
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
      Utils.DisposeDBHandles(connection, stmt, rs2);
    }
    model.put("noteList", noteList);
    User user = new User();
    model.put("user", user);
    return "view_notes";
  }

  @PostMapping(path = "/view_content/{note_id}")
  public String viewContents(Map<String, Object> model, @PathVariable String note_id) {
    Note note = new Note();
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();

      rs = stmt.executeQuery("SELECT * FROM notes WHERE id = " + note_id);
      if (rs.next()) {
        note.setId(rs.getLong("id"));
        note.setTitle(rs.getString("title"));
        note.setVideoId(rs.getString("videoId"));
        note.setDateCreated(rs.getDate("dateCreated").toLocalDate());
        note.setContent(rs.getString("content"));
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    model.put("note", note);
    return "view_content";
  }

  @RequestMapping(
    path = "/edit_own_note/{note_id}",
    method = RequestMethod.POST
  )
  public RedirectView editOwnNotes(Map<String, Object> model, @PathVariable String note_id, RedirectAttributes redir) {

    Note note = new Note();
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    // Get notes saved by user
    try {      
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery("SELECT * FROM notes WHERE id = " + note_id);
      if (rs.next()) {
        note.setId(rs.getLong("id"));
        note.setTitle(rs.getString("title"));
        note.setVideoId(rs.getString("videoId"));
        note.setContent(rs.getString("content"));
      }
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return new RedirectView("error", true);
    } finally {
      Utils.DisposeDBHandles(connection, stmt, rs);
    }
    redir.addFlashAttribute("oldNote", note);
    return new RedirectView("/take_notes", true);
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