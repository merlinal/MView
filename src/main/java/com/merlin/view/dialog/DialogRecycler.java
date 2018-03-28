package com.merlin.view.dialog;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MUtil;
import com.merlin.view.R;
import com.merlin.view.dialog.base.DialogCommon;
import com.merlin.view.dialog.base.IDialog;
import com.merlin.view.recycler.AbstractRecyclerAdapter;
import com.merlin.view.recycler.MRecyclerView;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * @author merlin
 */

public class DialogRecycler {

    private DialogCommon mDialog;
    private View mDialogView;
    private TextView mBtnText;
    private View mLineView;
    private IDialog.OnCancelListener mOnCancelListener;
    private IDialog.OnRadioListener mOnRadioListener;
    private DialogCommon.Builder mBuilder;

    public static <T> DialogRecycler newInstance(T... ts) {
        return new DialogRecycler(R.layout.m_dialog_recycler, R.id.m_dialog_mRecyclerView,
                R.layout.m_dialog_radio_item, R.id.m_dialog_item_text, R.id.m_dialog_btn, Arrays.asList(ts));
    }

    public static <T> DialogRecycler newInstance(List<T> list) {
        return new DialogRecycler(R.layout.m_dialog_recycler, R.id.m_dialog_mRecyclerView,
                R.layout.m_dialog_radio_item, R.id.m_dialog_item_text, R.id.m_dialog_btn, list);
    }

    public static <T> DialogRecycler newInstance(int layout, int recyclerId, int layoutItem, int textId, int btnId, T... ts) {
        return new DialogRecycler(layout, recyclerId, layoutItem, textId, btnId, Arrays.asList(ts));
    }

    public static <T> DialogRecycler newInstance(int layout, int recyclerId, int layoutItem, int textId, int btnId, List<T> list) {
        return new DialogRecycler(layout, recyclerId, layoutItem, textId, btnId, list);
    }

    private <T> DialogRecycler(int layout, int recyclerId, final int layoutItem, final int textId, int btnId, List<T> list) {
        mDialogView = MUtil.inflater().inflate(layout, null);
        mBuilder = new DialogCommon.Builder(MContext.app())
                .setView(mDialogView)
                .setWidth(MContext.device().widthPixels * 5 / 6)
                .setHeight(MContext.device().heightPixels * 5 / 6)
                .center();

        MRecyclerView mRecyclerView = mDialogView.findViewById(recyclerId);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<T>(list) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return layoutItem;
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                TextView itemText = holder.view(textId);
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
        mBtnText = mDialogView.findViewById(btnId);
        mLineView = mDialogView.findViewById(R.id.m_dialog_line);

        mBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel();
                }
            }
        });
    }

    public DialogRecycler setBtnText(int textColor, float textSize) {
        setTextView(mBtnText, textColor, textSize);
        return this;
    }

    private DialogRecycler setTextView(TextView textView, int textColor, float textSize) {
        if (textView == null) {
            return this;
        }
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    public DialogRecycler setBtn(String btn) {
        if (mBtnText != null && btn != null) {
            mBtnText.setText(btn);
        }
        return this;
    }

    public DialogRecycler hideBtn() {
        if (mBtnText != null) {
            mBtnText.setVisibility(View.GONE);
        }
        if (mLineView != null) {
            mLineView.setVisibility(View.GONE);
        }
        return this;
    }

    public DialogRecycler setOnCancelListener(final IDialog.OnCancelListener onCancelListener) {
        mDialog.setCancelListener(onCancelListener);
        this.mOnCancelListener = onCancelListener;
        return this;
    }

    public DialogRecycler setOnRadioListener(IDialog.OnRadioListener onRadioListener) {
        mOnRadioListener = onRadioListener;
        return this;
    }

    public DialogRecycler setWidth(int width) {
        mBuilder.setWidth(width);
        return this;
    }

    public DialogRecycler setHeight(int height) {
        mBuilder.setHeight(height);
        return this;
    }

    public DialogRecycler center() {
        mBuilder.center();
        return this;
    }

    public DialogRecycler bottom() {
        mBuilder.bottom();
        return this;
    }

    public DialogRecycler show(FragmentManager fragmentManager) {
        if (mDialog == null) {
            mDialog = mBuilder.build();
        }
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogRecycler");
        }
        return this;
    }

}
