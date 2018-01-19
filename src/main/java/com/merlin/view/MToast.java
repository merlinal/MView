package com.merlin.view;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MUtil;

/**
 * @author merlin
 */

public class MToast {

    public static void show(String message) {
        show(message, getView(message), 0);
    }

    public static void showCenter(String message) {
        show(message, getView(message), Gravity.CENTER);
    }

    public static void show(String message, View view, int gravity) {
        Toast toast = Toast.makeText(MContext.app(), message, Toast.LENGTH_SHORT);
        toast.setView(view);
        if (gravity != 0) {
            toast.setGravity(gravity, 0, 0);
        }
        toast.show();
    }

    private static View getView(String message) {
        TextView view = new TextView(MContext.app());
        view.setPadding(MUtil.dp2px(8), MUtil.dp2px(8), MUtil.dp2px(8), MUtil.dp2px(8));
        view.setBackgroundResource(R.drawable.m_bg_primary_ee_radius);
        view.setText(message);
        view.setLineSpacing(0, 1.2f);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(MUtil.color(R.color.white));
        return view;
    }

}
