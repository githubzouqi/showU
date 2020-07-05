package com.mushiny.www.showU.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SoftInputUtil {

    /**
     * 隐藏软键盘 并清空输入框内容
     * @param view EditText
     */
    public static void hideKeyboard(EditText view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        view.setText("");
        view.clearFocus();
    }

    /**
     * 仅仅隐藏软键盘
     * @param view
     */
    public static void hideKeyboardOnly(EditText view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        view.clearFocus();
    }

}
