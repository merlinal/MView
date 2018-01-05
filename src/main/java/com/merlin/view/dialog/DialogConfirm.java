package com.merlin.view.dialog;

import android.content.Context;
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

public class DialogConfirm {

    private DialogCommon mDialog;
    private TextView mTitleText;
    private TextView mMsgText;
    private TextView mLeftText;
    private TextView mRightText;

    public static DialogConfirm newInstance() {
        return new DialogConfirm();
    }

    private DialogConfirm() {
        mDialog = new DialogCommon.Builder(MContext.app()).setLayout(R.layout.m_dialog_confirm).center().build();
        mTitleText = mDialog.view(R.id.m_dialog_title);
        mMsgText = mDialog.view(R.id.m_dialog_message);
        mLeftText = mDialog.view(R.id.m_dialog_left);
        mRightText = mDialog.view(R.id.m_dialog_right);
    }

    public DialogConfirm setTitleText(int textColor, float textSize) {
        setTextView(mTitleText, textColor, textSize);
        return this;
    }

    public DialogConfirm setMessageText(int textColor, float textSize) {
        setTextView(mMsgText, textColor, textSize);
        return this;
    }

    public DialogConfirm setLeftText(int textColor, float textSize) {
        setTextView(mLeftText, textColor, textSize);
        return this;
    }

    public DialogConfirm setRightText(int textColor, float textSize) {
        setTextView(mRightText, textColor, textSize);
        return this;
    }

    private void setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }

    public void show(FragmentManager fragmentManager, int titleId, int messageId, int leftId, int rightId,
                     final IDialog.OnCancelListener onCancelListener,
                     final IDialog.OnConfirmListener onConfirmListener) {
        show(fragmentManager, MUtil.string(titleId), MUtil.string(messageId), MUtil.string(leftId),
                MUtil.string(rightId), onCancelListener, onConfirmListener);
    }


    public void show(FragmentManager fragmentManager, String title, String message, String left, String right,
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
        if (left != null) {
            mLeftText.setText(left);
        }
        if (right != null) {
            mRightText.setText(right);
        }

        mLeftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            }
        });
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });
    }

}
