package client.view;

import client.logic.Administration;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class LoginScreen extends Application
{
    private Administration administration;

    private Stage stage;
    private GridPane gridPane;

    private Label usernameText;
    private TextField usernameField;
    private Label passwordText;
    private TextField passwordField;
    private Button registerButton;
    private Button loginButton;

    public LoginScreen()
    {
        try
        {
            administration = new Administration();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        this.stage = primaryStage;

        Group root = new Group();

        innitializeViewObjects();

        root.getChildren().addAll(gridPane);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void innitializeViewObjects()
    {
        gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(5));

        usernameText = new Label("Username");

        usernameField = new TextField();

        passwordText = new Label("Password");

        passwordField = new TextField();

        registerButton = new Button("Register");
        registerButton.setOnAction(event ->
        {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (administration.register(username, password))
            {
                //todo open main screen
            }
        });

        loginButton = new Button("Login");
        loginButton.setOnAction(event ->
        {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (administration.login(username, password))
            {
                //todo open main screen
            }
        });

        gridPane.add(usernameText, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordText, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(registerButton, 0, 2);
        gridPane.add(loginButton, 1, 2);
    }
}
