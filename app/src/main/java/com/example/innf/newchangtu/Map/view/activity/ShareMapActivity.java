package com.example.innf.newchangtu.Map.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.innf.newchangtu.Map.bean.Contracts;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShareMapActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ShareMapActivity";
    public static final String EXTRA_CONTRACTS = "com.example.innf.changtu.view.activity.contracts";
    private static final int REQUEST_CONTRACTS = 0;

    private User mMe;
    private User mOppsite;
    private Contracts mContracts;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private boolean firstLocation = true;
    private BitmapDescriptor mCurrentMarker;
    private SwitchButton mSwitchButton;
    private FloatingActionButton mExchagneMapFAB;

    /*定位*/
    private double mLatitude;
    private double mLongitude;
    private BitmapDescriptor mMarker;

    private boolean isGo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMe = BmobUser.getCurrentUser(User.class);

        mExchagneMapFAB = (FloatingActionButton) findViewById(R.id.exchange_map_fab);
        mExchagneMapFAB.setOnClickListener(this);

        boolean isShow = (boolean) BmobUser.getObjectByKey("mIsShare");
        mSwitchButton = (SwitchButton) findViewById(R.id.switch_button);
        mSwitchButton.setChecked(isShow);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String str = isChecked ? "共享位置" : "取消共享位置";
                showToast(str);
                mMe.setShare(isChecked);
                mMe.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                    }
                });
            }
        });
        initMap();
        initMarker();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                User user = (User) extraInfo.getSerializable("user");

                InfoWindow infoWindow;
                TextView textView = new TextView(ShareMapActivity.this);

                textView.setText(mOppsite.getName());
                textView.setBackground(getResources().getDrawable(R.drawable.location_tips));
                textView.setPadding(30, 20, 30, 50);
                textView.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y -= 47;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);
                infoWindow = new InfoWindow(textView, ll, 0);
                mBaiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange_map_fab:
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                break;
        }
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.share_map);
        mBaiduMap = mMapView.getMap();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }


        //地图上比例尺
        //mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(true);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18f);
        mBaiduMap.setMapStatus(msu);
        //定位初始化
        locationClient = new LocationClient(this);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation bdLocation) {
                if (bdLocation == null || mMapView == null)
                    return;
                //构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(100).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

                mMe.setLatitude(bdLocation.getLatitude());
                mMe.setLongitude(bdLocation.getLongitude());
                mMe.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (null == e) {
                            Log.i(TAG, "Me ===>>> Latitude : " + bdLocation.getLatitude() + ", Longitude : " + mLongitude);
                        }
                    }
                });

                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
                    firstLocation = false;
                    LatLng xy = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mBaiduMap.animateMapStatus(status);
                }

            }
        });

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ShareMapActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.relate_friends:
                Intent intent = RelateFriendsActivity.newIntent(this);
                startActivityForResult(intent, REQUEST_CONTRACTS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        // 如果要显示位置图标,必须先开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        super.onStop();
        Log.i(TAG, "onStop: is called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.clear();
        isGo = false;
        Log.i(TAG, "onDestroy: is called");
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

    private void addOverLay(User user) {
        Log.i(TAG, "addOverLay: is called");
        mBaiduMap.clear();
        Marker marker = null;
        LatLng latLng = null;
        OverlayOptions options;
        mLatitude = user.getLatitude();
        mLongitude = user.getLongitude();
        Log.i(TAG, "user name == " + user.getName());
        Log.i(TAG, "Oppsite ====>>> mLatitude : " + mLatitude + " mLongitude : " + mLongitude);
        latLng = new LatLng(mLatitude, mLongitude);
        options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
        marker = (Marker) mBaiduMap.addOverlay(options);
        Bundle arg = new Bundle();
        arg.putSerializable("User", user);
        marker.setExtraInfo(arg);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isGo) {
                addOverLay(mOppsite);
                mHandler.postDelayed(mRunnable, 5 * 1000);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CONTRACTS) {
            mContracts = (Contracts) data.getSerializableExtra(RelateFriendsActivity.EXTRA_CONTRACTS);
            Log.i(TAG, "contract : " + mContracts.getName());
            final BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("name", mContracts.getName());
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    Log.i(TAG, "[ list size ]= " + list.size());
                    if (e == null) {
                        mOppsite = list.get(0);
                        Log.i(TAG, "contract user == " + mOppsite.getName());
                        Log.i(TAG, "is share = " + mOppsite.isShare());
                        Log.i(TAG, "contract user id " + mOppsite.getObjectId());
                        boolean isShare = (boolean) mOppsite.getObjectByKey("mIsShare");
                        if (isShare) {
                            Log.i(TAG, "latitude : " + mLatitude + " , longitude : " + mLongitude);
                            Log.i(TAG, "onActivityResult: user : " + mContracts.getContractUser().getObjectId());
                            addOverLay(mOppsite);
                            mHandler.postDelayed(mRunnable, 1000);
                            Log.i(TAG, mContracts.toString());
                        } else {
                            showToast("对方没有开启地图共享");
                        }
                    } else {
                        Log.i(TAG, e.getMessage());
                    }
                }
            });
        }
    }
}
