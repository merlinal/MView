package com.merlin.view.dialog;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.view.R;
import com.merlin.view.dialog.base.DialogCommon;

/**
 * @author merlin
 */

public class DialogProgress {

    private DialogCommon mDialog;

    public static DialogProgress inst() {
        return InstHolder.DIALOG_PROGRESS;
    }

    private static class InstHolder {
        private static final DialogProgress DIALOG_PROGRESS = new DialogProgress();
    }

    public void dismiss() {
        mDialog.dismissAllowingStateLoss();
    }

    private DialogProgress() {
        mDialog = new DialogCommon.Builder(MContext.app())
                .setLayout(R.layout.m_dialog_progress)
                .setCancelable(false)
                .center()
                .build();
    }

    public DialogProgress setText(String text) {
        TextView textView = mDialog.view(R.id.m_dialog_text);
        textView.setVisibility(text == null ? View.GONE : View.VISIBLE);
        textView.setText(text);
        return inst();
    }

    public DialogProgress showDialog(FragmentManager fragmentManager) {
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogProgress");
        }
        return inst();
    }

    public DialogProgress showDialog(FragmentManager fragmentManager, long time) {
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogProgress");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, time);
        return inst();
    }

}
