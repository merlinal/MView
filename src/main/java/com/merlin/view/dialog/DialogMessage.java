package com.merlin.view.dialog;

import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MUtil;
import com.merlin.view.R;

/**
 * @author merlin
 */

public class DialogMessage {

    private DialogCommon mDialog;
    private TextView mTitleText;
    private TextView mMsgText;
    private TextView mBtnText;

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

    public DialogMessage show(FragmentManager fragmentManager, int titleId, int messageId, int btnId,
                              final IDialog.OnCancelListener onCancelListener,
                              final IDialog.OnConfirmListener onConfirmListener) {
        return show(fragmentManager, MUtil.string(titleId), MUtil.string(messageId), MUtil.string(btnId),
                onCancelListener, onConfirmListener);
    }


    public DialogMessage show(FragmentManager fragmentManager, String title, String message, String btn,
                              final IDialog.OnCancelListener onCancelListener,
                              final IDialog.OnConfirmListener onConfirmListener) {
        mDialog.setCancelListener(onCancelListener);
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogConfirm");
        }

        if (title != null) {
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        } else {
            mTitleText.setVisibility(View.GONE);
        }
        mMsgText.setText(message);
        if (btn != null) {
            mBtnText.setText(btn);
        }

        mBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });
        return this;
    }
}
