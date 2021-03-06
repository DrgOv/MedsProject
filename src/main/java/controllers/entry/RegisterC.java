package controllers.entry;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import misc.utility.security.BCrypt;
import misc.user.UserMisc;
import misc.utility.NodeMisc;
import misc.utility.ViewMisc;
import model.date.Date;
import model.date.month;
import model.other.ProblemTypes;
import model.roles.Doctor;
import model.roles.Patient;
import model.roles.Person;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterC implements Initializable{

    @FXML private TextField surname;
    @FXML private TextField forename;
    @FXML private TextField email;
    @FXML private TextField usernameTextField;
    @FXML private TextField addressTextField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<Integer> dayComboBox;
    @FXML private ComboBox<month> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> doctorSpecialtyComboBox;
    @FXML private Label checkFieldsLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureCBES();
        NodeMisc.hideNode(doctorSpecialtyComboBox, addressTextField, checkFieldsLabel);
    }

    private Person returnPerson() {
        String role = roleComboBox.getSelectionModel().getSelectedItem();
        if (role.equals("Patient")) {
            return new Patient(surname.getText(), forename.getText(), LocalDate.of(yearComboBox.getSelectionModel().getSelectedItem(), monthComboBox.getSelectionModel().getSelectedItem().getId(), dayComboBox.getSelectionModel().getSelectedItem()), email.getText(), usernameTextField.getText(), BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt(12)));
        }
        if (role.equals("Doctor")) {
            String specialty = doctorSpecialtyComboBox.getSelectionModel().getSelectedItem().toString();
            return new Doctor(surname.getText(), forename.getText(), LocalDate.of(yearComboBox.getSelectionModel().getSelectedItem(), monthComboBox.getSelectionModel().getSelectedItem().getId(), dayComboBox.getSelectionModel().getSelectedItem()), email.getText(), usernameTextField.getText(), BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt(12)), specialty, addressTextField.getText());

        }
        return null;
    }

    private void ymdSettings() {
        yearComboBox.setItems(Date.generateYears(1950));
        monthComboBox.setItems(Date.generateMonths());
        monthComboBox.setConverter(new StringConverter<month>() {
            @Override
            public String toString(month month) {
                return month.getMonthName();
            }

            @Override
            public month fromString(String s) {
                return monthComboBox.getValue();
            }
        });
        dayComboBox.setItems(Date.generateDays(31));
    }

    private void configureCBES() {
        ymdSettings();
        ObservableList<String> roles = FXCollections.observableArrayList("Patient", "Doctor");
        roleComboBox.setItems(roles);
        doctorSpecialtyComboBox.setItems(ProblemTypes.getPhysicalProblems());
    }

    private boolean checkFields() {
       return surname.getText().isEmpty() ||
               forename.getText().isEmpty() ||
               email.getText().isEmpty() ||
               !EmailValidator.getInstance().isValid(email.getText()) ||
               usernameTextField.getText().isEmpty() ||
               passwordField.getText().isEmpty() ||
               confirmPasswordField.getText().isEmpty() ||
               (!passwordField.getText().equals(confirmPasswordField.getText())) ||
               dayComboBox.getSelectionModel().isEmpty() ||
               monthComboBox.getSelectionModel().isEmpty() ||
               yearComboBox.getSelectionModel().isEmpty() ||
               roleComboBox.getSelectionModel().isEmpty() ||
               (roleComboBox.getSelectionModel().getSelectedItem().equals("Doctor") &&
                       doctorSpecialtyComboBox.getSelectionModel().isEmpty());

    }

    @FXML void roleComboBoxAction() {
        String role = roleComboBox.getSelectionModel().getSelectedItem();
        if (role.equals("Patient")) {
            NodeMisc.hideNode(doctorSpecialtyComboBox, addressTextField);
        }
        if (role.equals("Doctor")) {
            NodeMisc.showNode(doctorSpecialtyComboBox, addressTextField);
        }
    }

    @FXML void createAccountButtonAction(ActionEvent actionEvent) throws IOException {
        Person person = null;
        if (checkFields()) {
            NodeMisc.showNode(checkFieldsLabel);
        } else {
            person = returnPerson();
            UserMisc.addUser(person);
            UserMisc.writeUsers();
            ViewMisc.showLogin();
        }
    }

    @FXML void cancelButtonAction(ActionEvent actionEvent) {
        ViewMisc.showLogin();
    }

}
