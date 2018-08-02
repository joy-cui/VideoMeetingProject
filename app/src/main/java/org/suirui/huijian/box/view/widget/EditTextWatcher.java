package org.suirui.huijian.box.view.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * EditText监听,不允许用户输入空格
 *
 * @authordingna
 * @date2017-07-27
 **/
public class EditTextWatcher implements TextWatcher {

    private EditText et;

    public EditTextWatcher(EditText et) {
        this.et = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int selectionStart = et.getSelectionStart();
        String replace = s.toString().replace(" ", "");
        if (replace.equals(s.toString())) {
            return;
        } else {
            et.setText(replace);
            et.setSelection(selectionStart - 1);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
