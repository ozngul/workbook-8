package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        BasicDataSource dataSource = getDataSource();

        try (Connection conn = dataSource.getConnection();
             Scanner scanner = new Scanner(System.in)) {

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

            System.out.print("\nEnter actor's first name: ");
            String firstName = scanner.nextLine();

            String getActorId = "SELECT actor_id FROM actor WHERE first_name = ? AND last_name = ?";
            int actorId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(getActorId)) {
                stmt.setString(1, firstName.toUpperCase());
                stmt.setString(2, lastName.toUpperCase());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        actorId = rs.getInt("actor_id");
                    } else {
                        System.out.println("No actor found with that full name.");
                        return;
                    }
                }
            }

            String getFilms = """
                SELECT f.title
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
                            System.out.println("- " + rs.getString("title"));
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

    private static BasicDataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/sakila");
        ds.setUsername("root");
        ds.setPassword("ozancan261"); // kendi şifreni buraya yaz
        return ds;
    }
}
