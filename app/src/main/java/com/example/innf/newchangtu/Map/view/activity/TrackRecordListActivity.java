package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.adapter.TrackAdapter;
import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.manager.TrackLab;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 保存之前足迹的信息，便于用户查看和其他用途
 */

public class TrackRecordListActivity extends BaseActivity {

    private static final String TAG = "TrackRecordListActivity";

    private static final int REFRESH_COMPLETE = 0x110;

    private RecyclerView mTrackRecordRecyclerView;
    private TrackAdapter mTrackAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_record_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTrackRecordRecyclerView = (RecyclerView) findViewById(R.id.track_record_recycler_view);
        /*创建LinearLayoutManager对象，让RecyclerView的元素倒序显示，并且初始元素不默认从底部开始显示*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mTrackRecordRecyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });

        mEmptyTextView = (TextView) findViewById(R.id.empty_track_text_view);

        updateUI();
    }

    private void updateUI() {
        TrackLab trackLab = TrackLab.get(this);
        List<Track> trackList = trackLab.getTrackList();

        showEmptyView(trackList.isEmpty());

        queryTrack();
        if (null == mTrackAdapter){
            mTrackAdapter = new TrackAdapter(trackList);
            mTrackRecordRecyclerView.setAdapter(mTrackAdapter);
        }else{
            mTrackAdapter.setTrackList(trackList);
        }

        mTrackAdapter.setOnItemClickListener(new TrackAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Track track) {
                Intent intent = TrackRecordActivity.newIntent(TrackRecordListActivity.this, track);
                startActivity(intent);
            }
        });
    }

    private void queryTrack() {
        showEmptyView(false);
        mSwipeRefreshLayout.setRefreshing(true);
        BmobQuery<Track> query = new BmobQuery<>();
        query.order("-createAt");/*按照时间降序*/

        query.findObjects(new FindListener<Track>() {
            @Override
            public void done(List<Track> list, BmobException e) {
                if (null == e) {
                    mTrackAdapter.clear();
                    if (null == list || list.size() == 0) {
                        showEmptyView(true);
                        mTrackAdapter.notifyDataSetChanged();
                        return;
                    }
                    Log.d(TAG, "queryTrack: is called");
//                    mTrackAdapter.addAll(list);
                    mTrackAdapter.addAll(list, mUser.getUsername());
                    mTrackRecordRecyclerView.setAdapter(mTrackAdapter);
                } else {
                    showEmptyView(true);
                }

            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
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


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TrackRecordListActivity.class);
        return intent;
    }

    public void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            mTrackRecordRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mTrackRecordRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
