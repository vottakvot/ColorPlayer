package ru.testsimpleapps.coloraudioplayer.control.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializableTool {

    public static boolean objectToFile(String path, Object object) {
        ObjectOutputStream outputStream = null;
        try {
            File file = new File(path);
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static Object fileToObject(String path) {
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            File file = new File(path);
            objectInputStream = new ObjectInputStream(new FileInputStream(file));
            object = objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }
}
