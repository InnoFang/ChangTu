package com.example.innf.newchangtu.Map.view.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.innf.newchangtu.Map.MyOrientationListener;
import com.example.innf.newchangtu.Map.adapter.PositionAdapter;
import com.example.innf.newchangtu.Map.bean.Position;
import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.model.PositionLab;
import com.example.innf.newchangtu.Map.utils.MyApplication;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.Map.widget.ContainMapLayout;
import com.example.innf.newchangtu.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Author: Inno Fang
 * Time: 2016/8/6 21:19
 * Description: 用户操作主界面
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_TRACK = "com.example.innf.newchangtu.Map.view.activity.track";
    private static final String KEY_START_POSITION = "com.example.innf.newchangtu.Map.view.activity.start_position";
    private static final String KEY_END_POSITION = "com.example.innf.newchangtu.Map.view.activity.end_position";
    private static final String KEY_TIME_INTERVAL = "com.example.innf.newchangtu.Map.view.activity.time_interval";
    private static final String KEY_PHONE = "com.example.innf.newchangtu.Map.view.activity.phone";

    /*多的全局Context*/
    private static final Context mContext = MyApplication.getContext();
    private static final int REQUEST_TRACK_PHOTO = 0;
    private static final int REQUEST_CODE = 1;
    private User mUser;
    private BmobQuery<User> bmobQuery;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FloatingActionsMenu mFloatingActionsMenu;
    private FloatingActionButton mActionPhotoRecord;
    private FloatingActionButton mActionFastLocal;
    private FloatingActionButton mActionFastBroadcast;
    private FloatingActionButton mActionExchangeMap;
    private FloatingActionButton mActionExchangeModel;
    private RecyclerView mPositionRecyclerView;
    private PositionAdapter mPositionAdapter;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ContainMapLayout mContainMapLayout;
    private DrawerLayout mDrawerLayout;

    private int mTimeInterval;
    private Track mTrack;
    private String mStartPosition;/*轨迹起点*/
    private String mEndPosition;/*轨迹终点*/
    private String mPhone;/*联系人号码*/
    private String mStatus;   /*用户当前位置*/
    private String mNewStatus;
    private String mRemark; /*用户播报备注信息*/
    private List<Position> mPositionList = PositionLab.get(this).getPositionList();

    /*地图相关*/
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private boolean firstLocation = true;
    private BitmapDescriptor mCurrentMarker;
    private GeoCoder mSearch;
    private MyLocationConfiguration.LocationMode mLocationMode;
    private double mLatitude;
    private double mLongitude;
    /*自定义图标*/
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*获得当前用户实例*/
        mUser = User.getCurrentUser(User.class);
        bmobQuery = new BmobQuery<>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        mToolbar = (Toolbar) findViewById(R.id.action_toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mPositionRecyclerView = (RecyclerView) findViewById(R.id.position_recycler_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mContainMapLayout = (ContainMapLayout) findViewById(R.id.contain_map_layout);

      /*创建LinearLayoutManager对象，让RecyclerView的元素倒序显示，并且初始元素不默认从底部开始显示*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mPositionRecyclerView.setLayoutManager(layoutManager);

        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState != null){
            mTrack = (Track) savedInstanceState.getSerializable(KEY_TRACK);
            mStartPosition = savedInstanceState.getString(KEY_START_POSITION);
            mEndPosition = savedInstanceState.getString(KEY_END_POSITION);
            mTimeInterval = savedInstanceState.getInt(KEY_TIME_INTERVAL);
            mPhone = savedInstanceState.getString(KEY_PHONE);
        }

        initFAB();
        initNavigationView();/*navigationView中Item处理*/
        updateUI();
        initMap();
        initSearchGeoPoint();


        mContainMapLayout.setCollapsingToolbarLayout(mCollapsingToolbarLayout);

        /*在Draw Menu 上显示用户信息*/
        bmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    TextView username = (TextView) findViewById(R.id.user_name_text_view);
                    String name = (String) BmobUser.getObjectByKey("username");
                    username.setText(name);
                    TextView mobilePhoneNumber = (TextView) findViewById(R.id.user_phone_number_text_view);
                    String phone = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
                    mobilePhoneNumber.setText(phone);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != drawerLayout) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.fast_send:
                Toast.makeText(MainActivity.this, R.string.fab_fast_broadcast, Toast.LENGTH_SHORT).show();
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pi = PendingIntent.getActivity(mContext, 0,
                        new Intent(MainActivity.this, MainActivity.class), 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                        .setTicker("紧急播报")
                        .setContentTitle("标题")
                        .setContentText("内容")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                notificationManager.notify(0, notification);

                showToast("紧急播报");
//                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//                smsIntent.setData(Uri.parse("smsto:"));
//                smsIntent.setType("vnd.android-dir/mms-sms");
//                if (null != mTrack) {
//                    smsIntent.putExtra("address", mPhone);
//                    smsIntent.putExtra("sms_body", getBroadcastSMS());
//                } else {
//                    smsIntent.putExtra("sms_body", getBroadcastSMSWithoutTrack());
//                }
//                try {
//                    startActivity(smsIntent);
//                    finish();
//                    Log.i("Finished sending SMS...", "");
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(MainActivity.this,
//                            "发送失败", Toast.LENGTH_LONG).show();
//                }
                break;
            case R.id.start_record:
                Log.d(TAG, "当前位置:" + getStatus());
                Intent intent = StartRecordActivity.newIntent(MainActivity.this, getStatus());
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        if (mPositionAdapter == null) {
            mPositionAdapter = new PositionAdapter(mPositionList);
            mPositionRecyclerView.setAdapter(mPositionAdapter);
        } else {
            mPositionAdapter.setPositionList(mPositionList);
        }
    }

    /*初始化FAB按钮*/
    private void initFAB() {

        mFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floating_actions_menu);

        /*地图类型*/
        mActionExchangeMap = (FloatingActionButton) findViewById(R.id.action_exchange_map);
        mActionExchangeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL){
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
        mActionExchangeModel = (FloatingActionButton) findViewById(R.id.action_exchange_model);
        mActionExchangeModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocationMode == MyLocationConfiguration.LocationMode.NORMAL){
                    mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                    mActionExchangeModel.setTitle(getResources().getString(R.string.fab_exchange_model_common));
                } else {
                    mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                    mActionExchangeModel.setTitle(getResources().getString(R.string.fab_exchange_model_compass));
                }
                mFloatingActionsMenu.toggle();
            }
        });

         /*快速播报*/
        mActionFastBroadcast = (FloatingActionButton) findViewById(R.id.action_fast_broadcast);
        mActionFastBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fastSend();
                mFloatingActionsMenu.toggle();
            }
        });

        /*用于跳转至“拍照记录”功能的按钮*/
        mActionPhotoRecord = (FloatingActionButton) findViewById(R.id.action_fab_recognizes_license_plate);
        mActionPhotoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null == mTrack){
                    Snackbar.make(view, "你还没有开始行程，无法拍照" ,Snackbar.LENGTH_LONG).show();
                } else {
                    if (isSdcardExisting()){
                        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTrackPhotoUri(mTrack));
                        startActivityForResult(captureImageIntent, REQUEST_TRACK_PHOTO);
                    } else {
                        Snackbar.make(view, "无法读取到SD卡" ,Snackbar.LENGTH_LONG).show();
                    }
                }
                mFloatingActionsMenu.toggle();
            }
        });

        /*用于跳转至“快速定位”功能的按钮，主要是让用户选择目的地，出发点等相关操作*/
        mActionFastLocal = (FloatingActionButton) findViewById(R.id.action_fast_local);
        mActionFastLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*定位到我的位置*/
                centerToMyLocation();
                if (null == mTrack) {
                    Position position = new Position();
                    position.setPosition(getStatus());
                    position.setMessage(getStatusMessage());
                    mPositionList.add(position);
                    updateUI();
                } else {
                    showToast(getString(R.string.fab_fast_local));
                    Position position = new Position();
                    position.setPosition(getStatus());
                    position.setMessage(getStatusMessage());
                    mPositionList.add(position);
                    updateUI();
                }
                mFloatingActionsMenu.toggle();
            }
        });
    }


    /*定位到我的位置*/
    private void centerToMyLocation(){
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    private Uri  getTrackPhotoUri(Track track){
        return Uri.fromFile(getTrackPhotoFile(track));
    }

    private File getTrackPhotoFile(Track track) {
        if (track != null){
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (null == externalFilesDir){
                Log.d(TAG, "+------+\n|  为空  |\n+------+");
                return null;
            }
            Log.d(TAG, track.getPhotoFilename(track));
            return new File(externalFilesDir, track.getPhotoFilename(track));
        }
        return null;
    }

    private boolean isSdcardExisting() {
        //判断SD卡是否存在
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void fastSend() {
        Log.d(TAG, "fastSend: is called");
        String[] remark = {
                "我需要你的帮助。", "我在这里堵车了。", "自定义备注"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this, android.R.layout.simple_list_item_1, remark
        );
        View view1 = View.inflate(MainActivity.this, R.layout.show_dialog, null);
        final ListView listView = (ListView) view1.findViewById(R.id.dialog_list_view);
        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.setTitle("备注信息")
                .setView(view1)
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendSMS();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    final EditText editText = new EditText(MainActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder
                            .setTitle("自定义备注")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    setRemark(editText.getText().toString());
                                    sendSMS();
                                    Log.d(TAG, editText.getText().toString());
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                } else {
                    setRemark(listView.getItemAtPosition(i).toString());
                    sendSMS();
                }
                dialog.dismiss();
            }
        });
    }


    /**
     * 快速定位测试方法
     **/


    public String getStatusMessage() {/*获得当前位置相关信息*/
        return "距离上一个目的地1公里，用时5分钟";
    }

    /******************/

    protected void sendSMS() {
        Log.i("Send SMS", "");

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        if (null != mTrack) {
            smsIntent.putExtra("address", mPhone);
            smsIntent.putExtra("sms_body", getBroadcastSMS());
        } else {
            smsIntent.putExtra("sms_body", getBroadcastSMSWithoutTrack());
        }
        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "发送失败", Toast.LENGTH_LONG).show();
        }
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    /*侧滑栏*/
    private void initNavigationView() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            MenuItem preMenuItem; /*用于辨别此前是否已有选中条目*/

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
               /*首先将选中条目变为选中状态，即checked为true，后关闭Drawer，以前选中Item需要变为未选中状态*/
                if (preMenuItem != null) {
                    preMenuItem.setCheckable(false);
                }
                menuItem.setCheckable(true);
                drawerLayout.closeDrawers();
                preMenuItem = menuItem;

                Intent intent;
                /*侧滑栏相应item的点击事件*/
                switch (menuItem.getItemId()) {
                    case R.id.drawer_menu_track_record:
                        intent = TrackRecordListActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    case R.id.drawer_menu_bind_on_account:
                        intent = RelateFriendsActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    case R.id.drawer_menu_personal_setting:
                        intent = SettingActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    case R.id.drawer_menu_nearby:
                        intent = NearbyActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    case R.id.drawer_menu_track:
                        intent = TrackActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                }
                return true;
            }

        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }


    public String getBroadcastSMS() {
        String detailAddress = mTrack.getDetailTrack();/*获取详细地址*/
        String startTime = mTrack.getTrackDate() + " " + mTrack.getTrackTime();/*获取出发时间*/
        String transportation = mTrack.getTransportation();/*获取交通工具*/
        String nowAddress = getStatus();/*获取当前位置*/
        String broadcastSMS;
        if (null != mRemark){
            broadcastSMS= getString(R.string.broadcast_sms_with_remark, detailAddress, startTime, transportation, nowAddress, getRemark());
        }else {
            broadcastSMS= getString(R.string.broadcast_sms, detailAddress, startTime, transportation, nowAddress);
        }
        Log.d(TAG, broadcastSMS);
        return broadcastSMS;
    }

    public String getBroadcastSMSWithoutTrack() {
        String broadcastSMS;
        if (null != mRemark){
            broadcastSMS = getString(R.string.broadcast_sms_without_track_with_remark, getStatus(), getRemark());
        }else{
            broadcastSMS = getString(R.string.broadcast_sms_without_track, getStatus());
        }
        Log.d(TAG, broadcastSMS);
        return broadcastSMS;
    }

    /************************/


    /**
     * 在点击一次退出
     **/
    private long sendSMSTime = 0;
    private int powerButtonClickTimes = 0; /*记录电源键在规定时间内点击次数*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        sendSMSTime = System.currentTimeMillis();/*记录点击电源键时间*/
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog dialog = builder.
                        setTitle(R.string.app_name)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("你确定退出畅途吗？")
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_POWER && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - sendSMSTime) < 2000) {
                powerButtonClickTimes++;
                if (powerButtonClickTimes == 4) {
                    Log.d(TAG, powerButtonClickTimes + "");
                    showToast("消息发送");
                }
            } else {
                powerButtonClickTimes = 0;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE) {
            mTrack = (Track) data.getSerializableExtra(StartRecordActivity.EXTRA_TRACK);
            mTimeInterval = data.getIntExtra(StartRecordActivity.EXTRA_TIME_INTERVAL, 0);
            mStartPosition = data.getStringExtra(StartRecordActivity.EXTRA_START);
            mEndPosition = data.getStringExtra(StartRecordActivity.EXTRA_END);
            mPhone = data.getStringExtra(StartRecordActivity.EXTRA_PHONE);
            Log.d(TAG, "/*************************/");
            Log.d(TAG, mTrack.toString());
            Log.d(TAG, mStartPosition);
            Log.d(TAG, mEndPosition);
            Log.d(TAG, mTimeInterval + "");
            Log.d(TAG, mPhone);
            Log.d(TAG, "/*************************/");
        }
        else if (requestCode == REQUEST_TRACK_PHOTO) {
            if (isSdcardExisting()){
                upload();
            } else {
                showToast("无法读取到SD卡，无法存储图片");
            }
        }

    }

    private void upload() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("图片上传中...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final BmobFile photo = new BmobFile(getTrackPhotoFile(mTrack));
        photo.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(null == e){
                    showToast("图片上传成功");
                    mTrack.setPhoto(photo);
                    mTrack.update(mTrack.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (null == e){
                                showToast("保存图片成功");
                            } else {
                                showToast("保存图片失败" + e.getMessage());
                            }
                        }
                    });
                } else {
                    showToast("图片上传失败" + e.getMessage());
                }
                progressDialog.cancel();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState: is called");
        savedInstanceState.putSerializable(KEY_TRACK, mTrack);
        savedInstanceState.putString(KEY_START_POSITION, mStartPosition);
        savedInstanceState.putString(KEY_END_POSITION, mEndPosition);
        savedInstanceState.putInt(KEY_TIME_INTERVAL, mTimeInterval);
        savedInstanceState.putString(KEY_PHONE, mPhone);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_TRACK, mTrack);
        savedInstanceState.putString(KEY_START_POSITION, mStartPosition);
        savedInstanceState.putString(KEY_END_POSITION, mEndPosition);
        savedInstanceState.putInt(KEY_TIME_INTERVAL, mTimeInterval);
        savedInstanceState.putString(KEY_PHONE, mPhone);
    }

    public int getTimeInterval() {
        return mTimeInterval;
    }

    public Track getTrack() {
        return mTrack;
    }


    /*让  轨迹  获得地点坐标*/
    public String getPosition() {
        return null;
    }

    /****
     * 百度地图显示
     *****/

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.main_map_view);
        mBaiduMap = mMapView.getMap();

        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
//        mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_st);
//        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
//        mBaiduMap.setMyLocationConfigeration(config);


        //地图上比例尺
        //mMapView.showScaleControl(false);
        // 隐藏缩放控件

        mMapView.showZoomControls(true);
        /*放大地图倍数，标尺为500米*/
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        //定位初始化
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;/*定位模式*/
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null || mMapView == null)
                    return;
                //构造定位数据
                MyLocationData locData = new MyLocationData
                        .Builder()
                        .direction(mCurrentX)
                        .accuracy(bdLocation.getRadius())
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude())
                        .build();
                mBaiduMap.setMyLocationData(locData);

                /*设置自定义图标*/
                MyLocationConfiguration configuration = new MyLocationConfiguration(mLocationMode, true, mIconLocation);
                mBaiduMap.setMyLocationConfigeration(configuration);

                /*更新经纬度*/
                mLatitude = bdLocation.getLatitude();
                mLongitude = bdLocation.getLongitude();

                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
                    LatLng xy = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mBaiduMap.animateMapStatus(status);
                    firstLocation = false;
                }
                showToast(bdLocation.getAddrStr());
//                Log.i(TAG, bdLocation.getAddrStr());
            }
        });


        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

         /*初始化图标*/
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);

        mMyOrientationListener = new MyOrientationListener(mContext);

        mMyOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    public void setStatus(String status) {
        mStatus = status;
        Log.d(TAG, mStatus);

    }

    public String getStatus() {
        return "山西省太原市中北大学";
//        return mStatus;
    }

    /*经纬度解析*/
    private void initSearchGeoPoint() {
        mSearch = GeoCoder.newInstance();// 创建地理编码检索实例

        // 设置地理编码检索监听者
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    showToast("抱歉，未能找到结果");
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

                Log.d(TAG, "onGetGeoCodeResult: ");
                showToast(strInfo);
            }

            //释放地理编码检索实例
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    showToast("抱歉，未能找到结果");
                    return;
                }

//                mBaiduMap.addOverlay(new MarkerOptions().position(reverseGeoCodeResult.getLocation())
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.icon_st)));
//                //加上覆盖物
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult
                        .getLocation()));
                //定位
                showToast(reverseGeoCodeResult.getAddress());
                Log.i(TAG, reverseGeoCodeResult.getAddress());
                mNewStatus = reverseGeoCodeResult.getAddress();

                //result保存翻地理编码的结果 坐标-->城市
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 如果要显示位置图标,必须先开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
            /*开启方向传感器*/
            mMyOrientationListener.start();
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        /*停止定位*/
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        mMyOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
