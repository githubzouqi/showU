package com.mushiny.www.showU.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.RegexUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.SoftInputUtil;
import com.mushiny.www.showU.util.ToastUtil;

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
 * 彩票信息
 */
public class ToolsLotteryFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    public static final String TAG = ToolsLotteryFragment.class.getSimpleName();

    private String type;
    private CharSequence[] items_type;
    private List<Object[]> typeList;
    private String typeHint;

    private String code;
    private String expect;

    @BindView(R.id.tv_lottery_type)
    TextView tv_lottery_type;
    @BindView(R.id.et_lottery_expect)
    EditText et_lottery_expect;
    @BindView(R.id.ptr_frame_lottery)
    PtrFrameLayout ptr_frame_lottery;
    @BindView(R.id.cardView_lottery_result)
    CardView cardView_lottery_result;
    @BindView(R.id.iv_lottery)
    ImageView iv_lottery;

    @BindView(R.id.tv_lottery_name_expect_time)
    TextView tv_lottery_name_expect_time;
    @BindView(R.id.tv_lottery_openCode)
    TextView tv_lottery_openCode;

    public ToolsLotteryFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param type
     * @return A new instance of fragment ToolsLotteryFragment.
     */
    public static ToolsLotteryFragment newInstance(String type) {
        ToolsLotteryFragment fragment = new ToolsLotteryFragment();
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
            view = inflater.inflate(R.layout.fragment_tools_lottery, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        initListener();

        PtrUtil.newInstance(getContext()).initial(ptr_frame_lottery, PtrUtil.DEFAULT_COLOR);
        ptr_frame_lottery.setMode(PtrFrameLayout.Mode.REFRESH);
        return view;
    }

    /**
     * 监听初始化
     */
    private void initListener() {
        et_lottery_expect.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String str = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(str)){
                        search();
                    }else {
                        ToastUtil.showToast(getContext(), getResources().getString(R.string
                                .str_lottery_expect_hint));
                    }
                    SoftInputUtil.hideKeyboard(et_lottery_expect);
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

        typeList = new ArrayList<>();
        typeList.add(new Object[]{"ssq", "双色球"});
        typeList.add(new Object[]{"qlc", "七乐彩"});
        typeList.add(new Object[]{"fc3d", "福彩3D"});
        typeList.add(new Object[]{"cjdlt", "超级大乐透"});
        typeList.add(new Object[]{"qxc", "七星彩"});
        typeList.add(new Object[]{"pl3", "排列3"});
        typeList.add(new Object[]{"pl5", "排列5"});
        items_type = new CharSequence[typeList.size()];
        for (int i = 0;i < typeList.size();i++){
            String name = String.valueOf(typeList.get(i)[1]);
            items_type[i] = name;
        }

        typeHint = getResources().getString(R.string.str_lottery_type_hint);
    }

    /**
     * 单击事件监听
     * @param view
     */
    @OnClick({R.id.cardView_lottery_type, R.id.btn_lottery_search})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.cardView_lottery_type:// 选择彩票种类
                new AlertDialog.Builder(getContext())
                        .setItems(items_type, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                code = String.valueOf(typeList.get(which)[0]);
                                tv_lottery_type.setText(items_type[which]);
                                dialog.dismiss();
                            }
                        }).create().show();
                break;

            case R.id.btn_lottery_search:// 查询
                search();
                break;
        }
    }

    // 根据期号和彩票种类查询中奖结果
    private void search(){

        if (tv_lottery_type.getText().toString().equals(typeHint)){
            ToastUtil.showToast(getContext(), typeHint);
            return;
        }

        String mExpect = et_lottery_expect.getText().toString();
        if (TextUtils.isEmpty(mExpect)){
            ToastUtil.showToast(getContext(), "请输入彩票期号");
            return;
        }

        expect = mExpect;
        SoftInputUtil.hideKeyboard(et_lottery_expect);
        loadData(expect, code);

    }

    /**
     * 调接口获取中奖结果
     * @param expect
     * @param code
     */
    private void loadData(String expect, String code) {

        PtrUtil.newInstance(getContext()).autoRefresh(ptr_frame_lottery);

        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("expect", expect);
        param.put("code", code);
        Call<ResponseBody> call = anInterface.getLotteryInfo(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ptr_frame_lottery.refreshComplete();
                    String str = new String(response.body().bytes());
                    if (TextUtils.isEmpty(str)){
                        ToastUtil.showToast(getContext(), "返回为空，请检查期号是否正确有效");
                        iv_lottery.setVisibility(View.VISIBLE);
                        cardView_lottery_result.setVisibility(View.GONE);
                        return;
                    }
                    JSONObject obj = new JSONObject(str);
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (code == 1){

                        String openCode = obj.getJSONObject("data").optString("openCode");
                        String name = obj.getJSONObject("data").optString("name");
                        String expect ="第" + obj.getJSONObject("data").optString("expect")
                                + "期";
                        String time = obj.getJSONObject("data").optString("time");

                        tv_lottery_name_expect_time.setText(name + " | " + expect + " | " + time);
                        tv_lottery_openCode.setText(openCode);

                        iv_lottery.setVisibility(View.GONE);
                        cardView_lottery_result.setVisibility(View.VISIBLE);
                    }else {
                        ToastUtil.showToast(getContext(), msg);
                        iv_lottery.setVisibility(View.VISIBLE);
                        cardView_lottery_result.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    iv_lottery.setVisibility(View.VISIBLE);
                    cardView_lottery_result.setVisibility(View.GONE);
                    ptr_frame_lottery.refreshComplete();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_frame_lottery.refreshComplete();
                t.printStackTrace();
                ToastUtil.showToast(getContext(), "请求失败，请稍后再试");
                iv_lottery.setVisibility(View.VISIBLE);
                cardView_lottery_result.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onTitleSet(String mBaseTitle) {
        super.onTitleSet(type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (typeList != null){
            typeList.clear();
        }
    }
}