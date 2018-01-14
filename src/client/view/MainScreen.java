package client.view;

import client.logic.Administration;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class MainScreen
{
    private final Administration administration;
    private final Stage primaryStage;
    private final List<ChatScreen> chatScreens;
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

        chatScreens = new ArrayList<>();

        initializeViewObjects();

        primaryStage.setScene(ViewToolbox.buildSceneFromGridPane(gridPane));
    }

    private void initializeViewObjects()
    {
        gridPane = ViewToolbox.buildStandardGridPane();

        chatListViewText = new Label("Chats");

        chatListView = new ListView<>();
        chatListView.setItems(administration.getParticipatingChatNames());
        administration.getParticipatingChatNames().addListener((ListChangeListener<String>) c -> chatListView.setItems(administration.getParticipatingChatNames()));
        chatListView.setOnMouseClicked(this::openChat);

        contactsListViewText = new Label("Contacts");

        contactsListView = new ListView<>();
        contactsListView.setItems(administration.getContacts());
        administration.getContacts().addListener((ListChangeListener<String>) c -> contactsListView.setItems(administration.getContacts()));
        contactsListView.setOnMouseClicked(this::newChat);

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
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
        {
            String selectedItem = contactsListView.getSelectionModel().getSelectedItem();

            if (selectedItem != null)
            {
                administration.newChat(selectedItem);

                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                chatScreens.add(new ChatScreen(new Stage(), administration, selectedItem));
            }
        }
    }

    private void openChat(MouseEvent event)
    {
        if (event.getClickCount() == 2)
        {
            String selectedItem = chatListView.getSelectionModel().getSelectedItem();

            if (selectedItem != null)
            {
                chatScreens.add(new ChatScreen(new Stage(), administration, selectedItem));
            }
        }
    }

    private void logout(ActionEvent event)
    {
        administration.logout();
        chatScreens.forEach(ChatScreen::close);
        chatScreens.clear();
        primaryStage.setScene(loginScene);
    }

    private void addContact(ActionEvent event)
    {
        new AddContactScreen(primaryStage, administration);
    }
}
