package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeacherwiseController {

    @FXML
    private ListView<String> listViewTeachers;
    @FXML
    private TextField textFieldTeacherName;
    @FXML
    private TextField textFieldCourse;
    @FXML
    private TextField textFieldRoom;
    @FXML
    private TextField textFieldDay;
    @FXML
    private TextField textFieldStartTime;
    @FXML
    private TextField textFieldEndTime;
    @FXML
    private TextField textFieldSection;
    @FXML
    private TextField textFieldName;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    private static final Logger LOGGER = Logger.getLogger(TeacherwiseController.class.getName());

    @FXML
    private void showData() {
        String name = textFieldName.getText();
        if (!name.isEmpty()) {
            displayTimetableForTeacher(name);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Please enter a Teacher's name.");
        }
    }

    private void displayTimetableForTeacher(String TeacherName) {
        listViewTeachers.getItems().clear();

        
        try (Connection connection = databaseConnection.getConnection()) {
        	String query = "SELECT * FROM USER4 WHERE TeacherName = ?";

        	PreparedStatement preparedStatement = connection.prepareStatement(query);
        	preparedStatement.setString(1, TeacherName);
        	 ResultSet resultSet = preparedStatement.executeQuery();

            
           

            if (resultSet.next()) {
                do {
                    String classInfo = String.format("Course: %s, Room: %s, Day: %s, StartTime: %s, EndTime: %s, Section: %s",
                            resultSet.getString("Course"), resultSet.getString("Room"), resultSet.getString("Day"),
                            resultSet.getString("StartTime"), resultSet.getString("EndTime"), resultSet.getString("Section"));
                    listViewTeachers.getItems().add(classInfo);
                } while (resultSet.next());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No schedule found for the specified teacher.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: ", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void addData() {
        String name = textFieldName.getText();
        String teacherName = textFieldTeacherName.getText();
        String course = textFieldCourse.getText();
        String room = textFieldRoom.getText();
        String day = textFieldDay.getText();
        String startTime = textFieldStartTime.getText();
        String endTime = textFieldEndTime.getText();
        String section = textFieldSection.getText();

        if (!name.isEmpty() && !teacherName.isEmpty() && !course.isEmpty() && !room.isEmpty() &&
                !day.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty() && !section.isEmpty()) {
            addTimetableForStudent(name, teacherName, course, room, day, startTime, endTime, section);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Please fill in all fields.");
        }
    }

    private void addTimetableForStudent(String name, String teacherName, String course, String room, String day,
                                        String startTime, String endTime, String section) {

        

        try (Connection connection = databaseConnection.getConnection()) {
        	

        	String query = "INSERT INTO USER4 (Name, TeacherName, Course, Room, Day, StartTime, EndTime, Section) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        
             PreparedStatement preparedStatement = connection.prepareStatement(query); 

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, teacherName);
            preparedStatement.setString(3, course);
            preparedStatement.setString(4, room);
            preparedStatement.setString(5, day);
            preparedStatement.setString(6, startTime);
            preparedStatement.setString(7, endTime);
            preparedStatement.setString(8, section);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Record added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the record.");
            }

            displayTimetableForTeacher(name);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: ", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void deleteData() {
        String name = textFieldName.getText();
        if (!name.isEmpty()) {
            deleteTimetableForTeacher(name);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Please enter a Teacher's name.");
        }
    }

    private void deleteTimetableForTeacher(String name) {
        

        try (Connection connection = databaseConnection.getConnection()) {
        	String query = "DELETE FROM USER4 WHERE Name = ?";

             PreparedStatement preparedStatement = connection.prepareStatement(query);
             

            preparedStatement.setString(1, name);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Records deleted successfully.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No records found for the specified teacher.");
            }

            listViewTeachers.getItems().clear();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: ", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}