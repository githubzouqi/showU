package com.mushiny.www.showU.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mushiny.www.showU.R;

import butterknife.ButterKnife;

/**
 * 发现 界面
 */
public class DiscoveryFragment extends BaseFragment {


    public DiscoveryFragment() {
        // Required empty public constructor
    }

    public static DiscoveryFragment newInstance(){
        return new DiscoveryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        ButterKnife.bind(this, view);

        initData();
        setListener();

        return view;
    }

    // 设置监听
    private void setListener() {

    }

    // 初始化
    private void initData() {

    }

}
