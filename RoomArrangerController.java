package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomArrangerController {

    @FXML
    private TextField textBoxCourse1;

    @FXML
    private TextField textBoxCourse2;

    @FXML
    private Button swapButton;

    @FXML
    private Label labelRoomData;

    @FXML
    private void initialize() {
        displayRoomDataFromDB();
    }

    @FXML
    private void handleSwapButton(ActionEvent event) {
        String courseName1 = textBoxCourse1.getText();
        String courseName2 = textBoxCourse2.getText();
        swapRooms(courseName1, courseName2);
    }

    private void displayRoomDataFromDB() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT Name, Room, GROUP_CONCAT(CONCAT(Course, ' - ', TeacherName, ' - ', Day, ' ', StartTime, '-', EndTime) ORDER BY Course SEPARATOR '; ') AS Schedule FROM USER4 GROUP BY Name, Room";
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder roomData = new StringBuilder();
            while (rs.next()) {
                String name = rs.getString("Name");
                String room = rs.getString("Room");
                String schedule = rs.getString("Schedule");
                roomData.append("Name: ").append(name).append(", Room: ").append(room).append(", Schedule: ").append(schedule).append("\n");
            }
            rs.close();
            stmt.close();
            labelRoomData.setText(roomData.toString());
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


    private void swapRooms(String courseName1, String courseName2) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection()) {
            String roomDetailsBefore = fetchRoomDetails(conn, courseName1, courseName2);

            String room1 = getRoomForCourse(conn, courseName1);
            String room2 = getRoomForCourse(conn, courseName2);

            String swapQuery1 = "UPDATE USER4 SET Room = '" + room2 + "' WHERE Course = '" + courseName1 + "'";
            String swapQuery2 = "UPDATE USER4 SET Room = '" + room1 + "' WHERE Course = '" + courseName2 + "'";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(swapQuery1);
                stmt.executeUpdate(swapQuery2);
            }

            String roomDetailsAfter = fetchRoomDetails(conn, courseName1, courseName2);

            String message = "Room details before swapping:\n" + roomDetailsBefore + "\n\nRoom details after swapping:\n" + roomDetailsAfter;

            displayRoomDataFromDB();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private String fetchRoomDetails(Connection conn, String courseName1, String courseName2) throws SQLException {
        StringBuilder roomDetails = new StringBuilder();
        String query = "SELECT * FROM USER4 WHERE Course IN ('" + courseName1 + "', '" + courseName2 + "')";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("Name");
                String course = rs.getString("Course");
                String teacher = rs.getString("TeacherName");
                String day = rs.getString("Day");
                String startTime = rs.getString("StartTime");
                String endTime = rs.getString("EndTime");
                String room = rs.getString("Room");
                roomDetails.append("Name: ").append(name).append(", Course: ").append(course).append(", Teacher: ").append(teacher)
                        .append(", Day: ").append(day).append(", Time: ").append(startTime).append(" - ").append(endTime)
                        .append(", Room: ").append(room).append("\n");
            }
        }
        return roomDetails.toString();
    }

    private String getRoomForCourse(Connection conn, String courseName) throws SQLException {
        String room = "";
        String query = "SELECT Room FROM USER4 WHERE Course = '" + courseName + "'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                room = rs.getString("Room");
            }
        }
        return room;
    }
}
