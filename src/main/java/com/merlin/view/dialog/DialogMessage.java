package com.merlin.view.dialog;

import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.view.R;
import com.merlin.view.dialog.base.DialogCommon;
import com.merlin.view.dialog.base.IDialog;

/**
 * @author merlin
 */

public class DialogMessage {

    private DialogCommon mDialog;
    private TextView mTitleText;
    private TextView mMsgText;
    private TextView mBtnText;

    private IDialog.OnConfirmListener onConfirmListener;

    public static DialogMessage newInstance() {
        return new DialogMessage();
    }

    private DialogMessage() {
        mDialog = new DialogCommon.Builder(MContext.app())
                .setLayout(R.layout.m_dialog_message)
                .setWidth(MContext.device().widthPixels * 3 / 4)
                .center()
                .build();
        mTitleText = mDialog.view(R.id.m_dialog_title);
        mMsgText = mDialog.view(R.id.m_dialog_message);
        mBtnText = mDialog.view(R.id.m_dialog_btn);

        mBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });
    }

    public DialogMessage setTitleText(int textColor, float textSize) {
        setTextView(mTitleText, textColor, textSize);
        return this;
    }

    public DialogMessage setMessageText(int textColor, float textSize) {
        setTextView(mMsgText, textColor, textSize);
        return this;
    }

    public DialogMessage setBtnText(int textColor, float textSize) {
        setTextView(mBtnText, textColor, textSize);
        return this;
    }

    private DialogMessage setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    public DialogMessage setTitle(String title) {
        if (title != null) {
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        } else {
            mTitleText.setVisibility(View.GONE);
        }
        return this;
    }

    public DialogMessage setMessage(String message) {
        mMsgText.setText(message);
        return this;
    }

    public DialogMessage setBtn(String btn) {
        if (btn != null) {
            mBtnText.setText(btn);
        }
        return this;
    }

    public DialogMessage setOnCancelListener(final IDialog.OnCancelListener onCancelListener) {
        mDialog.setCancelListener(onCancelListener);
        return this;
    }

    public DialogMessage setOnConfirmListener(final IDialog.OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    public DialogMessage show(FragmentManager fragmentManager) {
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogConfirm");
        }
        return this;
    }
}
