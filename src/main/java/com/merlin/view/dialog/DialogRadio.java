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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author merlin
 */

public class DialogRadio {

    private DialogCommon mDialog;
    private MRecyclerView mRecyclerView;
    private TextView mBtnText;
    private ArrayList<String> mDataList = new ArrayList<>();
    private IDialog.OnRadioListener mOnRadioListener;

    public static DialogRadio newInstance() {
        return new DialogRadio();
    }

    private DialogRadio() {
        mDialog = new DialogCommon.Builder(MContext.app()).setLayout(R.layout.m_dialog_radio)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT).bottom().build();
        mRecyclerView = mDialog.view(R.id.m_dialog_mRecyclerView);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<String>(mDataList) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return R.layout.m_dialog_radio_item;
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
                TextView itemText = holder.view(R.id.m_dialog_item_text);
                itemText.setText(getData(position));
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

    private void setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }


    public void show(FragmentManager fragmentManager, String btn,
                     final IDialog.OnCancelListener onCancelListener,
                     final IDialog.OnRadioListener onRadioListener,
                     String... datas) {
        mDialog.setCancelListener(onCancelListener);
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogRadio");
        }
        mOnRadioListener = onRadioListener;
        if (btn != null) {
            mBtnText.setText(btn);
        }

        if (datas != null) {
            mDataList.addAll(Arrays.asList(datas));
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        mBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
            }
        });
    }
}
