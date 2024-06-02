package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionwiseFXController {
    @FXML
    private ListView<String> listBoxSections;
    @FXML
    private ListView<String> listBoxDetails;
    @FXML
    private Button showDataButton;
    @FXML
    private TextField textBoxNewSection;
    @FXML
    private TextField textBoxTeacher;
    @FXML
    private TextField textBoxRoom;
    @FXML
    private TextField textBoxCourse;
    @FXML
    private TextField textBoxDay;
    @FXML
    private TextField textBoxStartTime;
    @FXML
    private TextField textBoxEndTime;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    public void initialize() {
        showDataButton.setOnAction(this::displaySectionwiseData);
        addButton.setOnAction(this::addSection);
        deleteButton.setOnAction(this::deleteSection);

        listBoxSections.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    displaySectionDetails(newValue);
                }
            }
        });

        displaySectionwiseData(null);
    }

    @FXML
    private void displaySectionwiseData(ActionEvent event) {
        listBoxSections.getItems().clear();

        String query = "SELECT DISTINCT Section FROM USER4";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listBoxSections.getItems().add(resultSet.getString("Section"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error fetching data: " + ex.getMessage());
        }
    }

    @FXML
    private void displaySectionDetails(String section) {
        listBoxDetails.getItems().clear();

        String query = "SELECT * FROM USER4 WHERE Section = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, section);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Display details in listBoxDetails
                    String details = "Teacher: " + resultSet.getString("TeacherName") +
                            ", Room: " + resultSet.getString("Room") +
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
    private void addSection(ActionEvent event) {
        String newSection = textBoxNewSection.getText().trim();
        String teacher = textBoxTeacher.getText().trim();
        String room = textBoxRoom.getText().trim();
        String course = textBoxCourse.getText().trim();
        String day = textBoxDay.getText().trim();
        String startTime = textBoxStartTime.getText().trim();
        String endTime = textBoxEndTime.getText().trim();

        if (newSection.isEmpty() || teacher.isEmpty() || room.isEmpty() || course.isEmpty() || day.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            // Handle empty fields
            return;
        }

        String query = "INSERT INTO USER4 (Section, TeacherName, Room, Course, Day, StartTime, EndTime) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newSection);
            statement.setString(2, teacher);
            statement.setString(3, room);
            statement.setString(4, course);
            statement.setString(5, day);
            statement.setString(6, startTime);
            statement.setString(7, endTime);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                displaySectionwiseData(null);
            } else {
                // Handle insertion failure
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void deleteSection(ActionEvent event) {
        String selectedSection = listBoxSections.getSelectionModel().getSelectedItem();
        if (selectedSection == null) {
            // Handle no selection
            return;
        }

        String query = "DELETE FROM USER4 WHERE Section = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, selectedSection);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                displaySectionwiseData(null);
            } else {
                // Handle deletion failure
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
