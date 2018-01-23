package com.merlin.view.dialog;

import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.merlin.core.context.MContext;
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
        mDialog = new DialogCommon.Builder(MContext.app())
                .setLayout(R.layout.m_dialog_confirm)
                .setWidth(MContext.device().widthPixels * 3 / 4)
                .center()
                .build();
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

    private DialogConfirm setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    public DialogConfirm setTitle(String title) {
        if (title != null) {
            mTitleText.setVisibility(View.VISIBLE);
            mTitleText.setText(title);
        } else {
            mTitleText.setVisibility(View.GONE);
        }
        return this;
    }

    public DialogConfirm setMessage(String message) {
        mMsgText.setText(message);
        return this;
    }

    public DialogConfirm setBtn(String left, String right) {
        if (left != null) {
            mLeftText.setText(left);
        }
        if (right != null) {
            mRightText.setText(right);
        }
        return this;
    }

    public DialogConfirm setOnCancelListener(final IDialog.OnCancelListener onCancelListener) {
        mDialog.setCancelListener(onCancelListener);
        mLeftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            }
        });
        return this;
    }

    public DialogConfirm setOnConfirmListener(final IDialog.OnConfirmListener onConfirmListener) {
        mRightText.setOnClickListener(new View.OnClickListener() {
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

    public DialogConfirm show(FragmentManager fragmentManager) {

        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogConfirm");
        }
        return this;
    }

}
