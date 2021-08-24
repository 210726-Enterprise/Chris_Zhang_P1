package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;

    /**
     * Default constructor connects to my local database.
     */
    public ConnectionFactory(){
        url = System.getenv("db_url");
        username = System.getenv("db_name");
        password = System.getenv("db_password");
    }

    /**
     * Takes in a parameters from a config that will connect to a database of choice.
     * @param cfg
     */
    public ConnectionFactory(Config cfg){
        url = cfg.getUrl();
        username = cfg.getUsername();
        password = cfg.getPassword();
    }

    public static synchronized Connection getConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
