package com.merlin.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.merlin.view.dialog.DialogCheck;
import com.merlin.view.dialog.DialogConfirm;
import com.merlin.view.dialog.DialogMessage;
import com.merlin.view.dialog.DialogRadio;
import com.merlin.view.dialog.IDialog;

import java.util.Set;

/**
 * Created by Administrator on 2018/1/4.
 */

public class TestA extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        findViewById(R.id.line11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogConfirm.newInstance().show(getSupportFragmentManager(),
                        null, "不再犹豫一下了么？", null, null,
                        new IDialog.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                MWidget.toast("取消");
                            }
                        }, new IDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                MWidget.toast("确定");
                            }
                        });
            }
        });

        findViewById(R.id.line12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogMessage.newInstance().setBtnText(0xffff0000, 0).show(getSupportFragmentManager(),
                        null, "删除就找不回了", "我知道了", null,
                        new IDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                MWidget.toast("确定");
                            }
                        });
            }
        });
        findViewById(R.id.line13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogRadio.newInstance().show(getSupportFragmentManager(),
                        null, null, new IDialog.OnRadioListener() {
                            @Override
                            public void onSelect(int index) {
                                MWidget.toast("" + index);
                            }
                        }, "头像", "姓名");
            }
        });
        findViewById(R.id.line14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCheck.newInstance().show(getSupportFragmentManager(),
                        null, null, null, new IDialog.OnCheckListener() {
                            @Override
                            public void onSelect(Set<Integer> indexSet) {
                                MWidget.toast("" + indexSet);
                            }
                        }, "头像", "姓名");
            }
        });
    }
}
