package com.merlin.view;

import android.view.Gravity;
import android.widget.Toast;

import com.merlin.core.context.MContext;

/**
 * @author merlin
 */

public class MView {

    public static void toast(String message) {
        Toast toast = Toast.makeText(MContext.app(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
