package com.mushiny.www.showU.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.activity.MainActivity;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * fragment 的基类
 * 适用于 hide 和 show 来隐藏和显示 fragment 时的友盟统计
 */
public class BaseFragment extends Fragment {

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    // 首次初始化，默认为 true
    private boolean isFirstInit = true;
    // 是否可见
    private boolean isVisible;

    protected String baseTitle = "UHello";

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
        LogUtil.e("TAG", "cName is :" + getClass().getName());

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Override
    public void onResume() {
        super.onResume();
        onTitleSet();
        // 首次初始化，默认可见并开启友盟统计
        if (isFirstInit){
            isVisible = true;
            isFirstInit = false;
            MobclickAgent.onPageStart(getClass().getName());
            return;
        }

        // 若当前界面可见，调用友盟开启跳转统计
        if (isVisible){
            MobclickAgent.onPageStart(getClass().getName());
        }

    }

    public void onTitleSet(){
        setTopTitle();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 若当前界面可见，调用友盟结束跳转统计
        if (isVisible){
            MobclickAgent.onPageEnd(getClass().getName());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            isVisible = true;// 用户可见
            MobclickAgent.onPageStart(getClass().getName());
            onTitleSet();
        }else {
            isVisible = false;
            MobclickAgent.onPageEnd(getClass().getName());
        }
    }

    /**
     * 从 Fragment A 跳转到另一个 Fragment B
     */
    public void showFragment(FragmentActivity fragmentActivity, Fragment current, Fragment next,
                             String tag){
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager()
                .beginTransaction();

        if (next.isAdded()){
            // 正常情况下是不会执行到该段代码的，不排除特殊情况，为严谨加上
            transaction.hide(current).show(next).addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }else {
            transaction.hide(current).add(R.id.framelayout_container, next, tag)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        }

        // 隐藏底部选项栏
        ((MainActivity)getActivity()).goneTab();
    }

    /**
     * 标题内容设置
     */
    public void setTopTitle(){
        ((MainActivity)getActivity()).setTitle(baseTitle);
    }

}
