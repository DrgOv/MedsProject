package controllers.account.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import misc.user.UserMisc;
import misc.utility.NodeMisc;
import misc.utility.TextMisc;
import misc.utility.ViewMisc;
import model.combobox.LogOut;
import model.combobox.Menu;
import model.combobox.Option;
import misc.utility.security.BCrypt;
import model.roles.Person;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

abstract public class AccSettingsC implements Initializable {

    @FXML protected TextField emailTextField;
    @FXML protected TextField usernameTextField;
    @FXML protected TextField roleTextField;
    @FXML protected PasswordField oldPasswordField;
    @FXML protected PasswordField newPasswordField;
    @FXML protected PasswordField confirmPasswordField;
    @FXML protected Label badOldPassword;
    @FXML protected Label badNewPassword;
    @FXML protected ComboBox<Option> optionsCB;
    @FXML protected Hyperlink forgotPasswordHL;

    Person loggedUser = UserMisc.getLoggedUser();
    protected ObservableList<Option> options = FXCollections.observableArrayList(new LogOut(), new Menu());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void configureAccountTab() {
        emailTextField.setText(loggedUser.getEmail());
        usernameTextField.setText(loggedUser.getUsername());
        roleTextField.setText(loggedUser.getRole());
    }

    protected void configureCB() {
        TextMisc.textBind(optionsCB.promptTextProperty(), loggedUser.usernameProperty());
        optionsCB.setItems(options);
    }

    @FXML void optionsCBAction() {
        optionsCB.getSelectionModel().getSelectedItem().action(loggedUser);
    }

    @FXML void editEmailButtonAction(ActionEvent event) throws IOException {
        NodeMisc.enableNode(emailTextField);
    }

    @FXML void editUsernameButtonAction(ActionEvent event) {
        NodeMisc.enableNode(usernameTextField);
    }

    @FXML void updateAccDetailsButtonAction(ActionEvent actionEvent) throws IOException {
        loggedUser.setEmail(emailTextField.getText());
        loggedUser.setUsername(usernameTextField.getText());
    }

    @FXML void changePasswordButtonAction(ActionEvent event) throws IOException {
        String oldPW = oldPasswordField.getText();
        String newPW = newPasswordField.getText();
        String confirmNewPW = confirmPasswordField.getText();
        if (!BCrypt.checkpw(oldPW, loggedUser.getPassword())) {
            NodeMisc.showNode(badOldPassword);
        }
        if (!newPW.equals(confirmNewPW)) {
            NodeMisc.showNode(badNewPassword);
        }
        if (BCrypt.checkpw(oldPW, loggedUser.getPassword())) {
            NodeMisc.hideNode(badOldPassword);
        }
        if (newPW.equals(confirmNewPW)) {
            NodeMisc.hideNode(badNewPassword);
        }
        if (BCrypt.checkpw(oldPW, loggedUser.getPassword()) && newPW.equals(confirmNewPW) && !newPW.isEmpty() && !confirmNewPW.isEmpty()) {
            loggedUser.setPassword(BCrypt.hashpw(newPW, BCrypt.gensalt(12)));
            UserMisc.updateUsers(loggedUser);
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            NodeMisc.hideNode(badOldPassword, badNewPassword);
            ViewMisc.showStage("/view/entry/loginView.fxml");
            UserMisc.setLoggedUser(null);
        }
    }

    @FXML void forgotPasswordHLAction(ActionEvent event) {
    }

}
