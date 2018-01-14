package client.view;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

class ErrorScreen
{
    private final Stage primaryStage;
    private final Scene previousScene;
    private GridPane gridPane;

    private Label errorText;
    private Button okButton;

    public ErrorScreen(Stage primaryStage, String error)
    {
        this.primaryStage = primaryStage;
        this.previousScene = primaryStage.getScene();

        initializeViewObjects(error);

        primaryStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
    }

    private void initializeViewObjects(String error)
    {
        gridPane = ViewToolbox.buildStandardGridPane();

        errorText = new Label(error);
        errorText.setTextFill(Color.RED);

        okButton = new Button("Ok");
        okButton.setOnAction(this::returnToPreviousScene);

        gridPane.add(errorText, 0, 0);
        gridPane.add(okButton, 0, 1);
    }

    private void returnToPreviousScene(ActionEvent event)
    {
        primaryStage.setScene(previousScene);
    }
}
