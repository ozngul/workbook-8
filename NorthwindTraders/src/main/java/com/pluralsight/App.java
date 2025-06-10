package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/northwind";
        String username = "root";
        String password = "ozancan261"; // kendi şifreni yaz

        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = DriverManager.getConnection(url, username, password);
            boolean running = true;

            while (running) {
                System.out.println("\n--- Northwind Ana Menü ---");
                System.out.println("1) Display all products");
                System.out.println("2) Display all customers");
                System.out.println("3) Display all categories");
                System.out.println("0) Exit");
                System.out.print("Select an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        displayProducts(connection);
                        break;
                    case "2":
                        displayCustomers(connection);
                        break;
                    case "3":
                        displayCategoriesAndProductsByCategory(connection, scanner);
                        break;
                    case "0":
                        running = false;
                        System.out.println("Çıkılıyor...");
                        break;
                    default:
                        System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Veritabanı hatası!");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 1) Ürünleri listele
    private static void displayProducts(Connection conn) {
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("%-5s %-30s %-10s %-10s\n", "ID", "Ürün Adı", "Fiyat", "Stok");
            System.out.println("---------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("ProductName");
                double price = rs.getDouble("UnitPrice");
                int stock = rs.getInt("UnitsInStock");

                System.out.printf("%-5d %-30s %-10.2f %-10d\n", id, name, price, stock);
            }

        } catch (SQLException e) {
            System.out.println("Ürünler alınırken hata oluştu.");
            e.printStackTrace();
        }
    }

    // 2) Müşterileri listele
    private static void displayCustomers(Connection conn) {
        String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("%-25s %-30s %-15s %-15s %-15s\n",
                    "Contact Name", "Company", "City", "Country", "Phone");
            System.out.println("--------------------------------------------------------------------------------------");

            while (rs.next()) {
                String contact = rs.getString("ContactName");
                String company = rs.getString("CompanyName");
                String city = rs.getString("City");
                String country = rs.getString("Country");
                String phone = rs.getString("Phone");

                System.out.printf("%-25s %-30s %-15s %-15s %-15s\n",
                        contact, company, city, country, phone);
            }

        } catch (SQLException e) {
            System.out.println("Müşteriler alınırken hata oluştu.");
            e.printStackTrace();
        }
    }

    // 3) Kategorileri ve seçilen kategoriye göre ürünleri listele
    private static void displayCategoriesAndProductsByCategory(Connection conn, Scanner scanner) {
        String categoryQuery = "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(categoryQuery)) {

            System.out.println("\n--- Kategoriler ---");
            System.out.printf("%-5s %-30s\n", "ID", "Kategori Adı");
            System.out.println("---------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("CategoryID");
                String name = rs.getString("CategoryName");
                System.out.printf("%-5d %-30s\n", id, name);
            }

        } catch (SQLException e) {
            System.out.println("Kategoriler alınırken hata oluştu.");
            e.printStackTrace();
            return;
        }

        System.out.print("\nLütfen bir kategori ID girin: ");
        int selectedCategoryId;
        try {
            selectedCategoryId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Geçersiz sayı girdiniz.");
            return;
        }

        String productQuery = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock " +
                "FROM Products WHERE CategoryID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(productQuery)) {
            pstmt.setInt(1, selectedCategoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n--- Seçilen Kategorideki Ürünler ---");
                System.out.printf("%-5s %-30s %-10s %-10s\n", "ID", "Ürün Adı", "Fiyat", "Stok");
                System.out.println("----------------------------------------------------------");

                boolean foundAny = false;
                while (rs.next()) {
                    foundAny = true;
                    int id = rs.getInt("ProductID");
                    String name = rs.getString("ProductName");
                    double price = rs.getDouble("UnitPrice");
                    int stock = rs.getInt("UnitsInStock");

                    System.out.printf("%-5d %-30s %-10.2f %-10d\n", id, name, price, stock);
                }

                if (!foundAny) {
                    System.out.println("Bu kategoriye ait ürün bulunamadı.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ürünler alınırken hata oluştu.");
            e.printStackTrace();
        }
    }
}
