package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ru.testsimpleapps.coloraudioplayer.App;


public class SerializableTool {

    public static final String TAG = SerializableTool.class.getSimpleName();

    public static boolean objectToFile(String fileName, Object object) {
        ObjectOutputStream objectOut = null;
        try {
            final FileOutputStream fileOut = App.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return true;
    }

    public static Object fileToObject(String fileName) {
        ObjectInputStream objectIn = null;
        Object object = null;
        try {
            final FileInputStream fileIn = App.getContext().getApplicationContext().openFileInput(fileName);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return object;
    }

    public static String objectToString(final Serializable object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(),0));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return encoded;
    }

    public static Serializable stringToObject(final String string){
        Serializable object = null;
        try {
            final byte[] bytes = Base64.decode(string,0);
            final ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (Serializable) objectInputStream.readObject();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());;
        }

        return object;
    }
}
