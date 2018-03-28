package com.merlin.view.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.merlin.core.context.MContext;
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

public class DialogSpinner {

    private DialogCommon mDialog;
    private IDialog.OnSelectListener mOnSelectedListener;
    private List mDataList;

    public static <T> DialogSpinner newInstance(View parentView, List<T> list) {
        return new DialogSpinner(parentView, R.layout.m_dialog_recycler_item, R.id.m_recycler_item_text, list);
    }

    public static <T> DialogSpinner newInstance(View parentView, T... ts) {
        return new DialogSpinner(parentView, R.layout.m_dialog_recycler_item, R.id.m_recycler_item_text, Arrays.asList(ts));
    }

    public static <T> DialogSpinner newInstance(View parentView, int itemLayoutId, int itemTextId, T... ts) {
        return new DialogSpinner(parentView, itemLayoutId, itemTextId, Arrays.asList(ts));
    }

    public static <T> DialogSpinner newInstance(View parentView, int itemLayoutId, int itemTextId, List<T> list) {
        return new DialogSpinner(parentView, itemLayoutId, itemTextId, list);
    }

    private <T> DialogSpinner(View parentView, final int itemLayoutId, final int itemTextId, final List<T> list) {
        mDataList = list;
        Rect rectParent = new Rect();
        parentView.getGlobalVisibleRect(rectParent);
        Rect rectRoot = new Rect();
        parentView.getWindowVisibleDisplayFrame(rectRoot);

        mDialog = new DialogCommon.Builder(MContext.app())
                .setWidth(parentView.getWidth())
                .setLayout(R.layout.m_dialog_spinner)
                .setDim(false)
                .setGravity(Gravity.LEFT | Gravity.TOP)
                .setX(rectParent.left)
                .setY(rectParent.bottom - rectRoot.top)
                .build();

        MRecyclerView mRecyclerView = mDialog.view(R.id.m_dialog_mRecyclerView);
        mRecyclerView.setAdapter(new AbstractRecyclerAdapter<T>(list) {

            @Override
            public int getItemResId(ViewGroup parent, int viewType) {
                return itemLayoutId;
            }

            @Override
            public void onBindViewHolder(final RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                TextView itemText = holder.view(itemTextId);
                itemText.setText(getData(position).toString());
                itemText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        onSelected(position);
                    }
                });
            }
        });
    }

    private void onSelected(int index) {
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onSelect(mDataList.get(index).toString(), index);
        }
    }

    public DialogSpinner setOnSelectedListener(IDialog.OnSelectListener onSelectedListener) {
        this.mOnSelectedListener = onSelectedListener;
        return this;
    }

    public DialogSpinner show(FragmentManager fragmentManager) {
        if (!mDialog.isShowing()) {
            showAnim();
            mDialog.show(fragmentManager, "DialogRadio");
        }
        return this;
    }

    private void showAnim() {
        AnimatorSet anim = new AnimatorSet();
        anim.playTogether(
                ObjectAnimator.ofFloat(mDialog.rootView(), "translationY", -300F, 0F),
                ObjectAnimator.ofFloat(mDialog.rootView(), "alpha", 0F, 1F)
        );
        anim.setDuration(300);
        anim.start();
    }

}
