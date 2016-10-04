package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.Trace;
import com.example.innf.newchangtu.Map.GsonService;
import com.example.innf.newchangtu.Map.RealtimeTrackData;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 足迹 界面，用于显示地图等相关操作
 */
public class TrackActivity extends BaseActivity {

    private static final String TAG = "TrackActivity";

    private Button strBtn,endBtn;


    int gatherInterval = 3;  //位置采集周期 (s)
    int packInterval = 10;  //打包周期 (s)
    String entityName = null;  // entity标识
    long serviceId = 124718;// 鹰眼服务ID
    int traceType = 2;  //轨迹服务类型
    private static OnStartTraceListener startTraceListener = null;  //开启轨迹服务监听器


    private static OnEntityListener entityListener = null;
    private RefreshThread refreshThread = null;  //刷新地图线程以获取实时点
    private static MapStatusUpdate msUpdate = null;
    private static BitmapDescriptor realtimeBitmap;  //图标
    private static OverlayOptions overlay;  //覆盖物
    private static List<LatLng> pointList = new ArrayList<LatLng>();  //定位点的集合
    private static PolylineOptions polyline = null;  //路线覆盖物


    private Trace trace;  // 实例化轨迹服务
    private LBSTraceClient client;  // 实例化轨迹服务客户端
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private boolean firstLocation;
    private GeoCoder mSearch;

    private String mStatus;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TrackActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);


        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        initSearchGeoPoint();
        initOnEntityListener();

        initOnStartTraceListener();
        click();

        client.startTrace(trace, startTraceListener);  // 开启轨迹服务

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_broadcast:
                sendSMS();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sendSMS() {
        Log.i("Send SMS", "");

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
//            smsIntent.putExtra("address", mPhone);
            smsIntent.putExtra("sms_body", "我现在的位置为：" + getStatus());
        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "发送失败", Toast.LENGTH_LONG).show();
        }
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    private void click() {
        strBtn= (Button) findViewById(R.id.star_btn);
        endBtn= (Button) findViewById(R.id.end_btn);
        strBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearch.geocode(new GeoCodeOption().city("太原").address("中北大学"));


            }
        });
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearch.geocode(new GeoCodeOption().city("太原").address("胜利桥东"));

            }
        });

    }


    private void initSearchGeoPoint() {
        mBaiduMap = mMapView.getMap();
        mSearch= GeoCoder.newInstance();// 创建地理编码检索实例

        // 设置地理编码检索监听者
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(TrackActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
//                mBaiduMap.clear();

                //构建markerOption，用于在地图上添加marker ，先找到位置，在添加图标
                mBaiduMap.addOverlay(new MarkerOptions().position(geoCodeResult.getLocation())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ct_map_location_32)));
                //地图位置移动到当前位置
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(geoCodeResult
                        .getLocation()));
                String strInfo = String.format("纬度：%f 经度：%f",
                        geoCodeResult.getLocation().latitude, geoCodeResult.getLocation().longitude);


             //   Toast.makeText(TrackActivity.this, strInfo, Toast.LENGTH_LONG).show();
            }

            //释放地理编码检索实例
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(TrackActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

//                mBaiduMap.addOverlay(new MarkerOptions().position(reverseGeoCodeResult.getLocation())
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.icon_st)));
//                //加上覆盖物
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult
                        .getLocation()));
                //定位
                //Toast.makeText(TrackActivity.this, reverseGeoCodeResult.getAddress(),
                   //     Toast.LENGTH_LONG).show();

                Log.i("TGA",reverseGeoCodeResult.getAddress());
                setStatus(reverseGeoCodeResult.getAddress());
                //result保存翻地理编码的结果 坐标-->城市
            }
        });

    }




    private void init() {


        entityName = getImei(getApplicationContext());  //手机Imei值的获取，用来充当实体名

        client = new LBSTraceClient(getApplicationContext());  //实例化轨迹服务客户端

        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);  //实例化轨迹服务

        client.setInterval(gatherInterval, packInterval);  //设置位置采集和打包周期



        mMapView= (MapView) findViewById(R.id.map);
        mBaiduMap=mMapView.getMap();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        // 设置自定义图标
//        BitmapDescriptor myMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_st);
//        MyLocationConfiguration config = new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.FOLLOWING, true, myMarker);
//        mBaiduMap.setMyLocationConfigeration(config);

        //地图上比例尺
        //mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);

        //定位初始化
        locationClient = new LocationClient(this);
        firstLocation =true;
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(bdLocation==null||mMapView==null)
                    return;
                //构造定位数据
                MyLocationData locData=new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(100).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
                    Log.i("III","kkkkkkkkkkkkkkkkkk");
                    firstLocation = false;
                    LatLng xy = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());


                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mSearch.geocode(new GeoCodeOption().city("太原").address("中北大学"));
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(xy));
                    mBaiduMap.animateMapStatus(status);

                    showToast(bdLocation.getAddrStr());
                    Log.i(TAG, bdLocation.getAddrStr());
                }

            }
        });

    }
    /**
     * 初始化设置实体状态监听器
     */
    private void initOnEntityListener(){
        Log.i("TGA","初始化成功");

        //实体状态监听器
        entityListener = new OnEntityListener(){

            @Override
            public void onRequestFailedCallback(String arg0) {
                Looper.prepare();
                Toast.makeText(
                        getApplicationContext(),
                        "entity请求失败的回调接口信息："+arg0,
                        Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }

            @Override
            public void onQueryEntityListCallback(String arg0) {
                /**
                 * 查询实体集合回调函数，此时调用实时轨迹方法
                 */
                showRealtimeTrack(arg0);
            }

        };
    }



    /** 追踪开始 */
    private void initOnStartTraceListener() {

        // 实例化开启轨迹服务回调接口
        startTraceListener = new OnStartTraceListener() {
            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                Log.i("TAG", "onTraceCallback=" + arg1);
                if(arg0 == 0 || arg0 == 10006){
                    startRefreshThread(true);
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
                Log.i("TAG", "onTracePushCallback=" + arg1);
            }
        };



    }


    /**
     * 轨迹刷新线程
     * @author BLYang
     */
    private class RefreshThread extends Thread{

        protected boolean refresh = true;

        public void run(){

            while(refresh){
                queryRealtimeTrack();
                try{
                    Thread.sleep(packInterval * 1000);
                }catch(InterruptedException e){
                    System.out.println("线程休眠失败");
                }
            }

        }
    }

    /**
     * 查询实时线路
     */
    private void queryRealtimeTrack(){

        String entityName = this.entityName;
        String columnKey = "";
        int returnType = 0;
        int activeTime = 0;
        int pageSize = 10;
        int pageIndex = 1;

        this.client.queryEntityList(
                serviceId,
                entityName,
                columnKey,
                returnType,
                activeTime,
                pageSize,
                pageIndex,
                entityListener
        );

    }


    /**
     * 展示实时线路图
     * @param realtimeTrack
     */
    protected void showRealtimeTrack(String realtimeTrack){

        if(refreshThread == null || !refreshThread.refresh){
            return;
        }

        //数据以JSON形式存取
        RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);

        if(realtimeTrackData != null && realtimeTrackData.getStatus() ==0){

            LatLng latLng = realtimeTrackData.getRealtimePoint();

            if(latLng != null){
                Log.i("TGA","当前有轨迹点");
                pointList.add(latLng);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                drawRealtimePoint(latLng);
            }
            else{

                Toast.makeText(getApplicationContext(), "当前无轨迹点", Toast.LENGTH_LONG).show();
            }

        }

    }

    /**
     * 画出实时线路点
     * @param point
     */
    private void drawRealtimePoint(LatLng point){
        Log.i("TGA","绘制成功");


        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        overlay = new MarkerOptions().position(point)
                .zIndex(9).draggable(true);

        if(pointList.size() >= 2  && pointList.size() <= 1000){
            Log.i("TGA","绘制hongse成功");
            polyline = new PolylineOptions().width(10).color(Color.GREEN).points(pointList);
        }

        addMarker();

    }


    private void addMarker(){

        if(msUpdate != null){
            mBaiduMap.setMapStatus(msUpdate);
        }

        if(polyline != null){
            mBaiduMap.addOverlay(polyline);
        }

        if(overlay != null){
            mBaiduMap.addOverlay(overlay);
        }


    }


    /**
     * 启动刷新线程
     * @param isStart
     */
    private void startRefreshThread(boolean isStart){

        if(refreshThread == null){
            refreshThread = new RefreshThread();
        }

        refreshThread.refresh = isStart;

        if(isStart){
            if(!refreshThread.isAlive()){
                refreshThread.start();
            }
        }
        else{
            refreshThread = null;
        }


    }


    /**
     * 获取手机的Imei码，作为实体对象的标记值
     * @param context
     * @return
     */

    private String getImei(Context context){
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }





    @Override
    protected void onStart()
    {
        // 如果要显示位置图标,必须先开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted())
        {
            locationClient.start();
        }
        super.onStart();
    }
    @Override
    protected void onStop()
    {
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /*获取当前位置测试方法*/
    public String getNowAddress() {
        return "中北大学";
    }
}

