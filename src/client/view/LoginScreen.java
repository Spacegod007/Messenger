package client.view;

import client.logic.Administration;
import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Application;
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

    private Stage primaryStage;
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
        this.primaryStage = primaryStage;

        Group root = new Group();

        initializeViewObjects();

        root.getChildren().addAll(gridPane);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initializeViewObjects()
    {
        gridPane = ViewToolbox.buildStandardGridPane();

        usernameText = new Label("Username");

        usernameField = new TextField();

        passwordText = new Label("Password");

        passwordField = new TextField();

        registerButton = new Button("Register");
        registerButton.setOnAction(event -> register()
        );

        loginButton = new Button("Login");
        loginButton.setOnAction(event -> login());

        gridPane.add(usernameText, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordText, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(registerButton, 0, 2);
        gridPane.add(loginButton, 1, 2);
    }

    private void login()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (administration.login(username, password))
        {
            new MainScreen(primaryStage, administration);
        }
        else
        {

        }

        resetTextFields();
    }

    private void register()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try
        {
            if (administration.register(username, password))
            {
                new MainScreen(primaryStage, administration);
            }
        }
        catch (InvalidArgumentException ignored)
        { }

        resetTextFields();
    }

    private void resetTextFields()
    {
        usernameField.setText(null);
        passwordField.setText(null);
    }
}
