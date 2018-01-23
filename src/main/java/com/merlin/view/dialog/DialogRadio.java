package com.merlin.view.dialog;

import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.view.R;
import com.merlin.view.recycler.AbstractRecyclerAdapter;
import com.merlin.view.recycler.MRecyclerView;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * @author merlin
 */

public class DialogRadio {

    private DialogCommon mDialog;
    private TextView mBtnText;
    private IDialog.OnRadioListener mOnRadioListener;

    public static <T> DialogRadio newInstance(T... ts) {
        return new DialogRadio(Arrays.asList(ts));
    }

    public static <T> DialogRadio newInstance(List<T> list) {
        return new DialogRadio(list);
    }

    private <T> DialogRadio(List<T> list) {
        mDialog = new DialogCommon.Builder(MContext.app()).setLayout(R.layout.m_dialog_radio)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT).bottom().build();
        MRecyclerView mRecyclerView = mDialog.view(R.id.m_dialog_mRecyclerView);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<T>(list) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return R.layout.m_dialog_radio_item;
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
                TextView itemText = holder.view(R.id.m_dialog_item_text);
                itemText.setText(getData(position).toString());
                itemText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        if (mOnRadioListener != null) {
                            mOnRadioListener.onSelect(position);
                        }
                    }
                });
            }
        });
        mBtnText = mDialog.view(R.id.m_dialog_btn);
    }

    public DialogRadio setBtnText(int textColor, float textSize) {
        setTextView(mBtnText, textColor, textSize);
        return this;
    }

    private DialogRadio setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    private DialogRadio setBtn(String btn) {
        if (btn != null) {
            mBtnText.setText(btn);
        }
        return this;
    }

    private DialogRadio setOnCancelListener(final IDialog.OnCancelListener onCancelListener) {
        mDialog.setCancelListener(onCancelListener);
        mBtnText.setOnClickListener(new View.OnClickListener() {
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

    private DialogRadio setOnRadioListener(IDialog.OnRadioListener onRadioListener) {
        mOnRadioListener = onRadioListener;
        return this;
    }

    public DialogRadio show(FragmentManager fragmentManager) {
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogRadio");
        }
        return this;
    }

}
