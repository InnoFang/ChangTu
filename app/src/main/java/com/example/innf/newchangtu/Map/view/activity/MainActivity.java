package com.example.innf.newchangtu.Map.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.innf.newchangtu.Map.MyOrientationListener;
import com.example.innf.newchangtu.Map.adapter.PositionAdapter;
import com.example.innf.newchangtu.Map.bean.Position;
import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.manager.PositionLab;
import com.example.innf.newchangtu.Map.utils.MyApplication;
import com.example.innf.newchangtu.Map.utils.PermissionListener;
import com.example.innf.newchangtu.Map.view.base.BaseMainActivity;
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
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Author: Inno Fang
 * Time: 2016/8/6 21:19
 * Description: 用户操作主界面
 */
public class MainActivity extends BaseMainActivity{

    /************
     * 用于紧急一键播报
     ************/
    public static String location;
    public static String firstName;
    public static String firstPhone;
    public static String secondName;
    public static String secondPhone;
    public static String thirdName;
    public static String thirdPhone;
    /***************************************/

    private static final String TAG = "MainActivity";
    private static final String KEY_TRACK = "com.example.innf.newchangtu.Map.view.activity.track";
    private static final String KEY_START_POSITION = "com.example.innf.newchangtu.Map.view.activity.start_position";
    private static final String KEY_END_POSITION = "com.example.innf.newchangtu.Map.view.activity.end_position";
    private static final String KEY_TIME_INTERVAL = "com.example.innf.newchangtu.Map.view.activity.time_interval";
    private static final String KEY_PHONE = "com.example.innf.newchangtu.Map.view.activity.phone";
    private static final String KEY_POSITION_LIST = "com.example.innf.newchangtu.Map.view.activity.position_list";

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
    private LinearLayout mEmptyView;

    private int mTimeInterval = 0;
    private Track mTrack = null;
    private String mStartPosition;/*轨迹起点*/
    private String mEndPosition;/*轨迹终点*/
    private String mPhone;/*联系人号码*/
    private String mStatus;   /*用户当前位置*/
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
    private boolean isLastTrackExist = true;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*获得当前用户实例*/
        mUser = User.getCurrentUser(User.class);
        bmobQuery = new BmobQuery<>();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        mToolbar = (Toolbar) findViewById(R.id.action_toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mPositionRecyclerView = (RecyclerView) findViewById(R.id.position_recycler_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mContainMapLayout = (ContainMapLayout) findViewById(R.id.contain_map_layout);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);

      /*创建LinearLayoutManager对象，让RecyclerView的元素倒序显示，并且初始元素不默认从底部开始显示*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mPositionRecyclerView.setLayoutManager(layoutManager);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
        }



        if (savedInstanceState != null) {
            mTrack = (Track) savedInstanceState.getSerializable(KEY_TRACK);
            mStartPosition = savedInstanceState.getString(KEY_START_POSITION);
            mEndPosition = savedInstanceState.getString(KEY_END_POSITION);
            mTimeInterval = savedInstanceState.getInt(KEY_TIME_INTERVAL);
            mPhone = savedInstanceState.getString(KEY_PHONE);
        }

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
//        toggle.setDrawerIndicatorEnabled(false);//设置为false时显示自己设置的图标
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openDrawerLayout();
                toggle.onDrawerOpened(mDrawerLayout);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        initFAB();
        initNavigationView();/*navigationView中Item处理*/
        initMap();
        updateUI();

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

        // 运行时权限请求
        requestRuntimePermiussion(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.READ_LOGS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        }, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                for (String permission : deniedPermission) {
                    showToast("被拒绝权限" + permission + ", 拒绝该权限将无法使用相关功能");
                }
            }
        });
    }

    private void queryLastTrack() {
        /*获取上次记录最后一次track，如果track的最后所在位置是当前位置就提示是否需要继续该track*/
        BmobQuery<Track> query = new BmobQuery<>();
        query.addWhereEqualTo("mUserName", (String) BmobUser.getObjectByKey("username"));
        query.findObjects(new FindListener<Track>() {
            @Override
            public void done(List<Track> list, BmobException e) {
               if (e == null && !list.isEmpty()){
                   Log.i(TAG, "查询上一次track");
                   final Track track = list.get(list.size() - 1);
                   final List<Position> positionList = track.getPositionList();
                   Log.i(TAG, positionList.get(positionList.size() - 1).getEndPosition() + " +++++ " + getStatus());
                   Log.i(TAG, positionList.get(positionList.size() - 1).getEndPosition().equals(getStatus()) + " ");
                   if (positionList.get(positionList.size() - 1).getEndPosition().equals(getStatus())) {
                       Log.i(TAG, "开启dialog");
                       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                       builder.setMessage("发现你上次的轨迹是从这里结束的，是否继续？")
                               .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       mTrack = track;
                                       mTimeInterval = mTrack.getTimeInterval();
                                       Log.i(TAG, "time interval : " + mTimeInterval);
                                       mStartPosition = mTrack.getStartPosition();
                                       mPhone = mTrack.getPhone();
                                       mRemark = mTrack.getRemark();
                                       mPositionList = positionList;
                                       isPositionEmpty(mPositionList);
                                       PositionLab.get(MainActivity.this).setPositionList(mPositionList);
                                       updateUI();
                                       Log.i(TAG, "继续track " + mPositionList.size());
                                       if (mTimeInterval != 0){
                                           Log.i(TAG, "开始记录: 开始");
                                           mHandler.postDelayed(mRunnable, mTimeInterval * 60 * 1000);
                                       }
                                   }
                               })
                               .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       Log.i(TAG, "重新track");
                                   }
                               })
                               .show();
                   }
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
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != drawerLayout) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.fast_send:
                intent = OneClickFastSendActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.start_record:
                Log.i(TAG, "当前位置:" + getStatus());
                intent = StartRecordActivity.newIntent(MainActivity.this, getStatus(), mTrack, mTimeInterval, mPhone);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        PositionLab positionLab = PositionLab.get(this);
        mPositionList = positionLab.getPositionList();
        /*若Adapter存在就不需要再次创建了*/
        if (null == mPositionAdapter) {
            mPositionAdapter = new PositionAdapter(mPositionList);
            mPositionRecyclerView.setAdapter(mPositionAdapter);
        } else {
            mPositionAdapter.setPositionList(mPositionList);
            mPositionAdapter.notifyDataSetChanged();
            mPositionRecyclerView.setAdapter(mPositionAdapter);
        }
        if (mTrack != null) {
            updateTrack(mTrack);
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
        mActionExchangeModel = (FloatingActionButton) findViewById(R.id.action_exchange_model);
        mActionExchangeModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocationMode == MyLocationConfiguration.LocationMode.NORMAL) {
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
                if (null == mTrack) {
                    Snackbar.make(view, "你还没有开始行程，无法拍照", Snackbar.LENGTH_LONG).show();
                } else {
                    if (isSdcardExisting()) {
                        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTrackPhotoUri(mTrack));
                        startActivityForResult(captureImageIntent, REQUEST_TRACK_PHOTO);
                    } else {
                        Snackbar.make(view, "无法读取到SD卡", Snackbar.LENGTH_LONG).show();
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
                    mTrack = new Track();
                    Position position = new Position();
                    position.setPosition(getStatus());
                    position.setMessage("行程开始");
                    mPositionList.add(position);
                    mTrack.setStartPosition(getStatus());
                    mTrack.setPositionList(mPositionList);
                    isPositionEmpty(mPositionList);
                    updateUI();
                } else {
                    addPosition();
                }
                mFloatingActionsMenu.toggle();
            }
        });
    }


    /*定位到我的位置*/
    private void centerToMyLocation() {
//        LatLng latLng = new LatLng(mLongitude, mLatitude);
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    private Uri getTrackPhotoUri(Track track) {
        return Uri.fromFile(getTrackPhotoFile(track));
    }

    private File getTrackPhotoFile(Track track) {
        if (track != null) {
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (null == externalFilesDir) {
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

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
        location = status;
        Log.d(TAG, mStatus);
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
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
                mDrawerLayout.closeDrawers();
                preMenuItem = menuItem;

                Intent intent;
                /*侧滑栏相应item的点击事件*/
                switch (menuItem.getItemId()) {
                    case R.id.drawer_menu_track_record:
                        intent = TrackRecordListActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        break;
                    case R.id.drawer_menu_map_sharing:
                        intent = ShareMapActivity.newIntent(MainActivity.this);
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
                        intent = new Intent(MainActivity.this, TrackActivity.class);
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
        if (null != mRemark) {
            broadcastSMS = getString(R.string.broadcast_sms_with_remark, nowAddress,  startTime, transportation, getRemark());
        } else {
            broadcastSMS = getString(R.string.broadcast_sms, nowAddress, startTime, transportation );
        }
        Log.d(TAG, broadcastSMS);
        return broadcastSMS;
    }

    public String getBroadcastSMSWithoutTrack() {
        String broadcastSMS;
        if (null != mRemark) {
            broadcastSMS = getString(R.string.broadcast_sms_without_track_with_remark, getStatus(), getRemark());
        } else {
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
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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

    public boolean isPositionEmpty(List<Position> positionList) {
        Log.i(TAG, "isPositionEmpty: is called " + positionList.size());
        if (positionList.size() == 0) {
            Log.i(TAG, "position is empty");
            mEmptyView.setVisibility(View.VISIBLE);
            mPositionRecyclerView.setVisibility(View.INVISIBLE);
            return true;
        } else {
            Log.i(TAG, "position isn't empty");
            mEmptyView.setVisibility(View.GONE);
            mPositionRecyclerView.setVisibility(View.VISIBLE);
            return false;
        }
//        return true;
    }

    public void updateTrack(Track track) {
        mEndPosition = getStatus();
        track.setEndPosition(mEndPosition);
        track.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i(TAG, "update successfully!!!");
                } else {
                    Log.i(TAG, "update failed!!!");
                }
            }
        });
    }

    private Handler mHandler = new Handler();

    private boolean isGo = true;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isGo) {
                mHandler.postDelayed(this, mTimeInterval * 60 * 1000);
                addPosition();
            }
            updateUI();
        }
    };

    //    LatLng l1 = new LatLng(38.016244, 112.449318);
//    LatLng l2 = new LatLng(38.016544, 112.450318);
//    showToast("两点距离 ： " + dis);
//    Log.i(TAG, "distance = " + dis);
//    double dis = DistanceUtil.getDistance(l1, l2);
    private void addPosition() {
        BmobQuery<Track> query = new BmobQuery<>();
        query.addWhereEqualTo("mUserName", (String) BmobUser.getObjectByKey("username"));
        query.findObjects(new FindListener<Track>() {
            @Override
            public void done(List<Track> list, BmobException e) {
                final Track track = list.get(list.size() - 1);
                final List<Position> positionList = track.getPositionList();
                Position lastPosition = positionList.get(positionList.size() - 1);
                LatLng lastLl = new LatLng(lastPosition.getLatitude(), lastPosition.getLongitude());
                Position position = new Position();
                position.setPosition(getStatus());
                position.setLatitude(mLatitude);
                position.setLongitude(mLongitude);
                LatLng ll = new LatLng(mLatitude, mLongitude);
                double dis = DistanceUtil.getDistance(lastLl, ll);
                int distance = (int) dis;
                position.setMessage("距离上一个地点" + distance + "米");
                mPositionList.add(position);
                mTrack.setPositionList(mPositionList);
                PositionLab.get(MainActivity.this).setPositionList(mPositionList);
                updateTrack(mTrack);
                updateUI();
            }
        });

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
            mEndPosition = mStartPosition;
            mPhone = data.getStringExtra(StartRecordActivity.EXTRA_PHONE);
            Log.i(TAG, "/*************************/");
            Log.i(TAG, mTrack.toString());
            Log.i(TAG, mStartPosition);
            Log.i(TAG, mEndPosition);
            Log.i(TAG, mTimeInterval + "");
            Log.i(TAG, mPhone);
            Log.i(TAG, "/*************************/");
            Position position = new Position();
            position.setPosition(getStatus());
            position.setMessage("行程开始");
            position.setLatitude(mLatitude);
            position.setLongitude(mLongitude);
            mPositionList.clear();
            mPositionList.add(position);
            mTrack.setPositionList(mPositionList);
            isPositionEmpty(mPositionList);
            updateTrack(mTrack);
            updateUI();
            mHandler.postDelayed(mRunnable, mTimeInterval * 60 * 1000);
        } else if (requestCode == REQUEST_TRACK_PHOTO) {
            if (isSdcardExisting()) {
                uploadImage();
            } else {
                showToast("无法读取到SD卡，无法存储图片");
            }
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("图片上传中...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final BmobFile photo = new BmobFile(getTrackPhotoFile(mTrack));
        photo.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (null == e) {
                    showToast("图片上传成功");
                    mTrack.setPhoto(photo);
                    mTrack.update(mTrack.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (null == e) {
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
//        savedInstanceState.putParcelableArrayList(KEY_POSITION_LIST, mPositionList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_TRACK, mTrack);
        savedInstanceState.putString(KEY_START_POSITION, mStartPosition);
        savedInstanceState.putString(KEY_END_POSITION, mEndPosition);
        savedInstanceState.putInt(KEY_TIME_INTERVAL, mTimeInterval);
        savedInstanceState.putString(KEY_PHONE, mPhone);
        Log.i(TAG, "onRestoreInstanceState: is called");
//        savedInstanceState.putParcelableArrayList(KEY_POSITION_LIST, mPositionList);
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
        /*放大地图倍数，标尺为50米*/
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
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

                mUser.setLatitude(mLatitude);
                mUser.setLongitude(mLongitude);
                mUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                    }
                });
//                mUser.update(new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (null == e){
//                            Log.i(TAG, "update user successfully!!!");
//                        } else {
//                            Log.i(TAG, "fail to update!!!");
//                        }
//                    }
//                });

                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
//                    LatLng xy = new LatLng(bdLocation.getLongitude(),bdLocation.getLatitude());
                    LatLng xy = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mBaiduMap.animateMapStatus(status);
                    firstLocation = false;
                }
//                showToast(bdLocation.getAddrStr());
//                Log.i(TAG, bdLocation.getAddrStr());
                setStatus(bdLocation.getAddrStr());

                /*获取上次记录最后一次track，如果track的最后所在位置是当前位置就提示是否需要继续该track*/
                if (isLastTrackExist && null == mTrack) {
                    queryLastTrack();
                    isLastTrackExist = false;
                }
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


    @Override
    protected void onStart() {
        super.onStart();
        // 如果要显示位置图标,必须先开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            Log.i(TAG, "onStart: is called");
            mLocationClient.start();
        }
        /*开启方向传感器*/
        mMyOrientationListener.start();

        if (mTrack != null && mTrack.getPositionList() != null) {
            mPositionList = mTrack.getPositionList();
            PositionLab.get(this).setPositionList(mPositionList);
        }
        Log.i(TAG, "onStart: is called");
        bmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    /*************************用于紧急一键播报**************************/
                    String _firstName = (String) BmobUser.getObjectByKey("mFirstContractName");
                    String _firstPhone = (String) BmobUser.getObjectByKey("mFirstContractPhone");
                    String _secondName = (String) BmobUser.getObjectByKey("mSecondContractName");
                    String _secondPhone = (String) BmobUser.getObjectByKey("mSecondContractPhone");
                    String _thirdName = (String) BmobUser.getObjectByKey("mThirdContractName");
                    String _thirdPhone = (String) BmobUser.getObjectByKey("mThirdContractPhone");
                    if (_firstName != null){
                        firstName = _firstName;
                    }
                    if (_firstPhone != null){
                        firstPhone = _firstPhone;
                    }
                    if (_secondName != null){
                        secondName = _secondName;
                    }
                    if (_secondPhone != null){
                        secondPhone = _secondPhone;
                    }
                    if (_thirdName != null){
                        thirdName = _thirdName;
                    }
                    if (_thirdPhone != null){
                        thirdPhone = _thirdPhone;
                    }
                    Log.i(TAG, firstName + " " + firstPhone + "; " + secondName + " " + secondPhone + "; " + thirdName + " " + thirdPhone);
                    /******************************************************************/
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.i(TAG, "onResume: is called");
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        Log.i(TAG, "onPause: is called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*停止定位*/
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        mMyOrientationListener.stop();
        Log.i(TAG, "onStop: is called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Log.i(TAG, "onDestroy: is called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
