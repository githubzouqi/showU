package com.mushiny.www.showU.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.adapter.FutureWeatherAdapter;
import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.Future5WeatherEntity;
import com.mushiny.www.showU.entity.WeatherCityListEntity;
import com.mushiny.www.showU.interfaces.NetworkInterface;
import com.mushiny.www.showU.table.WeatherCityTable;
import com.mushiny.www.showU.table.WeatherTypeTable;
import com.mushiny.www.showU.util.SoftInputUtil;
import com.mushiny.www.showU.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 天气预报页面
 */
public class WeatherFragment extends BaseFragment {

    public final static String TAG = WeatherFragment.class.getSimpleName();
    private static final int WHAT_CITY_LIST = 0x40;
    private static final int WHAT_TYPE_LIST = 0x41;
    private static final int WHAT_CITY_WEATHER = 0x42;

    @BindView(R.id.loading_data)MaterialProgressBar loading_data;
    @BindView(R.id.et_city_name)EditText et_city_name;
    @BindView(R.id.tv_search_city)TextView tv_search_city;
    @BindView(R.id.tv_realtime_info)TextView tv_realtime_info;
    @BindView(R.id.tv_realtime_temperature)TextView tv_realtime_temperature;
    @BindView(R.id.tv_realtime_direct)TextView tv_realtime_direct;
    @BindView(R.id.tv_realtime_power)TextView tv_realtime_power;
    @BindView(R.id.tv_realtime_humidity)TextView tv_realtime_humidity;
    @BindView(R.id.tv_realtime_aqi)TextView tv_realtime_aqi;
    @BindView(R.id.recycler_view_weather)RecyclerView recycler_view_weather;

    private List<WeatherCityListEntity.ResultBean> cityList = new ArrayList<>();// 保存接口返回的原始数据
    private JSONObject objectType = null;// 天气种类列表接口返回源数据
    private JSONObject objectCityWeather = null;// 查询城市天气接口返回源数据

    private List<WeatherCityTable> weather_city_table = new ArrayList<>();// 保存存入数据库中表的数据集合，这里是支持的城市列表数据
    private List<WeatherTypeTable> weather_type_table = new ArrayList<>();// 天气种类列表数据
    private List<WeatherCityTable> search_city_data = new ArrayList<>();// 保存从数据库读取的符合条件的数据：支持的城市列表
    private List<Future5WeatherEntity> future5WeatherList = new ArrayList<>();// 某城市未来5天的天气

    private String str_search_content = "";// 保存用户输入的地区名称

    private CharSequence[] cityItems;// 保存用户输入匹配的支持城市列表集合

    private String city = "";// 保存要查询城市的城市id
    private FutureWeatherAdapter adapter = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_CITY_LIST:// 获取支持城市列表成功

                    saveCityTableData();

                    break;
                case WHAT_TYPE_LIST:// 保存天气种类列表数据到数据库

                    saveTypeTableData();

                    break;
                case WHAT_CITY_WEATHER:// 城市的天气

                    showCityWeather();

                    break;
            }
        }
    };

    /**
     * 展示城市的天气
     * 当前时间以及未来5天天气
     */
    private void showCityWeather() {

        try {

            JSONObject object_realtime = objectCityWeather.getJSONObject("result")
                    .getJSONObject("realtime");
            String realtime_info = object_realtime.optString("info");
            String realtime_temperature = object_realtime.optString("temperature") + "℃"
                    + "（温度）";
            String realtime_humidity = object_realtime.optString("humidity") + "（湿度）";
            String realtime_direct = object_realtime.optString("direct") + "（风向）";
            String realtime_power = object_realtime.optString("power") + "（风力）";
            String realtime_aqi = getAqiInfo(object_realtime.optString("aqi"));

            // 当前天气
            tv_realtime_info.setText(realtime_info);
            tv_realtime_temperature.setText(realtime_temperature);
            tv_realtime_humidity.setText(realtime_humidity);
            tv_realtime_direct.setText(realtime_direct);
            tv_realtime_power.setText(realtime_power);
            tv_realtime_aqi.setText(realtime_aqi);

            // 最近5天天气
            JSONArray array_future = objectCityWeather.getJSONObject("result")
                    .getJSONArray("future");
            future5WeatherList.clear();
            for (int i = 0;i < array_future.length();i++){
                JSONObject object = array_future.getJSONObject(i);
                Future5WeatherEntity entity = new Future5WeatherEntity();
                String date = object.optString("date");
                String temperature = object.optString("temperature");
                String weather = object.optString("weather");
                String direct = object.optString("direct");

                entity.setFuture_date(date);
                entity.setFuture_temperature(temperature);
                entity.setFuture_weather(weather);
                entity.setFuture_direct(direct);

                future5WeatherList.add(entity);
            }

            adapter = new FutureWeatherAdapter(getContext(), future5WeatherList);
            recycler_view_weather.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            recycler_view_weather.setAdapter(adapter);
            recycler_view_weather.setItemAnimator(new DefaultItemAnimator());


        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.showToast(getContext(), "城市天气数据解析异常：" + e.getMessage());
        }

    }

    /**
     * 空气质量指数信息整理
     * @param aqi
     * @return
     */
    private String getAqiInfo(String aqi) {
        String aqi_info = "";
        if (!TextUtils.isEmpty(aqi)){
            int int_aqi = Integer.parseInt(aqi);
            if (0 <= int_aqi && int_aqi <= 50){
                aqi_info = "优";
            }
            if (51 <= int_aqi && int_aqi <= 100){
                aqi_info = "良";
            }
            if (101 <= int_aqi && int_aqi <= 150){
                aqi_info = "轻度污染";
            }
            if (151 <= int_aqi && int_aqi <= 200){
                aqi_info = "中度污染";
            }
            if (201 <= int_aqi && int_aqi <= 300){
                aqi_info = "重度污染";
            }
            if (300 <= int_aqi){
                aqi_info = "严重污染";
            }

            return int_aqi + "  " + aqi_info + "（空气质量指数）";
        }
        return aqi_info;
    }

    /**
     * 数据库保存 天气种类列表数据
     */
    private void saveTypeTableData() {

        try {

            weather_type_table.clear();
            JSONArray arrayType = objectType.getJSONArray("result");
            if (arrayType != null){
                for (int i = 0;i < arrayType.length();i++){

                    String wid = arrayType.getJSONObject(i).optString("wid");
                    String weather = arrayType.getJSONObject(i).optString("weather");

                    WeatherTypeTable typeTable = new WeatherTypeTable();
                    typeTable.setWid(wid);
                    typeTable.setWeather(weather);

                    weather_type_table.add(typeTable);
                }
            }else {
                ToastUtil.showToast(getContext(), "天气种类为空");
                return;
            }

            if (weather_type_table.size() != 0){
                LitePal.saveAllAsync(weather_type_table).listen(new SaveCallback() {
                    @Override
                    public void onFinish(boolean success) {
                        if (success){
                            ToastUtil.showToast(getContext(), "一次性成功读取保存天气种类数据"
                                    + weather_type_table.size() + "条");

                            getWeather(city);
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.showToast(getContext(), "天气种类数据保存异常：" + e.getMessage());
        }

    }

    /**
     * 数据库保存支持的城市列表数据
     * 支持的城市列表数据
     */
    private void saveCityTableData() {

        try {

            weather_city_table.clear();
            if (cityList.size() != 0){
                for (int i = 0;i < cityList.size();i++){
                    WeatherCityListEntity.ResultBean bean = cityList.get(i);
                    String district_id = bean.getId();
                    String province = bean.getProvince();
                    String city = bean.getCity();
                    String district = bean.getDistrict();

                    WeatherCityTable table = new WeatherCityTable();
                    table.setDistrict_id(district_id);
                    table.setProvince(province);
                    table.setCity(city);
                    table.setDistrict(district);

                    weather_city_table.add(table);
                }
            }else {
                ToastUtil.showToast(getContext(), "无支持的城市 可能相关接口服务已暂停");
                return;
            }

            if (weather_city_table.size() != 0){
//                LitePal.saveAll(weather_city_table);
                LitePal.saveAllAsync(weather_city_table).listen(new SaveCallback() {
                    @Override
                    public void onFinish(boolean success) {
                        if (success){
                            ToastUtil.showToast(getContext(), "一次性读取保存支持城市列表数据完成，已成功保存 " +
                                    weather_city_table.size() + " 条数据。");
                        }
                    }
                });
            }


        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.showToast(getContext(), "支持城市列表数据保存异常：" + e.getMessage());
        }

    }

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(){
        return new WeatherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);

        initData();

        setListeners();

        return view;
    }

    // 初始化
    private void initData() {

        // 第一次进入该界面，获取支持的城市列表数据并保存进数据库
        if (!cityDataIsExist()){
            getCities();
        }

    }

    /**
     * 数据库是否已经保存了支持的城市列表数据
     * false 表示未保存、true表示已经保存了
     * @return
     */
    private boolean cityDataIsExist() {

        WeatherCityTable city_table_first = LitePal.findFirst(WeatherCityTable.class);
        if (city_table_first != null){
            return true;
        }
        return false;
    }

    /**
     * 数据库是否已经保存了天气种类列表
     * @return
     */
    private boolean typeDataIsExist(){

        WeatherTypeTable typeTable = LitePal.findFirst(WeatherTypeTable.class);
        if (typeTable != null){
            return true;
        }
        return false;
    }

    // 设置监听
    private void setListeners() {

        // editText 设置输入法搜索动作监听
        et_city_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchCityWeather();
                    SoftInputUtil.hideKeyboard(et_city_name);
                    return true;
                }
                return false;
            }
        });

    }

    // 控件单击事件绑定
    @OnClick({R.id.btn_city_search})
    public void doClick(View view){

        switch (view.getId()){
            case R.id.btn_city_search:// 搜索城市天气

                searchCityWeather();
                SoftInputUtil.hideKeyboard(et_city_name);

                break;
        }

    }

    // 搜索城市天气
    private void searchCityWeather() {
        str_search_content = et_city_name.getText().toString().trim();
        if (TextUtils.isEmpty(str_search_content) || str_search_content.length() < 2){
            ToastUtil.showToast(getContext(), "请输入要查询的 城市/地区");
            return;
        }

        if (cityDataIsExist()){
            String param = "%"+ str_search_content.substring(0,2) +"%";
            search_city_data = LitePal.where("province LIKE ? or city LIKE ? or district LIKE ?", param, param, param).find(WeatherCityTable.class);

            if (search_city_data.size() == 0){
                ToastUtil.showToast(getContext(), "请填写正确的省份，城市或者地区名称");
                return;
            }

            int length = search_city_data.size();
            cityItems = new CharSequence[length];
            for (int i = 0;i < length;i++){
                cityItems[i] = search_city_data.get(i).getProvince() + "-" +
                        search_city_data.get(i).getCity()
                        + "-" + search_city_data.get(i).getDistrict();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("请选择要查询的 城市/地区")
                    .setIcon(R.mipmap.app_icon)
                    .setItems(cityItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            tv_search_city.setText(cityItems[which]);
                            city = search_city_data.get(which).getDistrict_id();
                            et_city_name.setText("");
                            et_city_name.clearFocus();
                            dialog.dismiss();

                            getWeather(city);
                        }
                    }).create().show();

        }
    }

    /**
     * 根据城市查询天气
     * @param city
     */
    private void getWeather(String city) {

        loading_data.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIS_JUHE_CN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("city", city);
        param.put("key", Constants.JUHE_WEATHER_FORECAST_APP_KEY);

        Call<ResponseBody> call = networkInterface.getCityWeather(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading_data.setVisibility(View.GONE);

                try {
                    String strCityWeather = new String(response.body().bytes());
                    objectCityWeather = new JSONObject(strCityWeather);

                    int error_code = objectCityWeather.optInt("error_code");
                    String reason = objectCityWeather.optString("reason");

                    if (error_code != 0){
                        // 异常
                        ToastUtil.showToast(getContext(), reason);
                        return;
                    }else {
                        // success
                        Message message = handler.obtainMessage();
                        message.what = WHAT_CITY_WEATHER;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "城市天气数据异常：" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading_data.setVisibility(View.GONE);
                ToastUtil.showToast(getContext(), t.getMessage());
            }
        });

    }

    /**
     * 获取 天气种类列表
     * 后面发现该接口是多余的，可以不需要使用到
     */
    private void getTypes() {
        loading_data.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIS_JUHE_CN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("key", Constants.JUHE_WEATHER_FORECAST_APP_KEY);

        Call<ResponseBody> call = networkInterface.getWeatherTypes(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                loading_data.setVisibility(View.GONE);
                try {
                    String strType = new String(response.body().bytes());
                    objectType = new JSONObject(strType);
                    int error_code = objectType.optInt("error_code");
                    String reason = objectType.optString("reason");

                    if (error_code != 0){
                        // 异常
                        ToastUtil.showToast(getContext(), reason);
                        return;
                    }else {
                        // success
                        Message message = handler.obtainMessage();
                        message.what = WHAT_TYPE_LIST;
                        handler.sendMessage(message);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "天气种类数据异常：" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading_data.setVisibility(View.GONE);
                ToastUtil.showToast(getContext(), t.getMessage());
            }
        });
    }

    /**
     * 获取 支持城市列表
     */
    private void getCities() {

        loading_data.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.APIS_JUHE_CN)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);
        Map<String, Object> param = new HashMap<>();
        param.put("key", Constants.JUHE_WEATHER_FORECAST_APP_KEY);

        Call<WeatherCityListEntity> call = networkInterface.getCities(param);
        call.enqueue(new Callback<WeatherCityListEntity>() {
            @Override
            public void onResponse(Call<WeatherCityListEntity> call, Response<WeatherCityListEntity>
                    response) {
                loading_data.setVisibility(View.GONE);

                try {
                    WeatherCityListEntity entity = response.body();
                    int error_code = entity.getError_code();
                    String reason = entity.getReason();

                    if (error_code != 0){
                        // 异常
                        ToastUtil.showToast(getContext(), reason);
                        return;
                    }else {
                        // 成功
                        cityList = entity.getResult();
                        Message message = handler.obtainMessage();
                        message.what = WHAT_CITY_LIST;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast(getContext(), "支持城市列表数据异常：" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<WeatherCityListEntity> call, Throwable t) {
                loading_data.setVisibility(View.GONE);
                ToastUtil.showToast(getContext(), t.getMessage());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
