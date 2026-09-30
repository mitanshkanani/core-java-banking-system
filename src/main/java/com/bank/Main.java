package com.bank;

import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("  Core Java Banking System — Stage 1");
        System.out.println("=========================================");

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connected to PostgreSQL successfully!");
                System.out.println("   Database : " + conn.getMetaData().getDatabaseProductName()
                                              + " " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("   URL      : " + conn.getMetaData().getURL());
                System.out.println("   User     : " + conn.getMetaData().getUserName());
            }

        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=========================================");
        System.out.println("  Stage 1 complete.");
        System.out.println("=========================================");
    }
}