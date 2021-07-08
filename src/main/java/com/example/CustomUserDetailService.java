package com.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSourceAuth;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("login request for: " + username);

        User user = new User();

        try (Connection connection = dataSourceAuth.getConnection()) {
            Statement stmt = connection.createStatement();

            // Checks if the given email and hashed password combination is in the table
            ResultSet rs = stmt.executeQuery("SELECT * FROM users where email=" + "'" + username + "'");
            if (!rs.next()) {
                throw new UsernameNotFoundException("Could not find the user");
            }

            // Construct the user object from the database
            user.setId(rs.getInt("id"));
            user.setFname(rs.getString("fname"));
            user.setLname(rs.getString("lname"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setType(rs.getString("type"));

        } catch (Exception e) {
            throw new UsernameNotFoundException("Could not find the user");
        }

        return user;
    }

    @Bean
    public DataSource dataSourceAuth() throws SQLException {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }
}