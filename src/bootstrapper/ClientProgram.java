package bootstrapper;

import client.view.LoginScreen;
import javafx.application.Application;

/**
 * Main entry point of the client application
 */
class ClientProgram
{
    /**
     * a private constructor to prevent initialisation of the class
     */
    private ClientProgram()
    { }

    /**
     * Main entry point of the client application
     * @param args
     */
    public static void main(String[] args)
    {
        Application.launch(LoginScreen.class, args);
    }
}
