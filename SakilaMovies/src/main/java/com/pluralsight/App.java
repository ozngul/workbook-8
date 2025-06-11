package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        BasicDataSource dataSource = getDataSource();

        try (Connection conn = dataSource.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            // 1. Kullanıcıdan soyad al
            System.out.print("Enter actor's last name: ");
            String lastName = scanner.nextLine();

            String findActorsQuery = "SELECT actor_id, first_name, last_name FROM actor WHERE last_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(findActorsQuery)) {
                stmt.setString(1, lastName.toUpperCase());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\nYour matches are:\n");
                        do {
                            int actorId = rs.getInt("actor_id");
                            String first = rs.getString("first_name");
                            String last = rs.getString("last_name");
                            System.out.printf("%d - %s %s\n", actorId, first, last);
                        } while (rs.next());
                    } else {
                        System.out.println("No matches!");
                        return;
                    }
                }
            }

            // 2. Kullanıcıdan actor ID al (güvenli şekilde)
            System.out.print("\nEnter actor ID to view their movies: ");
            int actorId;
            try {
                actorId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                return;
            }

            // 3. Actor ID gerçekten var mı kontrol et
            String checkActorQuery = "SELECT COUNT(*) FROM actor WHERE actor_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkActorQuery)) {
                stmt.setInt(1, actorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        System.out.println("No actor found with ID: " + actorId);
                        return;
                    }
                }
            }

            // 4. Film bilgilerini getir ve yazdır
            String getFilms = """
                SELECT f.film_id, f.title, f.description, f.release_year, f.length
                FROM film f
                JOIN film_actor fa ON f.film_id = fa.film_id
                WHERE fa.actor_id = ?
                ORDER BY f.title
                """;

            try (PreparedStatement stmt = conn.prepareStatement(getFilms)) {
                stmt.setInt(1, actorId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\nFilms featuring this actor:");
                        do {
                            int filmId = rs.getInt("film_id");
                            String title = rs.getString("title");
                            String description = rs.getString("description");
                            int releaseYear = rs.getInt("release_year");
                            int length = rs.getInt("length");

                            System.out.println("filmId: " + filmId);
                            System.out.println("title: " + title);
                            System.out.println("description: " + description);
                            System.out.println("releaseYear: " + releaseYear);
                            System.out.println("length: " + length);
                            System.out.println("------------------------");
                        } while (rs.next());
                    } else {
                        System.out.println("This actor has no films listed.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  DataSource ayarı
    private static BasicDataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/sakila");
        ds.setUsername("root");
        ds.setPassword("ozancan261"); // buraya kendi şifreni yaz
        return ds;
    }
}
