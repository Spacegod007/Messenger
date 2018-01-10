package bootstrapper;

import client.logic.Administration;
import client.view.LoginScreen;
import javafx.application.Application;

class ClientProgram
{
    private ClientProgram()
    { }

    public static void main(String[] args)
    {
        Application.launch(LoginScreen.class, args);
    }
}
