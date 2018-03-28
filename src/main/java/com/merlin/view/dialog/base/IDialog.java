package com.merlin.view.dialog.base;

import java.util.Set;

/**
 * @author merlin
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

    interface OnSelectListener {
        void onSelect(String text, int... indexes);
    }

}
