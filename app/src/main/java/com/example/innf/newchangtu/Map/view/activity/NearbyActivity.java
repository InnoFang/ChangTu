package com.example.innf.newchangtu.Map.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.innf.newchangtu.Map.adapter.NearbyAdapter;
import com.example.innf.newchangtu.Map.bean.Nearby;
import com.example.innf.newchangtu.Map.manager.NearbyLab;
import com.example.innf.newchangtu.Map.utils.AssetsUtils;
import com.example.innf.newchangtu.Map.utils.RegexValidateUtil;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Author: Inno Fang
 * Time: 2016/8/21 19:12
 * Description:
 */

public class NearbyActivity extends BaseActivity{

    private static final String TAG = "NearbyActivity";
    private static final int REFRESH_COMPLETE = 0x110;
    private static final int REQUEST_CODE = 1;


    private FloatingActionButton mExchangeFAB;
    private TextView mNearbyNameTextView;
    private TextView mEmptyTextView;
    public RecyclerView mNearbyRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private NearbyAdapter mNearbyAdapter;

    public int mItemPosition;
    //    private static NearbyActivity sNearbyActivity;
//
//    private NearbyActivity(){
//
//    }
//
//    public static NearbyActivity get(){
//        if (null == sNearbyActivity){
//            sNearbyActivity = new NearbyActivity();
//        }
//        return sNearbyActivity;
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        String address = (String) BmobUser.getObjectByKey("address");
        if (null == address){
            address = "位置未指定";
        }
        mNearbyNameTextView = (TextView) findViewById(R.id.nearby_name_text_view);
        mNearbyNameTextView.setText(address);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mExchangeFAB = (FloatingActionButton) findViewById(R.id.action_exchange);
        mExchangeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddressPicker();
            }
        });

        mNearbyRecyclerView = (RecyclerView) findViewById(R.id.nearby_recycler_view);
        /*创建LinearLayoutManager对象，让RecyclerView的元素倒序显示，并且初始元素不默认从底部开始显示*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mNearbyRecyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });

        mEmptyTextView = (TextView) findViewById(R.id.empty_nearby_view);

        updateUI();
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    updateUI();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    private void updateUI() {
        NearbyLab nearbyLab = NearbyLab.get(this);
        List<Nearby> nearbyList = nearbyLab.getNearbyList();

        showEmptyView(nearbyList.isEmpty());
        queryNearby();
        if (null == mNearbyAdapter){
            mNearbyAdapter = new NearbyAdapter(nearbyList);
            mNearbyRecyclerView.setAdapter(mNearbyAdapter);
        }else {
            mNearbyAdapter.setNearbyList(nearbyList);
            mNearbyAdapter.notifyItemChanged(mItemPosition);
        }

        mNearbyAdapter.setOnItemClickListener(new NearbyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Nearby nearby) {
                Intent intent = NearbyCommentsActivity.newIntent(NearbyActivity.this, nearby);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }
    private void queryNearby(){
        showEmptyView(false);
        mSwipeRefreshLayout.setRefreshing(true);
        BmobQuery<Nearby> query = new BmobQuery<>();
        query.order("-createAt");/*按照时间降序*/
        query.findObjects(new FindListener<Nearby>() {
            @Override
            public void done(List<Nearby> list, BmobException e) {
                if (null == e){
                    mNearbyAdapter.clear();
                    if (null == list || list.size() == 0){
                        showEmptyView(true);
                        mNearbyAdapter.notifyDataSetChanged();
                        return;
                    }
                    Log.d(TAG, "queryNearby: is called");
                    mNearbyAdapter.addAll(list, mNearbyNameTextView.getText().toString());
//                    mNearbyAdapter.addAll(list);
                    mNearbyRecyclerView.setAdapter(mNearbyAdapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                }else {
                    showEmptyView(true);
                }
            }
        });
    }

    /*地址选择器*/
    public void onAddressPicker() {
        new AddressInitTask(this).execute("山西", "太原", "尖草坪区");
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NearbyActivity.class);
        return intent;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nearby_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                Intent intent = NearbyAddActivity.newIntent(this);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showEmptyView(boolean isEmpty){
        if (isEmpty){
            mNearbyRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            mNearbyRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    class AddressInitTask extends AsyncTask<String, Void, ArrayList<AddressPicker.Province>> {
        private Activity activity;
        private ProgressDialog dialog;
        private String selectedProvince = "", selectedCity = "", selectedCounty = "";
        private boolean hideCounty = false;
        private StringBuilder address = new StringBuilder();
        /**
         * 初始化为不显示区县的模式
         *
         * @param activity
         * @param hideCounty is hide County
         */
        public AddressInitTask(Activity activity, boolean hideCounty) {
            this.activity = activity;
            this.hideCounty = hideCounty;
            dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
        }

        public AddressInitTask(Activity activity) {
            this.activity = activity;
            dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
        }

        @Override
        protected ArrayList<AddressPicker.Province> doInBackground(String... params) {
            if (params != null) {
                switch (params.length) {
                    case 1:
                        selectedProvince = params[0];
                        break;
                    case 2:
                        selectedProvince = params[0];
                        selectedCity = params[1];
                        break;
                    case 3:
                        selectedProvince = params[0];
                        selectedCity = params[1];
                        selectedCounty = params[2];
                        break;
                    default:
                        break;
                }
            }
            ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
            try {
                String json = AssetsUtils.readText(activity, "city.json");
                data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<AddressPicker.Province> result) {
            dialog.dismiss();
            if (result.size() > 0) {
                AddressPicker picker = new AddressPicker(activity, result);
                picker.setHideCounty(hideCounty);
                picker.setSelectedItem(selectedProvince, selectedCity, selectedCounty);
                picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(AddressPicker.Province province, AddressPicker.City city, AddressPicker.County county) {
                        mNearbyNameTextView.setText(getAddress(province, city, county));
                    }
                });
                picker.show();
            } else {
                Toast.makeText(activity, "数据初始化失败", Toast.LENGTH_SHORT).show();
            }
        }

        public String getAddress(AddressPicker.Province province, AddressPicker.City city, AddressPicker.County county){
            address.append(RegexValidateUtil.getChineseInString(province + ""));
            address.append(RegexValidateUtil.getChineseInString(city + ""));
            if (county != null){
                address.append(RegexValidateUtil.getChineseInString(county + ""));
            }
            Toast.makeText(activity, "位置已切换至：" + address.toString() + "，请下拉刷新", Toast.LENGTH_SHORT).show();
            return address.toString();
        }

    }
}
