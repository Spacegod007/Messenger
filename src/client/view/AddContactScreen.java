package client.view;

import client.logic.Administration;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class AddContactScreen
{
    private final Administration administration;

    private final Stage primaryStage;
    private final Scene previousScene;

    private GridPane gridPane;

    private Label resultText;
    private Label usernameText;
    private TextField contactName;
    private Button okButton;
    private Button cancelButton;

    AddContactScreen(Stage primaryStage, Administration administration)
    {
        this.primaryStage = primaryStage;
        previousScene = primaryStage.getScene();
        this.administration = administration;

        initializeViewObjects();

        primaryStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
    }

    private void initializeViewObjects()
    {
        gridPane = ViewToolbox.buildStandardGridPane();

        resultText = new Label();

        usernameText = new Label("Username:");

        contactName = new TextField();

        okButton = new Button("Ok");
        okButton.setOnAction(this::addContact);

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(this::cancel);

        gridPane.add(usernameText, 0, 0);
        gridPane.add(contactName, 0, 1);
        gridPane.add(resultText, 0, 2);
        gridPane.add(okButton, 1, 3);
        gridPane.add(cancelButton, 0, 3);
    }

    private void cancel(ActionEvent event)
    {
        primaryStage.setScene(previousScene);
    }

    private void addContact(ActionEvent event)
    {
        String contactname = contactName.getText();
        try
        {
            if (administration.addContact(contactname))
            {
                resultText.setText("Contact added");
                resultText.setTextFill(Color.BLACK);
            }
            else
            {
                resultText.setText("Contact does not exist is has already been added");
                resultText.setTextFill(Color.RED);
            }
        }
        catch (RemoteException e)
        {
            new ErrorScreen(primaryStage, String.format("Whoops, something went wrong in the connection.%nPlease check if you're still connected to the internet."));
        }
        catch (IllegalArgumentException e)
        {
            resultText.setText("You can't add yourself as a contact");
            resultText.setTextFill(Color.RED);
        }
    }
}
