package com.mushiny.www.showU.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtil {

    /**
     * 隐藏软键盘
     * @param view EditText
     */
    public static void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
