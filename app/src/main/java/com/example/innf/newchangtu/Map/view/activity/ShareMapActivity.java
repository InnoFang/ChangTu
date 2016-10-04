package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.innf.newchangtu.Map.bean.Contracts;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;
import com.kyleduo.switchbutton.SwitchButton;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ShareMapActivity extends BaseActivity {
    public static final String EXTRA_CONTRACTS = "com.example.innf.changtu.view.activity.contracts";

    private SwitchButton mSwitchButton;

    private Contracts mContracts;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private boolean firstLocation;
    private BitmapDescriptor mCurrentMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_map);
        initMap();

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContracts = (Contracts) getIntent().getSerializableExtra(EXTRA_CONTRACTS);

        mSwitchButton = (SwitchButton) findViewById(R.id.switch_button);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String str = isChecked ? "共享位置": "取消共享位置";
                showToast(str);
            }
        });
    }

    private void initMap() {
        mMapView= (MapView) findViewById(R.id.share_map);
        mBaiduMap=mMapView.getMap();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }


        //地图上比例尺
        //mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18f);
        mBaiduMap.setMapStatus(msu);
        //定位初始化
        locationClient = new LocationClient(this);
        firstLocation =true;
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
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
                    firstLocation = false;
                    LatLng xy = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mBaiduMap.animateMapStatus(status);
                }

            }
        });

    }

    public static Intent newIntent(Context context, Contracts contracts){
        Intent intent = new Intent(context, ShareMapActivity.class);
        intent.putExtra(EXTRA_CONTRACTS, contracts);
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
        switch (item.getItemId()){
            case R.id.delete_contracts:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog dialog = builder.setTitle("你确定删除该联系人吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContracts.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (null == e){
                                            showToast("删除成功");
                                        }else{
                                            showToast("删除失败");
                                        }
                                    }
                                });
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

                break;
        }
        return super.onOptionsItemSelected(item);
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
}
