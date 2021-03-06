package com.merlin.view.dialog;

import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.view.R;
import com.merlin.view.dialog.base.DialogCommon;
import com.merlin.view.dialog.base.IDialog;
import com.merlin.view.recycler.AbstractRecyclerAdapter;
import com.merlin.view.recycler.MRecyclerView;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author merlin
 */

public class DialogCheck {

    private DialogCommon mDialog;
    private TextView mLeftText;
    private TextView mRightText;

    private IDialog.OnCancelListener onCancelListener;
    private IDialog.OnCheckListener onCheckListener;

    private Set<Integer> mIndexSet = new HashSet<>();

    public static <E> DialogCheck newInstance(List<E> list) {
        return new DialogCheck(list);
    }

    public static <E> DialogCheck newInstance(E... es) {
        return new DialogCheck(Arrays.asList(es));
    }

    private <E> DialogCheck(List<E> list) {
        mDialog = new DialogCommon.Builder(MContext.app()).setLayout(R.layout.m_dialog_check)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT).bottom().build();
        MRecyclerView mRecyclerView = mDialog.view(R.id.m_dialog_mRecyclerView);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<E>(list) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return R.layout.m_dialog_check_item;
            }

            @Override
            public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
                TextView itemText = holder.view(R.id.m_dialog_item_text);
                itemText.setText(getData(position).toString());
                final CheckBox checkBox = holder.view(R.id.m_dialog_item_box);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBox.setChecked(!checkBox.isChecked());
                        if (checkBox.isChecked()) {
                            mIndexSet.add(position);
                        } else {
                            mIndexSet.remove(position);
                        }
                    }
                });
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        buttonView.setChecked(isChecked);
                        if (isChecked) {
                            mIndexSet.add(position);
                        } else {
                            mIndexSet.remove(position);
                        }
                    }
                });
            }
        });
        mLeftText = mDialog.view(R.id.m_dialog_left);
        mRightText = mDialog.view(R.id.m_dialog_right);

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
            public void onClick(View v) {
                mDialog.dismiss();
                if (onCheckListener != null) {
                    onCheckListener.onSelect(mIndexSet);
                }
            }
        });
    }

    public DialogCheck setLeftText(int textColor, float textSize) {
        setTextView(mLeftText, textColor, textSize);
        return this;
    }

    public DialogCheck setRightText(int textColor, float textSize) {
        setTextView(mRightText, textColor, textSize);
        return this;
    }

    private DialogCheck setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    public DialogCheck setOnCancelListener(final IDialog.OnCancelListener onCancelListener) {
        mDialog.setCancelListener(onCancelListener);
        this.onCancelListener = onCancelListener;
        return this;
    }

    public DialogCheck setOnRadioListener(final IDialog.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
        return this;
    }

    public DialogCheck setBtn(String left, String right) {
        if (left != null) {
            mLeftText.setText(left);
        }
        if (right != null) {
            mRightText.setText(right);
        }
        return this;
    }

    public DialogCheck show(FragmentManager fragmentManager) {
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogRadio");
        }
        return this;
    }

}
