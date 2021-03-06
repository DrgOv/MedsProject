package controllers.other;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import misc.user.UserMisc;
import misc.utility.NodeMisc;
import misc.utility.ViewMisc;
import misc.utility.security.BCrypt;
import misc.utility.security.SecurityMisc;
import model.roles.Person;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordResetC implements Initializable {

    @FXML private Label badCredentials;
    @FXML private TextField usernameTextField;
    @FXML private TextField emailTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void checkCredentialsResetPassword() throws IOException {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        Person person = null;
        boolean userExists = false;
        for (Person x: UserMisc.getUsers()) {
            if (username.equals(x.getUsername()) && x.getEmail().equals(email)) {
                userExists = true;
                person = x;
                break;
            }
        }
        if (userExists == true) {
            person.setPassword(BCrypt.hashpw(SecurityMisc.sendMailFromTo("meds.app0@gmail.com", "8L111119meds", person.getEmail()), BCrypt.gensalt(12)));
            UserMisc.updateUsers(person);
            ViewMisc.showStage("/view/entry/loginView.fxml");
        }
        else {
            NodeMisc.showNode(badCredentials);
        }
    }

    @FXML void cancelAction(ActionEvent event) {
        ViewMisc.showLogin();
    }

    @FXML void resetPWButtonAction(ActionEvent event) throws IOException {
        checkCredentialsResetPassword();
    }

}
