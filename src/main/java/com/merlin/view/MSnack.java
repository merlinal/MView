package com.merlin.view;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.merlin.core.util.MUtil;

/**
 * @author merlin
 */

public class MSnack {

    public static void show(View view, String message) {
        show(view, MUtil.color(R.color.primary_ee), message, MUtil.color(R.color.white), null, 0, null);
    }

    public static void show(View view, String message, String button, View.OnClickListener listener) {
        show(view, MUtil.color(R.color.primary_ee), message, MUtil.color(R.color.white), button, MUtil.color(R.color.white), listener);
    }

    /**
     * @param view            页面view，用于找父容器
     * @param backgroundColor 背景色
     * @param message         消息
     * @param messageColor    消息字体颜色
     * @param button          按钮
     * @param buttonColor     按钮颜色
     * @param listener        事件
     */
    public static void show(View view, int backgroundColor, String message, int messageColor,
                            String button, int buttonColor, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        setSnackColor(snackbar, messageColor, backgroundColor, buttonColor);
        snackbar.setAction(button, listener);
        snackbar.show();
    }

    private static void setSnackColor(Snackbar snack, int messageColor, int backgroundColor, int buttonColor) {
        View view = snack.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
            if (messageColor != 0) {
                TextView textView = MUtil.view(view, R.id.snackbar_text);
                textView.setTextColor(messageColor);
            }
            if (buttonColor != 0) {
                Button button = MUtil.view(view, R.id.snackbar_action);
                button.setTextColor(buttonColor);
            }
        }
    }

}
