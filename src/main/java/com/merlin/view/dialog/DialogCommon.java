package com.merlin.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.merlin.view.R;

/**
 * @author merlin.
 */

public class DialogCommon extends DialogFragment {

    private Builder mBuilder;
    private IDialog.OnCancelListener mOnCancelListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //代码设置 无标题
        setStyle(DialogFragment.STYLE_NO_TITLE, mBuilder.style);
        //解决复用时的异常 DialogFragment can not be attached to a container view
        if (mBuilder != null && mBuilder.view != null && mBuilder.view.getParent() != null) {
            ((ViewGroup) mBuilder.view.getParent()).removeAllViews();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //点击空白处能否取消
        setCancelable(mBuilder.cancelable);
        //在此处设置 无标题 对话框背景色
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        //全屏
        if (mBuilder.fullScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //对话框背景色
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //背景黑暗度
        window.setDimAmount(mBuilder.dim ? 0.35f : 0f);
        //设置dialog的 进出 动画
        if (mBuilder.anim != 0) {
            window.setWindowAnimations(mBuilder.anim);
        }
        return mBuilder.view;
    }

    @Override
    public void onStart() {
        /*这样也可以设置宽高*/
        /*getDialog().getWindow().setLayout(width, height);*/

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        if (mBuilder.gravity != 0) {
            params.gravity = mBuilder.gravity;
        }
        //设置宽高偏移
        params.width = mBuilder.width != 0 ? mBuilder.width : WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = mBuilder.height != 0 ? mBuilder.height : WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = mBuilder.x;
        params.y = mBuilder.y;
        getDialog().getWindow().setAttributes(params);

        super.onStart();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel();
        }
    }

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public <T> T view(int viewId) {
        return (T) mBuilder.view.findViewById(viewId);
    }

    public void setCancelListener(IDialog.OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    public static class Builder {
        private int layoutResId;
        private int width;
        private int height;
        private int x;
        private int y;
        private int style;
        private int anim;
        private int gravity;
        private boolean cancelable = true;
        private boolean fullScreen = false;
        private boolean dim = true;
        private Context context;
        private View view;

        public Builder(Context context) {
            this.context = context;
        }

        public DialogCommon build() {
            DialogCommon dialog = new DialogCommon();
            dialog.mBuilder = this;
            return dialog;
        }

        public Builder bottom() {
            anim = R.style.anim_bottom;
            gravity = Gravity.BOTTOM | Gravity.CENTER;
            return this;
        }

        public Builder center() {
            anim = R.style.anim_center;
            gravity = Gravity.CENTER;
            return this;
        }

        public Builder fullScreen() {
            fullScreen = true;
            style = R.style.dialog_fullscreen;
            width = WindowManager.LayoutParams.MATCH_PARENT;
            height = WindowManager.LayoutParams.MATCH_PARENT;
            return this;
        }

        public Builder setLayout(int layoutResId) {
            this.layoutResId = layoutResId;
            this.view = LayoutInflater.from(context).inflate(layoutResId, null);
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        public Builder setStyle(int style) {
            this.style = style;
            return this;
        }

        public Builder setAnim(int anim) {
            this.anim = anim;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setDim(boolean dim) {
            this.dim = dim;
            return this;
        }

    }

}
