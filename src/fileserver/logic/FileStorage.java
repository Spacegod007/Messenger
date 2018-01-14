package fileserver.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The file storage part of the system
 */
public class FileStorage extends UnicastRemoteObject implements IFileStorage
{
    /**
     * The prefix of the stored files location
     */
    private static final String PATH_PREFIX = "StoredFiles\\";

    /**
     * A synchronizer object used to only allow the reading/writing of one file at a time
     */
    private final Object synchronizer;

    /**
     * The constructor of the file storage object
     * @throws RemoteException if something goes wrong while initiating the server-side of the connection
     */
    public FileStorage() throws RemoteException
    {
        super();

        synchronizer = new Object();
    }

    /**
     * Gets a file of the server
     * @param filename of the file
     * @return a byte array containing the data of a file
     * @throws FileNotFoundException if the file was not found
     */
    @Override
    public byte[] getFile(String filename) throws FileNotFoundException
    {
        try
        {
            synchronized (synchronizer)
            {
                return readAllData(new File(PATH_PREFIX + filename));
            }
        }
        catch (FileNotFoundException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Stores data on the server
     * @param filename of the file to be stored
     * @param data of the file
     */
    @Override
    public void storeData(String filename, byte[] data)
    {
        synchronized (synchronizer)
        {
            writeAllData(new File(PATH_PREFIX + filename), data);
        }
    }

    /**
     * reads all data of a specified file
     * @param file to be read
     * @return a byte array containing the data of the file
     * @throws IOException if something goes wrong while reading the file
     */
    private byte[] readAllData(File file) throws IOException
    {
        return Files.readAllBytes(Paths.get(file.toURI()));
    }

    /**
     * Writes all data of the specified file
     * @param file where the data needs to be written
     * @param data to be written
     */
    private void writeAllData(File file, byte[] data)
    {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false))
        {
            fileOutputStream.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
