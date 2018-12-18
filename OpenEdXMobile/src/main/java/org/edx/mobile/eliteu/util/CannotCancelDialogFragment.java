package org.edx.mobile.eliteu.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;


import roboguice.fragment.RoboDialogFragment;

public class CannotCancelDialogFragment extends RoboDialogFragment {

    static final String ARG_TITLE = "ARG_TITLE";
    static final String ARG_TITLE_RES = "ARG_TITLE_RES";
    static final String ARG_MESSAGE = "ARG_MESSAGE";
    static final String ARG_MESSAGE_RES = "ARG_MESSAGE_RES";

    ButtonAttribute positiveButtonAttr;

    ButtonAttribute negativeButtonAttr;

    public static CannotCancelDialogFragment newInstance(final @Nullable String title,
                                                         final @NonNull String message,
                                                         final @NonNull String positiveText,
                                                         final @Nullable DialogInterface.OnClickListener onPositiveClick,
                                                         final @Nullable String negativeText,
                                                         final @Nullable DialogInterface.OnClickListener onNegativeClick) {
        final CannotCancelDialogFragment fragment = new CannotCancelDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);
        arguments.putString(ARG_MESSAGE, message);
        fragment.positiveButtonAttr = new ButtonAttribute() {
            @NonNull
            @Override
            public String getText() {
                return positiveText;
            }

            @Nullable
            @Override
            public DialogInterface.OnClickListener getOnClickListener() {
                return onPositiveClick;
            }
        };
        fragment.negativeButtonAttr = new ButtonAttribute() {
            @NonNull
            @Override
            public String getText() {
                return negativeText;
            }

            @Nullable
            @Override
            public DialogInterface.OnClickListener getOnClickListener() {
                return onNegativeClick;
            }
        };
        fragment.setArguments(arguments);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final int titleResId = args.getInt(ARG_TITLE_RES);
        final int messageResId = args.getInt(ARG_MESSAGE_RES);
        final CharSequence title = titleResId != 0 ?
                getText(titleResId) : args.getString(ARG_TITLE);
        final CharSequence message = messageResId != 0 ?
                getText(messageResId) : args.getString(ARG_MESSAGE);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        if (title != null) {
            alertDialog.setTitle(title);
        }
        if (positiveButtonAttr != null) {
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveButtonAttr.getText(),
                    positiveButtonAttr.getOnClickListener());
        }
        if (negativeButtonAttr != null) {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeButtonAttr.getText(),
                    negativeButtonAttr.getOnClickListener());
        }
        return alertDialog;
    }

    public static abstract class ButtonAttribute {
        @Nullable
        abstract String getText();

        @Nullable
        abstract DialogInterface.OnClickListener getOnClickListener();
    }
}
