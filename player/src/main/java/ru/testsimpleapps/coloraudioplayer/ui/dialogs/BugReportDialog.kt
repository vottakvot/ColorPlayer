package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AlertDialog

import ru.testsimpleapps.coloraudioplayer.R

class BugReportDialog(context: Context, message: String) {
    private val EMAIL_HEADER = "Internal player error."
    private val alertDialog: AlertDialog
    private val fullMessage: String

    init {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(context.resources.getString(R.string.crush_title))
        alertDialogBuilder.setMessage(context.resources.getString(R.string.crush_message))
        alertDialogBuilder.setCancelable(true)

        fullMessage = "Version:_" + Build.VERSION.RELEASE.replace(" ", "_") + "\n" +
                "Manufacturer:_" + Build.MANUFACTURER.replace(" ", "_") + "\n" +
                "Model:_" + Build.MODEL.replace(" ", "_") + "\n" + message.replace(" ", "_")

        alertDialogBuilder.setPositiveButton(context.resources.getString(R.string.crush_button_send)) { dialog, arg1 -> sendEmail(context, context.resources.getString(R.string.EmailForApplication), EMAIL_HEADER, fullMessage) }

        alertDialogBuilder.setNegativeButton(context.resources.getString(R.string.crush_button_cancel)) { dialog, arg1 -> }

        alertDialog = alertDialogBuilder.create()
    }

    fun show() {
        alertDialog.show()
    }

    companion object {

        fun sendEmail(context: Context, to: String, subject: String?, body: String?) {
            val builder = StringBuilder("mailto:" + Uri.encode(to))
            if (subject != null) {
                builder.append("?subject=" + Uri.encode(Uri.encode(subject)))
                if (body != null) {
                    builder.append("&body=" + Uri.encode(Uri.encode(body)))
                }
            }
            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(builder.toString())))
        }
    }
}
