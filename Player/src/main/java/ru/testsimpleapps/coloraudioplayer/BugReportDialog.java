package ru.testsimpleapps.coloraudioplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;

public class BugReportDialog {
    private final String EMAIL_HEADER = "Internal player error.";
    private final AlertDialog alertDialog;
    private final String fullMessage;

    public BugReportDialog(final Context context, final String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.crush_title));
        alertDialogBuilder.setMessage(context.getResources().getString(R.string.crush_message));
        alertDialogBuilder.setCancelable(true);

        fullMessage =   "Version:_" + Build.VERSION.RELEASE.replace(" ", "_") + "\n" +
                        "Manufacturer:_" + Build.MANUFACTURER.replace(" ", "_") + "\n" +
                        "Model:_" + Build.MODEL.replace(" ", "_") + "\n" + message.replace(" ", "_");

        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.crush_button_send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                sendEmail(context, context.getResources().getString(R.string.EmailForApplication), EMAIL_HEADER, fullMessage);
            }
        });

        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.crush_button_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });

        alertDialog = alertDialogBuilder.create();
    }

    public static void sendEmail(Context context, String to, String subject, String body){
        StringBuilder builder = new StringBuilder("mailto:" + Uri.encode(to));
        if (subject != null) {
            builder.append("?subject=" + Uri.encode(Uri.encode(subject)));
            if (body != null) {
                builder.append("&body=" + Uri.encode(Uri.encode(body)));
            }
        }
        context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(builder.toString())));
    }

    public void show(){
        alertDialog.show();
    }
}
