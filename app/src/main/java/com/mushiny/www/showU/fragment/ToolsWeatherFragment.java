package com.mushiny.www.showU.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.mushiny.www.showU.adapter.ToolWeatherAdapter;
import com.mushiny.www.showU.interfaces.GrantListener;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.util.LogUtil;
import com.mushiny.www.showU.util.PermissionUtil;
import com.mushiny.www.showU.util.PtrUtil;
import com.mushiny.www.showU.util.Retrofit2Util;
import com.mushiny.www.showU.util.SoftInputUtil;
import com.mushiny.www.showU.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 工具-天气查询
 */
public class ToolsWeatherFragment extends BaseFragment {

    public static final String TOOLS_TYPE = "TOOLS_TYPE";
    public static final String TAG = ToolsWeatherFragment.class.getSimpleName();
    private static final int WHAT_LOCATE = 0x110;

    @BindView(R.id.iv_tool_weather)
    ImageView iv_tool_weather;
    @BindView(R.id.et_tool_weather)
    EditText et_tool_weather;
    @BindView(R.id.recycler_view_tools_weather)
    RecyclerView recycler_view_tools_weather;
    @BindView(R.id.ptr_tool_weather)
    PtrFrameLayout ptr_tool_weather;

    private List<String[]> weatherList = new ArrayList<>();
    private ToolWeatherAdapter adapter;
    private PermissionUtil permissionUtil;

    private String type;

    List<Address> result = null;

    public ToolsWeatherFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_LOCATE:
                    LogUtil.e("TAG", "result size = " + result.size());
                    String locality = result.get(0).getLocality();
                    ToastUtil.showToast(getContext(), "当前定位城市为：" + locality);
                    weatherQuery(locality);
                    break;
            }
        }
    };

    /**
     * 单例
     * @param type
     */
    public static ToolsWeatherFragment newInstance(String type) {
        ToolsWeatherFragment fragment = new ToolsWeatherFragment();
        Bundle args = new Bundle();
        args.putString(TOOLS_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(TOOLS_TYPE, getResources().getString(R.string.app_name));
        }
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tools_weather, container, false);
        }
        ButterKnife.bind(this, view);

        initData();
        setListener();

        return view;
    }

    @Override
    public void onTitleSet() {
        baseTitle = type;
        super.onTitleSet();
    }

    /**
     * 数据初始化
     */
    private void initData() {

        permissionUtil = new PermissionUtil(getContext(), this);
        // 位置危险权限申请
        permissionUtil.apply(PermissionUtil.ACCESS_FINE_LOCATION, new GrantListener() {
            @Override
            public void onAgree() {
                location();// 定位
            }

            @Override
            public void onDeny() {

            }

            @Override
            public void onDenyNotAskAgain() {

            }
        });

    }

    /**
     * 定位获取当前城市
     */
    private void location() {
        LocationManager locationManager = (LocationManager) getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        String providerName = locationManager.getBestProvider(criteria, true);
//        String providerName = LocationManager.NETWORK_PROVIDER;
        String providerName = "";
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            providerName = LocationManager.NETWORK_PROVIDER;
        }else if (providerList.contains(LocationManager.GPS_PROVIDER)){
            providerName = LocationManager.GPS_PROVIDER;
        }else {
            ToastUtil.showToast(getContext(), "provider 获取失败");
            return;
        }
        // 权限复验
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.showToast(getContext(), "权限未授权，请先授权UHello定位权限");
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                1000,
//                1,
//                new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        //GPS信息发生改变时，更新位置
////                        locationUpdates(location);
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//
//                    }
//                });
        Location location = locationManager.getLastKnownLocation(providerName);
        if (location != null){
            final double longitude = location.getLongitude();// 经度
            final double latitude = location.getLatitude();// 纬度
            LogUtil.e("TAG", "longitude = " + longitude);
            LogUtil.e("TAG", "latitude = " + latitude);

            // 因为这里 Geocoder对象的 getFromLocation 方法，源码说明中建议在工作线程执行 getFromLocation方法
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        result = geocoder.getFromLocation(latitude, longitude, 1);
                        handler.sendEmptyMessage(WHAT_LOCATE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            ToastUtil.showToast(getContext(), "UHello 定位失败");
        }

    }

    private void setListener() {
        et_tool_weather.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String site = v.getText().toString().trim();
                    if (!TextUtils.isEmpty(site)){
                        weatherQuery(site);
                    }else {
                        ToastUtil.showToast(getContext(), getResources().getString(R.string
                                .str_tool_weather_hint));
                    }
                    SoftInputUtil.hideKeyboard(et_tool_weather);
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * 请求危险权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PermissionUtil.ACCESS_FINE_LOCATION:
                permissionUtil.singleApplyResult(requestCode, grantResults);
                break;
        }
    }

    /**
     * 点击事件监听
     * @param view
     */
    @OnClick({R.id.btn_tool_weather})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.btn_tool_weather:// 天气搜索
                String site = et_tool_weather.getText().toString().trim();
                if (!TextUtils.isEmpty(site)){
                    weatherQuery(site);
                    SoftInputUtil.hideKeyboard(et_tool_weather);
                }else {
                    ToastUtil.showToast(getContext(),
                            getResources().getString(R.string.str_tool_weather_hint));
                }
                break;
        }
    }

    /**
     * 查询城市天气数据
     * @param site
     */
    private void weatherQuery(String site){

        PtrUtil.newInstance(getContext()).initial(ptr_tool_weather, PtrUtil.DEFAULT_COLOR);
        PtrUtil.newInstance(getContext()).autoRefresh(ptr_tool_weather);

        NetworkInterface anInterface = Retrofit2Util.createWithROLLHeader(NetworkInterface.class);
        // okhttp 会自动编码中文
        String encodeSite = site;
        try {
            encodeSite = URLEncoder.encode(site, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = anInterface.getWeather(site);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    ptr_tool_weather.refreshComplete();
                    JSONObject obj = new JSONObject(new String(response.body().bytes()));
                    int code = obj.optInt("code");
                    String msg = obj.optString("msg");
                    if (code == 1){

                        String address = obj.getJSONObject("data").optString("address");
                        JSONArray array = obj.getJSONObject("data").getJSONArray("forecasts");
                        if (array != null && array.length() > 0){
                            weatherList.clear();
                            for (int i = 0;i < array.length();i++){
                                String date = array.getJSONObject(i).optString("date");
                                String dayOfWeek = array.getJSONObject(i)
                                        .optString("dayOfWeek");
                                dayOfWeek = getWeekInfo(dayOfWeek);
                                String dayWeather = array.getJSONObject(i)
                                        .optString("dayWeather");
                                String nightWeather = array.getJSONObject(i)
                                        .optString("nightWeather");
                                String dayTemp = array.getJSONObject(i).optString("dayTemp");
                                String nightTemp = array.getJSONObject(i)
                                        .optString("nightTemp");
                                String dayWindDirection = array.getJSONObject(i)
                                        .optString("dayWindDirection");
                                String nightWindDirection = array.getJSONObject(i)
                                        .optString("nightWindDirection");
                                String dayWindPower = array.getJSONObject(i)
                                        .optString("dayWindPower");
                                String nightWindPower = array.getJSONObject(i)
                                        .optString("nightWindPower");

                                String main = "";
                                if (i == 0){
                                    // 今天
                                    main = address + "\n" + date + "（今天） " + dayOfWeek;
                                }else {
                                    // 未来几天
                                    main = address + "\n" + date + " " + dayOfWeek;
                                }
                                String temp = dayWeather + " / " + nightWeather + "\n\n"
                                        + dayTemp + " / " + nightTemp;
                                String wind = dayWindDirection + " " + dayWindPower + " / "
                                        + nightWindDirection + " " + nightWindPower;

                                weatherList.add(new String[]{main, temp, wind});
                            }
                        }

                        setAdapter();
                        visible(recycler_view_tools_weather);
                        gone(iv_tool_weather);

                    }else {
                        gone(recycler_view_tools_weather);
                        visible(iv_tool_weather);
                        ToastUtil.showToast(getContext(), msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    gone(recycler_view_tools_weather);
                    visible(iv_tool_weather);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ptr_tool_weather.refreshComplete();
                gone(recycler_view_tools_weather);
                visible(iv_tool_weather);
                ToastUtil.showToast(getContext(), "查询失败");

            }
        });

    }

    /**
     * recyclerView 设置数据
     */
    private void setAdapter() {
        if (weatherList.size() != 0){
            if (adapter != null){
                adapter.notifyDataSetChanged();
                recycler_view_tools_weather.smoothScrollToPosition(0);
                return;
            }

            adapter = new ToolWeatherAdapter(getContext(), weatherList);
            recycler_view_tools_weather.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler_view_tools_weather.setAdapter(adapter);
            recycler_view_tools_weather.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private String getWeekInfo(String dayOfWeek) {
        String str = "星期一";
        switch (dayOfWeek){
            case "1": str = "星期一";
                break;
            case "2": str = "星期二";
                break;
            case "3": str = "星期三";
                break;
            case "4": str = "星期四";
                break;
            case "5": str = "星期五";
                break;
            case "6": str = "星期六";
                break;
            case "7": str = "星期天";
                break;
        }
        return str;
    }

    private void visible(View view){
        view.setVisibility(View.VISIBLE);
    }

    private void gone(View view){
        view.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}