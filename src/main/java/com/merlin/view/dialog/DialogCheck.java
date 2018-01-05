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
import com.merlin.view.recycler.AbstractRecyclerAdapter;
import com.merlin.view.recycler.MRecyclerView;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author merlin
 */

public class DialogCheck {

    private DialogCommon mDialog;
    private MRecyclerView mRecyclerView;
    private TextView mLeftText;
    private TextView mRightText;
    private ArrayList<String> mDataList = new ArrayList<>();
    private Set<Integer> mIndexSet = new HashSet<>();

    public static DialogCheck newInstance() {
        return new DialogCheck();
    }

    private DialogCheck() {
        mDialog = new DialogCommon.Builder(MContext.app()).setLayout(R.layout.m_dialog_check)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT).bottom().build();
        mRecyclerView = mDialog.view(R.id.m_dialog_mRecyclerView);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<String>(mDataList) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return R.layout.m_dialog_check_item;
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
                TextView itemText = holder.view(R.id.m_dialog_item_text);
                itemText.setText(getData(position));
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
    }

    public DialogCheck setLeftText(int textColor, float textSize) {
        setTextView(mLeftText, textColor, textSize);
        return this;
    }

    public DialogCheck setRightText(int textColor, float textSize) {
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

    public void show(FragmentManager fragmentManager, String left, String right,
                     final IDialog.OnCancelListener onCancelListener,
                     final IDialog.OnCheckListener onCheckListener,
                     String... datas) {
        mDialog.setCancelListener(onCancelListener);
        if (!mDialog.isShowing()) {
            mDialog.show(fragmentManager, "DialogRadio");
        }
        if (left != null) {
            mLeftText.setText(left);
        }
        if (right != null) {
            mRightText.setText(right);
        }

        if (datas != null) {
            mDataList.addAll(Arrays.asList(datas));
            mRecyclerView.getAdapter().notifyDataSetChanged();
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
            public void onClick(View v) {
                mDialog.dismiss();
                if (onCheckListener != null) {
                    onCheckListener.onSelect(mIndexSet);
                }
            }
        });
    }
}
