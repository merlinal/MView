package com.merlin.view.dialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MLog;
import com.merlin.core.util.MUtil;
import com.merlin.core.util.MVerify;
import com.merlin.view.R;
import com.merlin.view.SelectModel;
import com.merlin.view.widget.PickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author merlin
 */

public class DialogPicker {

    private DialogCommon mDialog;
    private LinearLayout mContainerLayout;
    private TextView mTitleText, mLeftText, mRightText;
    /**
     * 数据集
     */
    private List<List<SelectModel>> mDataList;
    /**
     * 是否联动
     */
    private boolean isLinked = true;
    /**
     * 浮标，第mPickerIndex个PickerView
     */
    private int mPickerIndex = 0;
    /**
     * 每个滚轮的单位
     */
    private String[] mLabels;
    /**
     * 每个滚轮的宽度占比
     */
    private float[] mLayoutWeights;
    /**
     * 每个滚轮的默认选中项
     */
    private int[] mSelectIndexes;
    /**
     * 选中项，用于确定事件返回
     */
    private SparseArray<String> mTextList;
    private SparseIntArray mIndexList;
    /**
     * 是否首次展示dialog
     */
    private boolean isFirstShow = true;
    /**
     * 事件
     */
    private IDialog.OnCancelListener mCancelListener;
    private IDialog.OnSelectListener mSelectListener;

    public static DialogPicker newInstance(boolean isLinked) {
        return new DialogPicker(isLinked);
    }

    private DialogPicker(boolean isLinked) {
        mDialog = new DialogCommon.Builder(MContext.app())
                .setLayout(R.layout.m_dialog_picker)
                .bottom()
                .build();
        this.isLinked = isLinked;
        //view
        mContainerLayout = mDialog.view(R.id.m_dialog_container);
        mTitleText = mDialog.view(R.id.m_dialog_title);
        mLeftText = mDialog.view(R.id.m_dialog_left);
        mRightText = mDialog.view(R.id.m_dialog_right);
        //初始化
        mTextList = new SparseArray<>();
        mIndexList = new SparseIntArray();
        mLeftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (mCancelListener != null) {
                    mCancelListener.onCancel();
                }
            }
        });
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (mSelectListener != null) {
                    String text = "";
                    for (int i = 0; i < mTextList.size(); i++) {
                        text += mTextList.get(i);
                    }
                    int[] index = new int[mIndexList.size()];
                    for (int i = 0; i < mIndexList.size(); i++) {
                        index[i] = mIndexList.get(i);
                    }
                    mSelectListener.onSelect(text, index);
                }
            }
        });
    }

    public DialogPicker addLinked(List<SelectModel> list, String... labels) {
        return addLinked(list, labels, null, null);
    }

    public DialogPicker addLinked(List<SelectModel> list, String[] labels, float[] weights, int[] selectIndexes) {
        mDataList = new ArrayList<>(1);
        mDataList.add(list);
        mLabels = labels;
        mLayoutWeights = weights;
        mSelectIndexes = selectIndexes;
        return this;
    }

    public DialogPicker addSingle(final List<SelectModel> list, String label) {
        return addSingle(list, label, 1.0f, 0);
    }

    public DialogPicker addSingle(final List<SelectModel> list, String label, float weight, int selectIndex) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
            mLabels = new String[]{};
            mLayoutWeights = new float[]{};
            mSelectIndexes = new int[]{};
        }
        mDataList.add(list);
        mLabels = Arrays.copyOf(mLabels, mLabels.length + 1);
        mLabels[mLabels.length - 1] = label;
        mLayoutWeights = Arrays.copyOf(mLayoutWeights, mLayoutWeights.length + 1);
        mLayoutWeights[mLayoutWeights.length - 1] = weight;
        mSelectIndexes = Arrays.copyOf(mSelectIndexes, mSelectIndexes.length + 1);
        mSelectIndexes[mSelectIndexes.length - 1] = selectIndex;
        return this;
    }

    private DialogPicker add(List<List<SelectModel>> list, String[] labels, float[] weights, int[] selectIndexes) {
        mDataList = list;
        mLabels = (!MVerify.isEmpty(labels) ? labels : new String[]{""});
        mLayoutWeights = (weights != null && weights.length > 0 ? weights : new float[]{1.0f});
        mSelectIndexes = (selectIndexes != null && selectIndexes.length > 0 ? selectIndexes : new int[]{0});
        if (isLinked) {
            add(list.get(0));
        } else {
            for (int i = 0; i < list.size(); i++) {
                add(list.get(i));
            }
        }
        return this;
    }

    private void add(final List<SelectModel> list) {
        String label = mLabels[mPickerIndex % mLabels.length];
        float weight = mLayoutWeights[mPickerIndex % mLayoutWeights.length];
        int selectIndex = mSelectIndexes[mPickerIndex % mSelectIndexes.length];
        //获取itemView
        View itemView = MUtil.view(R.layout.m_dialog_picker_item, mContainerLayout, false);
        mContainerLayout.addView(itemView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
        PickerView pickerView = MUtil.view(itemView, R.id.m_dialog_picker);
        TextView labelText = MUtil.view(itemView, R.id.m_dialog_label);
        //设置数据
        if (selectIndex < 0 || selectIndex >= list.size()) {
            selectIndex = 0;
        }
        pickerView.setData(list);
        pickerView.setSelected(selectIndex);
        pickerView.setTag(mPickerIndex);
        SelectModel selectedModel = list.get(selectIndex);
        if (MVerify.isEmpty(label)) {
            labelText.setVisibility(View.GONE);
        } else {
            labelText.setVisibility(View.VISIBLE);
            labelText.setText(label);
        }
        //记录选中项
        mTextList.put(mPickerIndex, selectedModel.toString());
        mIndexList.put(mPickerIndex, selectIndex);
        //浮标，第mPickerIndex个PickerView
        mPickerIndex++;
        //事件
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                notifyDataChanged(pickerView, list, index);
            }
        });
        //联动
        if (isLinked && !MVerify.isEmpty(selectedModel.getChildren())) {
            add(list.get(selectIndex).getChildren());
        }
    }

    private void notifyDataChanged(PickerView pickerView, List<SelectModel> list, int selectIndex) {
        //改变当前滚轮的数据状态
        for (SelectModel model : list) {
            model.setSelected(false);
        }
        SelectModel selectedModel = list.get(selectIndex);
        selectedModel.setSelected(true);
        //获取当前滚轮的浮标，是第几个滚轮
        int tag = Integer.valueOf(pickerView.getTag().toString());
        mTextList.put(tag, selectedModel.toString());
        mIndexList.put(tag, selectIndex);
        //若联动 & 有子滚轮，改变子滚轮的状态
        if (isLinked) {
            if (tag < mContainerLayout.getChildCount() - 1) {
                List<SelectModel> childList = selectedModel.getChildren();
                PickerView childPickerView = MUtil.view(mContainerLayout.getChildAt(tag + 1), R.id.m_dialog_picker);
                childPickerView.setData(childList);
                childPickerView.setSelected(0);
                executeAnimator(childPickerView);
                //递归
                notifyDataChanged(childPickerView, childList, 0);
            }
        }
    }

    public DialogPicker setTitle(String text) {
        if (text != null) {
            mTitleText.setText(text);
        }
        return this;
    }

    public DialogPicker setLeft(String text, final IDialog.OnCancelListener onCancelListener) {
        if (text != null) {
            mLeftText.setText(text);
        }
        mCancelListener = onCancelListener;
        return this;
    }

    public DialogPicker setRight(String text, final IDialog.OnSelectListener onSelectListener) {
        if (text != null) {
            mRightText.setText(text);
        }
        mSelectListener = onSelectListener;
        return this;
    }

    public DialogPicker setTitleText(int textColor, float textSize) {
        setTextView(mTitleText, textColor, textSize);
        return this;
    }

    public DialogPicker setLeftText(int textColor, float textSize) {
        setTextView(mLeftText, textColor, textSize);
        return this;
    }

    public DialogPicker setRightText(int textColor, float textSize) {
        setTextView(mRightText, textColor, textSize);
        return this;
    }

    private DialogPicker setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    public DialogPicker show(FragmentManager fragmentManager) {
        if (mDialog.isShowing()) {
            MLog.e("DialogPicker is showing");
            return this;
        }
        //第一次show时，初始数据
        if (isFirstShow) {
            add(mDataList, mLabels, mLayoutWeights, mSelectIndexes);
            isFirstShow = false;
        }
        mDialog.show(fragmentManager, "DialogPicker");
        return this;
    }

    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }

    private void getSelectText(StringBuffer sb, int[] selectIndexes) {
        for (int i = 0; i < mDataList.size(); i++) {
            for (int j = 0; j < mDataList.get(i).size(); j++) {
                SelectModel model = mDataList.get(i).get(j);
                if (model.isSelected()) {
                    selectIndexes[i] = j;
                    sb.append(model.toString());
                    break;
                }
            }
        }
    }

}
