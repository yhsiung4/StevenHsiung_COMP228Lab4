package org.example;

import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:students.db")) {
            initializeDatabase(connection);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Add a new student record");
                System.out.println("2. List All the students by Major");
                System.out.println("3. List all the students");
                System.out.println("4. Delete a student by ID");
                System.out.println("5. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addStudent(connection, scanner);
                        break;
                    case 2:
                        listStudentsByMajor(connection, scanner);
                        break;
                    case 3:
                        listAllStudents(connection);
                        break;
                    case 4:
                        deleteStudentById(connection, scanner);
                        break;
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create the student table if it doesn't exist
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS student (ID VARCHAR(36) PRIMARY KEY, NAME VARCHAR(50), MAJOR VARCHAR(50))"
            );
        }
    }

    private static void addStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        System.out.print("Enter student major: ");
        String major = scanner.nextLine();

        String id = UUID.randomUUID().toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO student (ID, NAME, MAJOR) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, major);

            preparedStatement.executeUpdate();
            System.out.println("Student added successfully.");
        }
    }

    private static void listStudentsByMajor(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter major to filter by: ");
        String major = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM student WHERE MAJOR = ?")) {
            preparedStatement.setString(1, major);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getString("ID"));
                System.out.println("Name: " + resultSet.getString("NAME"));
                System.out.println("Major: " + resultSet.getString("MAJOR"));
                System.out.println("--------------");
            }
        }
    }

    private static void listAllStudents(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM student");

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getString("ID"));
                System.out.println("Name: " + resultSet.getString("NAME"));
                System.out.println("Major: " + resultSet.getString("MAJOR"));
                System.out.println("--------------");
            }
        }
    }
    private static void deleteStudentById(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter ID of student to delete: ");
        String id = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM student WHERE ID = ?")) {
            preparedStatement.setString(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student with ID " + id + " not found.");
            }
        }
    }
}