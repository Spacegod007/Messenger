package client.view;

import client.logic.Administration;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainScreen
{
    private final Administration administration;
    private final Stage primaryStage;
    private final Scene loginScene;
    private GridPane gridPane;

    private Label chatListViewText;
    private ListView<String> chatListView;
    private Label contactsListViewText;
    private ListView<String> contactsListView;

    private Button logoutButton;
    private Button addContactButton;

    public MainScreen(Stage primaryStage, Administration administration)
    {
        this.primaryStage = primaryStage;
        this.administration = administration;
        loginScene = primaryStage.getScene();

        getRequiredData();
        initializeViewObjects();

        primaryStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
    }

    private void getRequiredData()
    {

    }

    private void initializeViewObjects()
    {
        gridPane = ViewToolbox.buildStandardGridPane();

        chatListViewText = new Label("Chats");

        chatListView = new ListView<>();
        updateChatList(FXCollections.observableArrayList(administration.getParticipatingChatNames()));
        chatListView.setOnMouseClicked(this::openChat);

        contactsListViewText = new Label("Contacts");

        contactsListView = new ListView<>();
        updateContactList(FXCollections.observableArrayList(administration.getContacts()));
        contactsListViewText.setOnMouseClicked(this::newChat);

        logoutButton = new Button("Logout");
        logoutButton.setOnAction(this::logout);

        addContactButton = new Button("Add contact");
        addContactButton.setOnAction(this::addContact);

        gridPane.add(chatListViewText, 0, 0);
        gridPane.add(chatListView, 0, 1);
        gridPane.add(contactsListViewText, 1, 0);
        gridPane.add(contactsListView, 1, 1);
        gridPane.add(logoutButton, 0, 2);
        gridPane.add(addContactButton, 1, 2);
    }

    private void newChat(MouseEvent event)
    {
        if (event.getClickCount() == 2)
        {
            administration.newChat(contactsListView.getSelectionModel().getSelectedItem());
        }
    }

    private void openChat(MouseEvent event)
    {
        if (event.getClickCount() == 2)
        {
//            new ChatScreen(this, administration, administration.getChatMessages(chatListView.getSelectionModel().getSelectedItem()));
        }
    }

    private void updateChatList(ObservableList<String> chatNames)
    {
        Platform.runLater(() -> chatListView.setItems(chatNames));
    }

    private void updateContactList(ObservableList<String> contactnames)
    {
        Platform.runLater(() -> contactsListView.setItems(contactnames));
    }

    private void logout(ActionEvent event)
    {
        administration.logout();
        primaryStage.setScene(loginScene);
    }

    private void addContact(ActionEvent event)
    {
        new AddContactScreen(primaryStage, administration);
    }
}
