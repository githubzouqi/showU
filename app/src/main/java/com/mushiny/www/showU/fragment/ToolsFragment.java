package com.mushiny.www.showU.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.ToolAdapter;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.QueryMap;

/**
 * 工具页
 */
public class ToolsFragment extends BaseFragment {

    public final static String TAG = ToolsFragment.class.getSimpleName();

    private static final String tool_weather = "天气查询";
    private static final String tool_rubbish_sort = "垃圾分类查询";
    private static final String tool_lottery = "彩票信息";
    private static final String tool_logistics = "物流信息";

    private int count = 1;
    private List<Object[]> toolList = new ArrayList<>();
    private ToolAdapter toolAdapter;

    @BindView(R.id.recycler_view_tools) RecyclerView recycler_view_tools;
    @BindView(R.id.tv_tool_fine_statement) TextView tv_tool_fine_statement;
    @BindView(R.id.tv_tool_fine_statement_author) TextView tv_tool_fine_statement_author;
    @BindView(R.id.ptr_frame_tool) PtrFrameLayout ptr_frame_tool;
    @BindView(R.id.cv_tool_fine_statement) CardView cv_tool_fine_statement;

    public ToolsFragment() {
        // Required empty public constructor
    }

    public static ToolsFragment newInstance() {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_tools, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        setPtrFrame();
        setListener();

        return view;
    }

    /**
     * 刷新框架设置
     */
    private void setPtrFrame() {
        PtrUtil.newInstance(getContext()).set_1_BaseSetting(ptr_frame_tool);
        PtrUtil.newInstance(getContext()).set_2_MaterialHeader(ptr_frame_tool, PtrUtil.DEFAULT_COLOR);
        PtrUtil.newInstance(getContext()).set_3_Footer(ptr_frame_tool);
        ptr_frame_tool.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    /**
     * 数据初始化
     */
    private void initData() {

        // 设置标题内容
        baseTitle = getResources().getString(R.string.str_mine);
        // 精美语句初始化设置
        tv_tool_fine_statement.setText(getResources()
                .getString(R.string.str_tool_fine_statement_default));
        tv_tool_fine_statement_author.setText(getResources().getString(R.string.default_title));

        toolList.add(new Object[]{tool_weather, R.mipmap.tool_weather});
        toolList.add(new Object[]{tool_rubbish_sort, R.mipmap.tool_rubbish_sort});
        toolList.add(new Object[]{tool_lottery, R.mipmap.tool_lottery});
        toolList.add(new Object[]{tool_logistics, R.mipmap.tool_logistics});

        loadData();
    }

    /**
     * 设置监听
     */
    private void setListener() {

        // 每日精美语句 长按更新
        cv_tool_fine_statement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dailyWord();
                return true;
            }
        });

    }

    /**
     * 加载页面数据
     */
    private void loadData() {

        if (toolList.size() != 0){
            toolAdapter = new ToolAdapter(getContext(), toolList);
            recycler_view_tools.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recycler_view_tools.setAdapter(toolAdapter);
            recycler_view_tools.setItemAnimator(new DefaultItemAnimator());
        }

        if (toolAdapter != null){
            // recyclerView item点击事件
            toolAdapter.setOnItemClick(new MyItemClickInterface() {
                @Override
                public void OnRecyclerViewItemClick(View itemView, int position) {
                    String content = String.valueOf(toolList.get(position)[0]);
                    switch (content){
                        case tool_weather:// 天气查询

                            ToolsWeatherFragment weatherFragment =
                                    ToolsWeatherFragment.newInstance(tool_weather);
                            showFragment(getActivity(), ToolsFragment.this,
                                    weatherFragment, ToolsWeatherFragment.TAG);
                            break;
                        case tool_rubbish_sort:// 垃圾分类查询
                            ToolsRubbishSortFragment toolsRubbishSortFragment =
                                    ToolsRubbishSortFragment.newInstance(tool_rubbish_sort);
                            showFragment(getActivity(), ToolsFragment.this,
                                    toolsRubbishSortFragment, ToolsRubbishSortFragment.TAG);
                            break;
                        case tool_lottery:// 彩票信息
                            ToolsLotteryFragment lotteryFragment = ToolsLotteryFragment
                                    .newInstance(tool_lottery);
                            showFragment(getActivity(), ToolsFragment.this, lotteryFragment
                            , ToolsLotteryFragment.TAG);
                            break;
                        case tool_logistics:// 物流信息

                            break;
                    }
                }
            });
        }

        dailyWord();

    }

    /**
     * 每日精美语句
     */
    private void dailyWord() {

        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_tool);// 自动刷新

        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("count", count);
        Call<ResponseBody> call = anInterface.getDailyWord(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ptr_frame_tool.refreshComplete();
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (code == 1){
                        String content = obj.getJSONArray("data").getJSONObject(0)
                                .optString("content");
                        String author = obj.getJSONArray("data").getJSONObject(0)
                                .optString("author");
                        if (TextUtils.isEmpty(author)){
                            author = getResources().getString(R.string.default_title);
                        }
                        tv_tool_fine_statement.setText(content.trim());
                        tv_tool_fine_statement_author.setText(author);
                    }else {
                        ToastUtil.showToast(getContext(), msg);
                    }
                }catch (Exception e){
                    ptr_frame_tool.refreshComplete();
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "数据异常：" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_frame_tool.refreshComplete();
                ToastUtil.showToast(getContext(), "请求失败：" + t.getMessage());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}