import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;

public class UserFormApp extends Application {

    private TextField fullNameField;
    private TextField idField;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private TextField homeProvinceField;
    private DatePicker dobPicker;
    private File dataFile = new File("records.txt");

    @Override
    public void start(Stage primaryStage) {
        // Main GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Labels and Input Fields
        Label fullNameLabel = new Label("FullName:");
        fullNameField = new TextField();

        Label idLabel = new Label("ID:");
        idField = new TextField();

        Label genderLabel = new Label("Gender:");
        maleRadio = new RadioButton("Male");
        femaleRadio = new RadioButton("Female");
        ToggleGroup genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);

        Label homeProvinceLabel = new Label("HomeProvince:");
        homeProvinceField = new TextField();

        Label dobLabel = new Label("DOB:");
        dobPicker = new DatePicker();

        // Buttons
        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");
        Button restoreButton = new Button("Restore");
        Button findPrevButton = new Button("Find Prev");
        Button findNextButton = new Button("Find Next");
        Button criteriaButton = new Button("Criteria");
        Button closeButton = new Button("Close");

        // Add Components to GridPane
        gridPane.add(fullNameLabel, 0, 0);
        gridPane.add(fullNameField, 1, 0);

        gridPane.add(idLabel, 0, 1);
        gridPane.add(idField, 1, 1);

        gridPane.add(genderLabel, 0, 2);
        HBox genderBox = new HBox(10, maleRadio, femaleRadio);
        gridPane.add(genderBox, 1, 2);

        gridPane.add(homeProvinceLabel, 0, 3);
        gridPane.add(homeProvinceField, 1, 3);

        gridPane.add(dobLabel, 0, 4);
        gridPane.add(dobPicker, 1, 4);

        // Add Buttons to a separate VBox
        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(newButton, deleteButton, restoreButton, findPrevButton, findNextButton, criteriaButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        // Add Button Box and GridPane to BorderPane
        BorderPane root = new BorderPane();
        root.setLeft(gridPane);
        root.setRight(buttonBox);
        BorderPane.setMargin(buttonBox, new Insets(20));

        // Button Actions
        newButton.setOnAction(e -> saveRecord());
        findNextButton.setOnAction(e -> findNextRecord());
        closeButton.setOnAction(e -> primaryStage.close());

        // Scene and Stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("User Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveRecord() {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");
        String homeProvince = homeProvinceField.getText();
        LocalDate dob = dobPicker.getValue();

        if (fullName.isEmpty() || id.isEmpty() || gender.isEmpty() || homeProvince.isEmpty() || dob == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
            return;
        }

        try (FileWriter writer = new FileWriter(dataFile, true)) {
            writer.write(fullName + "," + id + "," + gender + "," + homeProvince + "," + dob + "\n");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Record saved successfully!");
            clearForm();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Unable to save record!");
        }
    }

    private void findNextRecord() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String record;
            if ((record = reader.readLine()) != null) {
                String[] fields = record.split(",");
                if (fields.length == 5) {
                    fullNameField.setText(fields[0]);
                    idField.setText(fields[1]);
                    if (fields[2].equals("Male")) {
                        maleRadio.setSelected(true);
                    } else if (fields[2].equals("Female")) {
                        femaleRadio.setSelected(true);
                    }
                    homeProvinceField.setText(fields[3]);
                    dobPicker.setValue(LocalDate.parse(fields[4]));
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "End of Records", "No more records found!");
            }
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Unable to read records!");
        }
    }

    private void clearForm() {
        fullNameField.clear();
        idField.clear();
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        homeProvinceField.clear();
        dobPicker.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}