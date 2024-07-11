package com.perunovpavel.util;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL_KEY = "db.url";

    @SneakyThrows
    public static Connection open() {

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(PropertiesUtil.getProperty(URL_KEY));
        } catch (SQLException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
