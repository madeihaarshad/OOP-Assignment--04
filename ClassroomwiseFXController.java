package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassroomwiseFXController {
    @FXML
    private ListView<String> listBoxSections;
    @FXML
    private ListView<String> listBoxDetails;
    @FXML
    private TextField textBoxRoom;
    @FXML
    private TextField textBoxTeacherName;
    @FXML
    private TextField textBoxCourse;
    @FXML
    private TextField textBoxDay;
    @FXML
    private TextField textBoxStartTime;
    @FXML
    private TextField textBoxEndTime;
    @FXML
    private TextField textBoxSection;
    @FXML
    private Button showDataButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    public void initialize() {
        showDataButton.setOnAction(this::displayClassroomwiseFXData);
        addButton.setOnAction(this::addRoom);
        deleteButton.setOnAction(this::deleteRoom);

        listBoxSections.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    displayClassroomwiseFXDetails(newValue);
                }
            }
        });

        displayClassroomwiseFXData(null);
    }

    @FXML
    private void displayClassroomwiseFXData(ActionEvent event) {
        listBoxSections.getItems().clear();

        String query = "SELECT DISTINCT room FROM USER4";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listBoxSections.getItems().add(resultSet.getString("Room"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error fetching data: " + ex.getMessage());
        }
    }

    @FXML
    private void displayClassroomwiseFXDetails(String room) {
        listBoxDetails.getItems().clear();

        String query = "SELECT * FROM USER4 WHERE room = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, room);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Display details in listBoxDetails
                    String details = "Room: " + resultSet.getString("Room") +
                            ", Teacher: " + resultSet.getString("TeacherName") +
                            ", Course: " + resultSet.getString("Course") +
                            ", Day: " + resultSet.getString("Day") +
                            ", Start Time: " + resultSet.getString("StartTime") +
                            ", End Time: " + resultSet.getString("EndTime");
                    listBoxDetails.getItems().add(details);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error fetching details: " + ex.getMessage());
        }
    }

    @FXML
    private void addRoom(ActionEvent event) {
        String newRoom = textBoxRoom.getText().trim();
        String teacher = textBoxTeacherName.getText().trim();
        String course = textBoxCourse.getText().trim();
        String day = textBoxDay.getText().trim();
        String startTime = textBoxStartTime.getText().trim();
        String endTime = textBoxEndTime.getText().trim();
        String section = textBoxSection.getText().trim();

        if (newRoom.isEmpty() || teacher.isEmpty() || course.isEmpty() || day.isEmpty() ||
                startTime.isEmpty() || endTime.isEmpty() || section.isEmpty()) {
            // Handle empty fields
            displayWarning("Error", "All fields are required.");
            return;
        }

        // Check if the room is available
        if (!isRoomAvailable(newRoom, day, startTime, endTime)) {
            // Room is not available at the specified time
            // Display occupied time slots and teacher's name
            String occupiedSlots = getOccupiedTimeSlots(newRoom, day);
            String message = "Room is not available at this time. Occupied by " + occupiedSlots;
            displayWarning("Room Unavailable", message);
            return;
        }

        // Room is available, proceed with insertion
        String query = "INSERT INTO USER4 (Room, TeacherName, Course, Day, StartTime, EndTime, Section) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newRoom);
            statement.setString(2, teacher);
            statement.setString(3, course);
            statement.setString(4, day);
            statement.setString(5, startTime);
            statement.setString(6, endTime);
            statement.setString(7, section);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                displayClassroomwiseFXData(null);
                clearTextFields(); // Clear text fields after successful insertion
            } else {
                displayWarning("Error", "Failed to add room.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            displayWarning("Error", "An error occurred while adding room.");
        }
    }

    private void displayWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isRoomAvailable(String room, String day, String startTime, String endTime) {
        String query = "SELECT * FROM USER4 WHERE Room = ? AND Day = ? AND ((StartTime <= ? AND EndTime >= ?) OR (StartTime <= ? AND EndTime >= ?))";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, room);
            statement.setString(2, day);
            statement.setString(3, startTime);
            statement.setString(4, startTime);
            statement.setString(5, endTime);
            statement.setString(6, endTime);

            try (ResultSet resultSet = statement.executeQuery()) {
                // If there are any overlapping bookings, the room is not available
                return !resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Error occurred or no result
        return false;
    }

    private String getOccupiedTimeSlots(String room, String day) {
        String query = "SELECT TeacherName, StartTime, EndTime FROM USER4 WHERE Room = ? AND Day = ?";

        StringBuilder occupiedSlots = new StringBuilder();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, room);
            statement.setString(2, day);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String teacherName = resultSet.getString("TeacherName");
                    String startTime = resultSet.getString("StartTime");
                    String endTime = resultSet.getString("EndTime");
                    occupiedSlots.append("Teacher: ").append(teacherName)
                                .append(", Time: ").append(startTime)
                                .append(" - ").append(endTime).append("; ");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return occupiedSlots.toString();
    }

    private void clearTextFields() {
        textBoxRoom.clear();
        textBoxTeacherName.clear();
        textBoxCourse.clear();
        textBoxDay.clear();
        textBoxStartTime.clear();
        textBoxEndTime.clear();
        textBoxSection.clear();
    }


    @FXML
    private void deleteRoom(ActionEvent event) {
        String selectedRoom = listBoxSections.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            // Handle no selection
            return;
        }

        String query = "DELETE FROM USER4 WHERE Room = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, selectedRoom);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                displayClassroomwiseFXData(null);
            } else {
                // Handle deletion failure
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}