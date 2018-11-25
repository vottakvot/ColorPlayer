package ru.testsimpleapps.coloraudioplayer.managers.tools

import android.content.Context
import android.util.Base64
import android.util.Log

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

import ru.testsimpleapps.coloraudioplayer.app.App


object SerializableTool {

    val TAG = SerializableTool::class.java.simpleName

    fun objectToFile(fileName: String, `object`: Any): Boolean {
        var objectOut: ObjectOutputStream? = null
        try {
            val fileOut = App.instance.openFileOutput(fileName, Context.MODE_PRIVATE)
            objectOut = ObjectOutputStream(fileOut)
            objectOut.writeObject(`object`)
            fileOut.getFD().sync()
        } catch (e: IOException) {
            Log.e(TAG, e.message)
            return false
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                }

            }
        }

        return true
    }

    fun fileToObject(fileName: String): Any? {
        var objectIn: ObjectInputStream? = null
        var `object`: Any? = null
        try {
            val fileIn = App.instance.getApplicationContext().openFileInput(fileName)
            objectIn = ObjectInputStream(fileIn)
            `object` = objectIn.readObject()
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message)
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, e.message)
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                }

            }
        }

        return `object`
    }

    fun objectToString(`object`: Serializable): String? {
        var encoded: String? = null
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(`object`)
            objectOutputStream.close()
//            encoded = String(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0))
            ""
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        }

        return encoded
    }


    fun stringToObject(string: String): Serializable? {
        var `object`: Serializable? = null
        try {
            val bytes = Base64.decode(string, 0)
            val objectInputStream = ObjectInputStream(ByteArrayInputStream(bytes))
            `object` = objectInputStream.readObject() as Serializable
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, e.message)
        } catch (e: ClassCastException) {
            Log.e(TAG, e.message)
        }

        return `object`
    }
}
