package com.mushiny.www.showU.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.ToolLogisticsAdapter;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.SoftInputUtil;
import com.mushiny.www.showU.util.ToastUtil;
import com.umeng.commonsdk.debug.E;

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
 * 物流信息
 */
public class ToolLogisticsFragment extends BaseFragment {

    public static final String TAG = ToolLogisticsFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";

    private String type;

    private String logistics_no;
    private String logistics_id;

    private List<Object[]> coLists = new ArrayList<>();
    private CharSequence[] coItems;

    private List<String[]> infoList = new ArrayList<>();
    private ToolLogisticsAdapter adapter;

    @BindView(R.id.et_logistics)
    EditText et_logistics;
    @BindView(R.id.ptr_frame_logistics)
    PtrFrameLayout ptr_frame_logistics;
    @BindView(R.id.iv_logistics)
    ImageView iv_logistics;
    @BindView(R.id.tv_logistics_co)
    TextView tv_logistics_co;
    @BindView(R.id.recycler_view_tools_logistics)
    RecyclerView recycler_view_tools_logistics;

    public ToolLogisticsFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param type
     */
    public static ToolLogisticsFragment newInstance(String type) {
        ToolLogisticsFragment fragment = new ToolLogisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
        }
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_tool_logistics, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        PtrUtil.newInstance(getContext())
                .initialRefreshOnly(ptr_frame_logistics, PtrUtil.DEFAULT_COLOR);
        initListener();
        return view;
    }

    /**
     * 数据初始化
     */
    private void initData() {

        logistics_no = getResources().getString(R.string.str_logistics_hint);
    }

    /**
     * 初始化监听
     */
    private void initListener() {

        et_logistics.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String str = v.getText().toString().trim();
                    logistics_no = str;
                    search();

                    return true;
                }
                return false;
            }
        });

        tv_logistics_co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMulCo = true;
                PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_logistics);
                selectCo();
            }
        });
    }

    /**
     * 根据运单号查询信息
     */
    private void search() {
        if (TextUtils.isEmpty(logistics_no)){
            ToastUtil.showToast(getContext(), getResources().getString(R.string
                    .str_logistics_hint));
            return;
        }
        SoftInputUtil.hideKeyboardOnly(et_logistics);

        if (logistics_no.equals(getResources().getString(R.string.str_logistics_hint))){
            ToastUtil.showToast(getContext(), logistics_no);
            return;
        }

        getCos();
    }

    /**
     * 根据运单号查询匹配的快递公司 - 可能存在多个结果
     */
    private void getCos() {

        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_logistics);
        final NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("logistics_no", logistics_no);
        Call<ResponseBody> call = anInterface.getCos(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (!(code == 1)){
                        ptr_frame_logistics.refreshComplete();
                        ToastUtil.showToast(getContext(), msg);
                        errorUi(msg);
                        return;
                    }
                    JSONArray array = obj.getJSONObject("data").getJSONArray("searchList");
                    if (array != null && array.length() != 0){
                        int length = array.length();
                        coLists.clear();
                        coItems = new CharSequence[length];
                        for (int i = 0;i < length;i++){
                            String logisticsTypeName = array.getJSONObject(i)
                                    .optString("logisticsTypeName");
                            int logisticsTypeId = array.getJSONObject(i)
                                    .optInt("logisticsTypeId");
                            coLists.add(new Object[]{logisticsTypeName, logisticsTypeId});
                            coItems[i] = String.valueOf(logisticsTypeName);
                        }

                        setLogisticsInfo();
                    }else {
                        ptr_frame_logistics.refreshComplete();
                        ToastUtil.showToast(getContext(), "无匹配快递公司，请检查运单号是否正确");
                        errorUi("无匹配快递公司，请检查运单号是否正确");
                        return;
                    }

                }catch (Exception e){
                    ptr_frame_logistics.refreshComplete();
                    e.printStackTrace();
                    errorUi("识别返回数据异常");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_frame_logistics.refreshComplete();
                errorUi("根据运单号识别快递公司失败");
            }
        });

    }

    private boolean isMulCo = false;// 根据运单号是否匹配多个快递公司

    /**
     * 设置快递公司名称
     */
    private void setLogisticsInfo() {

        if (coLists.size() == 1){
            logistics_id = String.valueOf(coLists.get(0)[1]);
            tv_logistics_co.setText(String.valueOf(coLists.get(0)[0]));
            tv_logistics_co.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tv_logistics_co.setClickable(false);
            isMulCo = false;
            getLogisticsInfo();
        }else {
            tv_logistics_co.setClickable(true);
            isMulCo = true;
            selectCo();
        }

    }

    // 根据识别的快递公司结合运单号查询快递信息
    private void getLogisticsInfo(){
        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
//        LogUtil.e("TAG", "logistics_no = " + logistics_no);
//        LogUtil.e("TAG", "logistics_id = " + logistics_id);
        param.put("logistics_no", logistics_no);
        param.put("logistics_id", logistics_id);
        Call<ResponseBody> call = anInterface.getLogisticsInfo(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    LogUtil.e("TAG", "获取快递信息 url = " + call.request().url().toString());
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (code != 1){
                        ptr_frame_logistics.refreshComplete();
                        ToastUtil.showToast(getContext(), msg);
                        errorUi(msg);
                        return;
                    }

                    JSONArray array = obj.getJSONObject("data").getJSONArray("data");
                    if (array != null && array.length() != 0){
                        infoList.clear();
                        for (int i = (array.length() - 1);i >= 0;i--){
                            String time = array.getJSONObject(i).optString("time");
                            String desc = array.getJSONObject(i).optString("desc");
                            infoList.add(new String[]{time, desc});
                        }

                        setAdapter();
                    }else {
                        ToastUtil.showToast(getContext(), "快递信息为空，请稍后再查询");
                        ptr_frame_logistics.refreshComplete();
                        errorUi("快递信息为空，请稍后再查询");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ptr_frame_logistics.refreshComplete();
                    errorUi("获取快递信息异常");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_frame_logistics.refreshComplete();
                errorUi("获取快递信息失败");
            }
        });
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {

        isMulCo = false;
        recycler_view_tools_logistics.setVisibility(View.VISIBLE);
        iv_logistics.setVisibility(View.GONE);
        ptr_frame_logistics.refreshComplete();

        if (adapter != null){
            adapter.notifyDataSetChanged();
            recycler_view_tools_logistics.scrollToPosition(0);
            return;
        }
        adapter = new ToolLogisticsAdapter(getContext(), infoList);
        recycler_view_tools_logistics.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view_tools_logistics.setAdapter(adapter);
        recycler_view_tools_logistics.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 请求 失败/异常 显示UI
     * @param s
     */
    private void errorUi(String s){
        iv_logistics.setVisibility(View.VISIBLE);
        if (!isMulCo){
            tv_logistics_co.setText(s);
            tv_logistics_co.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        recycler_view_tools_logistics.setVisibility(View.GONE);
    }

    private void selectCo(){
        new AlertDialog.Builder(getContext())
                .setTitle("根据运单号识别到多个快递公司，请选择查询")
                .setIcon(R.mipmap.app_icon)
                .setItems(coItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logistics_id = String.valueOf(coLists.get(which)[1]);
                        tv_logistics_co.setText("已选【" + String.valueOf(coLists.get(which)[0])
                                + "】 点击可切换");
                        tv_logistics_co.setTextColor(getResources().getColor(R.color.color_other));
                        getLogisticsInfo();
                        dialog.dismiss();
                    }
                }).setCancelable(false).create().show();
    }

    @OnClick({R.id.btn_logistics})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.btn_logistics:
                logistics_no = et_logistics.getText().toString().trim();
                search();
                break;
        }
    }

    @Override
    public void onTitleSet() {
        baseTitle = type;
        super.onTitleSet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}