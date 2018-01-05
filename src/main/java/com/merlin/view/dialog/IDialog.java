package com.merlin.view.dialog;

import java.util.Set;

/**
 * Created by Administrator on 2018/1/4.
 */

public interface IDialog {

    interface OnCancelListener {
        void onCancel();
    }

    interface OnConfirmListener {
        void onConfirm();
    }

    interface OnRadioListener {
        void onSelect(int index);
    }

    interface OnCheckListener {
        void onSelect(Set<Integer> indexSet);
    }

}
