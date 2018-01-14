package client.view;

import client.logic.Administration;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shared.FileMessage;
import shared.Message;
import shared.SerializableChat;

import java.io.File;
import java.io.IOException;

public class ChatScreen
{
    private final Stage privateStage;
    private final Administration administration;

    private SerializableChat chat;
    private String chatName;

    private ListView<Message> messageListView;
    private ListView<String> participants;
    private TextField messageField;
    private Button sendMessage;
    private Button sendFile;

    ChatScreen(Stage privateStage, Administration administration, String chatName)
    {
        this.privateStage = privateStage;
        this.administration = administration;
        this.chatName = chatName;

        this.chat = administration.getChatByName(chatName);

        administration.getChatByName().addListener((MapChangeListener<String, SerializableChat>) change -> refreshContents());

        initializeViewObjects();
    }

    private void initializeViewObjects()
    {
        GridPane gridPane = ViewToolbox.buildStandardGridPane();

        messageListView = new ListView<>();
        messageListView.setOnMouseClicked(this::messageListViewClicked);

        participants = new ListView<>();

        messageField = new TextField();

        messageField.setOnKeyReleased(this::keyPressed);

        sendMessage = new Button("Send");
        sendMessage.setOnMouseClicked(mouseEvent -> sendMessage());

        sendFile = new Button("File");
        sendFile.setOnMouseClicked(mouseEvent -> sendFile());

        refreshContents();

        gridPane.add(messageListView, 0, 0);
        gridPane.add(participants,1, 0);
        gridPane.add(sendFile, 1, 1);
        gridPane.add(messageField, 0, 2);
        gridPane.add(sendMessage, 1, 2);

        privateStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
        privateStage.show();
    }

    private void messageListViewClicked(MouseEvent mouseEvent)
    {
        Message selectedItem = messageListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem instanceof FileMessage)
        {
            FileMessage fileMessage = (FileMessage) selectedItem;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save " + fileMessage.getFilename());
            fileChooser.setInitialFileName(fileMessage.getFilename());
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null)
            {
                try
                {
                    administration.getFile(fileMessage, file);
                }
                catch (IOException e)
                {
                    new ErrorScreen(privateStage, String.format("Whoops, something went wrong while saving the file.%nFeel free to try again later."));
                }
            }
        }
    }

    private void sendFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File to send");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null)
        {
            administration.sendFile(chatName, file);
        }
    }

    private void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER && !keyEvent.isShiftDown() && !keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isMetaDown() && !keyEvent.isShortcutDown())
        {
            sendMessage();
        }
    }

    private void sendMessage()
    {
        String message = messageField.getText();

        if (message != null)
        {
            administration.sendMessage(chatName, message);
        }
    }

    private void refreshContents()
    {
        chat = administration.getChatByName(chatName);

        Platform.runLater(() -> {
            participants.setItems(FXCollections.observableArrayList(chat.getParticipants()));
            messageListView.setItems(FXCollections.observableArrayList(chat.getMessages()));
            messageListView.scrollTo(messageListView.getItems().size() - 1);
        });
    }

    public void close()
    {
        privateStage.close();
    }
}
