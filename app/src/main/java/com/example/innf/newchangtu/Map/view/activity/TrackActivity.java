package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 足迹 界面，用于显示地图等相关操作
 */
public class TrackActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    private static final String TAG = "TrackActivity";

    private Button strBtn, endBtn;
    private FloatingActionButton mActionStartTrack;
    private FloatingActionButton mActionExchangeMap;
    private FloatingActionsMenu mFloatingActionsMenu;
    private static boolean needdraw=true;

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
    private static MapView mMapView;
    private static BaiduMap mBaiduMap;

    private GeoCoder mSearch;
    public static int index = 0;
    public static LatLng stLatLng = null;

    private String mStatus;
    private boolean firstLocation;

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
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        index = 0;

        init();
        initOnEntityListener();

        initOnStartTraceListener();
        click();

        initFAB();

        client.startTrace(trace, startTraceListener);  // 开启轨迹服务

        Snackbar.make(mMapView, "开始绘制轨迹", Snackbar.LENGTH_LONG).show();
    }

    private void initFAB() {
        mFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floating_actions_menu);

        /*地图类型*/
        mActionExchangeMap = (FloatingActionButton) findViewById(R.id.action_exchange_map);
        mActionExchangeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    mActionExchangeMap.setTitle(getResources().getString(R.string.fab_exchange_map_normal));
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    mActionExchangeMap.setTitle(getResources().getString(R.string.fab_exchange_map_site));
                }
                mFloatingActionsMenu.toggle();
            }
        });

        /*地图模式*/
        mActionStartTrack = (FloatingActionButton) findViewById(R.id.action_end_track);
        mActionStartTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTrack();
                mFloatingActionsMenu.toggle();
            }
        });
    }


    private void endTrack() {
        Snackbar.make(mMapView, "结束绘制轨迹", Snackbar.LENGTH_LONG).show();
        needdraw=false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        strBtn = (Button) findViewById(R.id.star_btn);
        endBtn = (Button) findViewById(R.id.end_btn);
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

    private void init() {

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);

        entityName = getImei(getApplicationContext());  //手机Imei值的获取，用来充当实体名

        client = new LBSTraceClient(getApplicationContext());  //实例化轨迹服务客户端

        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);  //实例化轨迹服务

        client.setInterval(gatherInterval, packInterval);  //设置位置采集和打包周期

    }

    /**
     * 初始化设置实体状态监听器
     */
    private void initOnEntityListener() {
        Log.i("TGA", "初始化成功");

        //实体状态监听器
        entityListener = new OnEntityListener() {

            @Override
            public void onRequestFailedCallback(String arg0) {
                Looper.prepare();
                Toast.makeText(
                        getApplicationContext(),
                        "entity请求失败的回调接口信息：" + arg0,
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


    /**
     * 追踪开始
     */
    private void initOnStartTraceListener() {

        // 实例化开启轨迹服务回调接口
        startTraceListener = new OnStartTraceListener() {
            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                Log.i("TAG", "onTraceCallback=" + arg1);
                if (arg0 == 0 || arg0 == 10006) {
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

    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(TrackActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        //定位
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(TrackActivity.this, strInfo, Toast.LENGTH_LONG).show();
        //click()


        //result保存地理编码的结果 城市-->坐标
    }


    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(TrackActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Toast.makeText(TrackActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();
        Log.i("MYWEIZHI", result.getAddress());
        setStatus(result.getAddress());

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat format;
        format = new SimpleDateFormat("HH:mm:ss");
        String str = format.format(curDate);
        Log.e("time", "time2" + str);
        //将时间位置信息保存到list中

        //result保存翻地理编码的结果 坐标-->城市
    }


    /**
     * 轨迹刷新线程
     *
     * @author BLYang
     */
    private class RefreshThread extends Thread {

        protected boolean refresh = true;

        public void run() {

            while (refresh) {
                queryRealtimeTrack();
                try {
                    Thread.sleep(packInterval * 1000);
                } catch (InterruptedException e) {
                    System.out.println("线程休眠失败");
                }
            }

        }
    }

    /**
     * 查询实时线路
     */
    private void queryRealtimeTrack() {

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
     *
     * @param realtimeTrack
     */
    protected void showRealtimeTrack(String realtimeTrack) {
        if (refreshThread == null || !refreshThread.refresh) {
            return;
        }

        //数据以JSON形式存取
        RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);

        if (realtimeTrackData != null && realtimeTrackData.getStatus() == 0) {

            LatLng latLng = realtimeTrackData.getRealtimePoint();

            if (latLng != null&&needdraw==true) {
                Log.i("TGA", "当前有轨迹点");
                pointList.add(latLng);
                index++;
                if (index % 2 == 0) {
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(latLng));


                }


                if (index == 1) {
                    stLatLng = latLng;
                    Log.d("FFF", stLatLng.toString());


                }
                drawRealtimePoint(latLng);
            } else {

                Toast.makeText(getApplicationContext(), "当前无轨迹点", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * 画出实时线路点
     *
     * @param point
     */
    private void drawRealtimePoint(LatLng point) {
        Log.i("TGA", "绘制成功");
        mBaiduMap.clear();
        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ct_map_location);
        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);


        if (pointList.size() >= 2 && pointList.size() <= 6000) {
            polyline = new PolylineOptions().width(10).color(Color.GREEN).points(pointList);
        }

        addMarker();
    }


    private void addMarker() {
        if (stLatLng != null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ct_map_location_32);
            OverlayOptions option = new MarkerOptions().position(stLatLng).icon(bitmap);
            mBaiduMap.addOverlay(option);
        }

        if (msUpdate != null) {
            mBaiduMap.setMapStatus(msUpdate);
        }

        if (polyline != null) {
            mBaiduMap.addOverlay(polyline);
        }

        if (overlay != null) {
            mBaiduMap.addOverlay(overlay);
        }


    }


    /**
     * 启动刷新线程
     *
     * @param isStart
     */
    private void startRefreshThread(boolean isStart) {

        if (refreshThread == null) {
            refreshThread = new RefreshThread();
        }

        refreshThread.refresh = isStart;

        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }


    }


    /**
     * 获取手机的Imei码，作为实体对象的标记值
     *
     * @param context
     * @return
     */

    private String getImei(Context context) {
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
    protected void onStart() {
        // 如果要显示位置图标,必须先开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        needdraw=true;
        super.onStart();
        Log.i("TGATGA", "start");
    }

    @Override

    protected void onStop() {
        mBaiduMap.setMyLocationEnabled(false);
        Log.i("TGATGA", "stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TGATGA", "destroy");

        mMapView.onDestroy();

        index = 0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TGATGA", "onresume");
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TGATGA", "onpause");
        mMapView.onPause();
    }


}