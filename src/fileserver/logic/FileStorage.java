package fileserver.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FileStorage extends UnicastRemoteObject implements IFileStorage
{
    private static final String PATH_PREFIX = "StoredFiles\\";

    public FileStorage() throws RemoteException
    {
        super();
    }

    @Override
    public byte[] getFile(String filename) throws FileNotFoundException
    {
        try
        {
            return readAllData(new File(PATH_PREFIX + filename));
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

    @Override
    public void storeData(String filename, byte[] data)
    {
        writeAllData(new File(PATH_PREFIX + filename), data);
    }

    private byte[] readAllData(File file) throws IOException
    {
        return Files.readAllBytes(Paths.get(file.toURI()));
    }

    private void writeAllData(File file, byte[] data)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false))
        {
            file.getParentFile().mkdirs();
            fileOutputStream.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
