package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormController {

    @FXML
    public void handleTeacherwiseTimetable(ActionEvent event) {
        openForm("Teacherwise.fxml", "Teacherwise Timetable");
    }

    @FXML
    public void handleStudentwiseTimetable(ActionEvent event) {
        openForm("Student.fxml", "Studentwise Timetable");
    }

    @FXML
    public void handleSectionwiseTimetable(ActionEvent event) {
        openForm("SectionwiseFX.fxml", "Sectionwise Timetable");
    }

    @FXML
    public void handleRoomArranger(ActionEvent event) {
        openForm("RoomArranger.fxml", "Room Arranger");
    }

    @FXML
    public void handleClassroomwiseTimetable(ActionEvent event) {
        openForm("ClassroomwiseFX.fxml", "Classroomwise Timetable");
    }

    private void openForm(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
