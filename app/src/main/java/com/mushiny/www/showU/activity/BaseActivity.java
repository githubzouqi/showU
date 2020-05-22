package com.mushiny.www.showU.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * activity 基类
 */
public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 根据 tag 获取 fragment
     * 注：别在 activity 的 onCreate 方法中使用该方法，因为此时 fragment 还未完全创建好，会返回为 null
     * @param fragmentTag
     * @return
     */
    public Fragment findFragmentByTag(String fragmentTag){

        return getSupportFragmentManager().findFragmentByTag(fragmentTag);

    }

    /**
     * 加载根 fragment
     */
    public void loadRootFragment(int containerId, @NonNull Fragment rootFragment, String tag){

        getSupportFragmentManager().beginTransaction().add(containerId, rootFragment, tag).commitAllowingStateLoss();

    }

    /**
     * 获取当前 activity 栈内的 fragment 个数
     * 注：别在 activity 的 onCreate 方法中使用该方法
     * @return
     */
    public int getFragmentCount(){
        return getSupportFragmentManager().getFragments().size();
    }

    // 隐藏 activity 中的 FragmentManager 栈内的所有已经添加了的 fragment
    public void hideAllFragment(FragmentTransaction transaction){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0){
            for (Fragment fragment : fragments){
                // 加上不为空判断，防止出现异常（hide方法的参数不能为空，否则会报错）
                if (fragment != null){
                    transaction.hide(fragment);
                }
            }
        }
    }

    /**
     * 获取加入了返回栈的 fragment 个数
     * @return
     */
    public int getBackStackEntryCount(){
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    /**
     * 返回栈中的实例出栈
     */
    public void popBack(){
        getSupportFragmentManager().popBackStack();
    }
}
