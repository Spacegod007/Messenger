package client.view;

import client.logic.Administration;

public class ChatScreen
{
    private final MainScreen mainScreen;
    private final Administration administration;

    public ChatScreen(MainScreen mainScreen, Administration administration, String chatName)
    {
        this.mainScreen = mainScreen;
        this.administration = administration;

    }
}
