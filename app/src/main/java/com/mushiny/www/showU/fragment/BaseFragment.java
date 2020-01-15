package com.mushiny.www.showU.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.util.ToastUtil;

/**
 * fragment 的基类
 */
public class BaseFragment extends Fragment {

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fragment 重影解决方案：自己保存Fragment的Hidden状态
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    /**
     * 从 Fragment A 跳转到另一个 Fragment B
     */
    public void showFragment(FragmentActivity fragmentActivity, Fragment current, Fragment next, String tag){
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();

        if (next.isAdded()){
            // 正常情况下是不会执行到该段代码的，不排除特殊情况，为严谨加上
            transaction.hide(current).show(next).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }else {
            transaction.hide(current).add(R.id.framelayout_container, next, tag)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }
    }
}
