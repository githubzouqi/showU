package com.mushiny.www.showU.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.ToolRubbishSortAdapter;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.RegexUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.SoftInputUtil;
import com.mushiny.www.showU.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 垃圾分类查询
 */
public class ToolsRubbishSortFragment extends BaseFragment {

    public static final String TOOLS_TYPE = "TOOLS_TYPE";
    public static final String TAG = ToolsRubbishSortFragment.class.getSimpleName();

    private String tools_type;

    @BindView(R.id.et_tool_rubbish_sort) EditText et_tool_rubbish_sort;
    @BindView(R.id.recycler_view_tools_rubbish_sort) RecyclerView recycler_view_tools_rubbish_sort;
    @BindView(R.id.iv_tool_rubbish_sort) ImageView iv_tool_rubbish_sort;
    @BindView(R.id.ptr_tool_rubbish_sort) PtrFrameLayout ptr_tool_rubbish_sort;

    private List<Object[]> rubbishList = new ArrayList<>();
    private ToolRubbishSortAdapter rubbishSortAdapter;

    public ToolsRubbishSortFragment() {
        // Required empty public constructor
    }

    /**
     * 单例携带数据
     */
    public static ToolsRubbishSortFragment newInstance(String tools_type) {
        ToolsRubbishSortFragment fragment = new ToolsRubbishSortFragment();
        Bundle args = new Bundle();
        args.putString(TOOLS_TYPE, tools_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            tools_type = getArguments().getString(TOOLS_TYPE,
                    getResources().getString(R.string.default_title));
        }
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater
                    .inflate(R.layout.fragment_tools_rubbish_sort, container, false);
        }
        ButterKnife.bind(this, view);
        initData();
        setListener();

        return view;
    }

    private void setListener() {
        et_tool_rubbish_sort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String str = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(str)){
                        sortQuery(str);
                    }else {
                        ToastUtil.showToast(getContext(), getResources().getString(R.string
                                .str_tool_rubbish_sort_keyword));
                    }
                    SoftInputUtil.hideKeyboard(et_tool_rubbish_sort);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 数据初始化
     */
    private void initData() {
        PtrUtil.newInstance(getContext()).initial(ptr_tool_rubbish_sort, PtrUtil.DEFAULT_COLOR);
    }


    /**
     * 垃圾分类查询
     */
    private void sortQuery(String keyword) {

        PtrUtil.newInstance(getContext()).autoRefresh(ptr_tool_rubbish_sort);
        Map<String, Object> param = new HashMap<>();
        param.put("name", keyword);
        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Call<ResponseBody> call = anInterface.getRubbishType(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ptr_tool_rubbish_sort.refreshComplete();
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (code == 1){
                        iv_tool_rubbish_sort.setVisibility(View.GONE);
                        recycler_view_tools_rubbish_sort.setVisibility(View.VISIBLE);

                        rubbishList.clear();

                        if (!obj.getJSONObject("data").isNull("aim")){
                            String aim_name = obj.getJSONObject("data").getJSONObject("aim")
                                    .optString("goodsName");
                            String aim_type = obj.getJSONObject("data").getJSONObject("aim")
                                    .optString("goodsType");
                            rubbishList.add(new Object[]{0, aim_name, aim_type});
                        }

                        JSONArray array = obj.getJSONObject("data")
                                .getJSONArray("recommendList");
                        if (array != null && array.length() != 0){
                            for (int i = 0;i < array.length();i++){
                                String recommend_name = array.getJSONObject(i)
                                        .optString("goodsName");
                                String recommend_type = array.getJSONObject(i)
                                        .optString("goodsType");
                                rubbishList.add(new Object[]{1, recommend_name, recommend_type});
                            }
                        }

                        // 设置adapter
                        setAdapter();

                    }else {
                        recycler_view_tools_rubbish_sort.setVisibility(View.GONE);
                        iv_tool_rubbish_sort.setVisibility(View.VISIBLE);
                        new AlertDialog.Builder(getContext()).setMessage(msg).create().show();
//                        ToastUtil.showToast(getContext(), msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    recycler_view_tools_rubbish_sort.setVisibility(View.GONE);
                    iv_tool_rubbish_sort.setVisibility(View.VISIBLE);
                    ToastUtil.showToast(getContext(), "数据异常");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_tool_rubbish_sort.refreshComplete();
                recycler_view_tools_rubbish_sort.setVisibility(View.GONE);
                iv_tool_rubbish_sort.setVisibility(View.VISIBLE);
                ToastUtil.showToast(getContext(), "查询失败：" + t.getMessage());
            }
        });

    }

    /**
     * recyclerView 设置数据源
     */
    private void setAdapter() {

        if (rubbishList.size() == 0){
            ToastUtil.showToast(getContext(), "查询为空");
            return;
        }
        if (rubbishSortAdapter == null){
            rubbishSortAdapter = new ToolRubbishSortAdapter(getContext(), rubbishList);
            recycler_view_tools_rubbish_sort.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            recycler_view_tools_rubbish_sort.setAdapter(rubbishSortAdapter);
            recycler_view_tools_rubbish_sort.setItemAnimator(new DefaultItemAnimator());
        }else {
            rubbishSortAdapter.notifyDataSetChanged();
            recycler_view_tools_rubbish_sort.smoothScrollToPosition(0);
        }

    }

    /**
     * 单击事件监听
     * @param view
     */
    @OnClick({R.id.btn_tool_rubbish_sort})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.btn_tool_rubbish_sort:// 垃圾分类查询
                String keyword = et_tool_rubbish_sort.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)){
                    sortQuery(keyword);
                    SoftInputUtil.hideKeyboard(et_tool_rubbish_sort);
                }else {
                    ToastUtil.showToast(getContext(),
                            getResources().getString(R.string.str_tool_rubbish_sort_keyword));
                    return;
                }
                break;
        }
    }

    @Override
    public void onTitleSet(String mBaseTitle) {
        super.onTitleSet(tools_type);
    }
}