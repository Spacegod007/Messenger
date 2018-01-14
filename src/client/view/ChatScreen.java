package client.view;

import client.logic.Administration;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import shared.Message;
import shared.SerializableChat;

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

        participants = new ListView<>();

        messageField = new TextField();

        sendMessage = new Button("Send");
        sendMessage.setOnMouseClicked(this::sendMessage);

        sendFile = new Button("File");

        refreshContents();

        gridPane.add(messageListView, 0, 0);
        gridPane.add(participants,1, 0);
        gridPane.add(sendFile, 1, 1);
        gridPane.add(messageField, 0, 2);
        gridPane.add(sendMessage, 1, 2);

        privateStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
        privateStage.show();
    }

    private void sendMessage(MouseEvent mouseEvent)
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
