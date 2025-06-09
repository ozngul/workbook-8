package com.pluralsight;

import java.sql.*;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/northwind";
        String username = "root";
        String password = "password"; // buraya kendi şifreni yaz

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Veritabanına başarıyla bağlanıldı!");

            String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("\n--- Northwind Ürün Listesi (Stacked) ---");

            while (resultSet.next()) {
                int id = resultSet.getInt("ProductID");
                String name = resultSet.getString("ProductName");
                double price = resultSet.getDouble("UnitPrice");
                int stock = resultSet.getInt("UnitsInStock");

                System.out.println("Product Id: " + id);
                System.out.println("Name: " + name);
                System.out.println("Price: " + price);
                System.out.println("Stock: " + stock);
                System.out.println("------------------");
            }



            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Veritabanı bağlantı hatası!");
            e.printStackTrace();
        }
    }
}
