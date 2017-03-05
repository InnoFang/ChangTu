package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.adapter.NearbyCommentsAdapter;
import com.example.innf.newchangtu.Map.bean.Nearby;
import com.example.innf.newchangtu.Map.bean.NearbyComments;
import com.example.innf.newchangtu.Map.manager.NearbyCommentsLab;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NearbyCommentsActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXTRA_NEARBY = "nearby";
    private static final String TAG = "NearbyCommentsActivity";

    private Nearby mNearby;
    private TextView mNearbyNameTextView;
    private TextView mNearbyContentTextView;
    private TextView mNearbyDateTextView;
    private RecyclerView mNearbyCommentsRecyclerView;
    private EditText mNearbyCommentsAddEditText;
    private ImageView mNearbyCommentsAddImageView;
    private List<NearbyComments> mNearbyCommentsList;
    private NearbyCommentsAdapter mNearbyCommentsAdapter;
    private LinearLayout mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_comments);

        mNearby = (Nearby) getIntent().getSerializableExtra(EXTRA_NEARBY);
//        mNearbyComments = mNearby.getNearbyComments();
        mNearbyCommentsList = mNearby.getNearbyCommentsList();
        NearbyCommentsLab.get(this).setNearbyCommentsList(mNearbyCommentsList);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mNearbyNameTextView = (TextView) findViewById(R.id.nearby_name_text_view);
        mNearbyContentTextView = (TextView) findViewById(R.id.nearby_content_text_view);
        mNearbyDateTextView = (TextView) findViewById(R.id.nearby_date_text_view);
        mNearbyCommentsAddEditText = (EditText) findViewById(R.id.nearby_comments_add_edit_text);
        mNearbyCommentsAddImageView = (ImageView) findViewById(R.id.nearby_comments_add_image_view);
        mNearbyCommentsRecyclerView = (RecyclerView) findViewById(R.id.nearby_comments_recycler_view);

        mNearbyCommentsAddImageView.setOnClickListener(this);
        mNearbyCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
        showEmptyView(mNearbyCommentsList.isEmpty());
    }

    private void updateUI() {
        mNearbyNameTextView.setText(mNearby.getName());
        mNearbyDateTextView.setText(mNearby.getDate());
        mNearbyContentTextView.setText(mNearby.getContent());

        NearbyCommentsLab nearbyCommentsLab = NearbyCommentsLab.get(this);
        List<NearbyComments> nearbyCommentsList = nearbyCommentsLab.getNearbyCommentsList();

        if (null == mNearbyCommentsAdapter){
            mNearbyCommentsAdapter = new NearbyCommentsAdapter(nearbyCommentsList);
            mNearbyCommentsRecyclerView.setAdapter(mNearbyCommentsAdapter);
        } else {
            mNearbyCommentsAdapter.setNearbyCommentsList(mNearbyCommentsList);
            mNearbyCommentsAdapter.notifyDataSetChanged();
        }

    }

    public static Intent newIntent(Context context, Nearby nearby) {
        Intent intent = new Intent(context, NearbyCommentsActivity.class);
        intent.putExtra(EXTRA_NEARBY, nearby);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nearby_comments_add_image_view:
                String comments = mNearbyCommentsAddEditText.getText().toString();
                if (comments.equals("")) {
                    showToast("评论为空");
                } else {
                    NearbyComments nearbyComments = new NearbyComments();
                    nearbyComments.setContent(comments);
                    mNearbyCommentsList.add(nearbyComments);
                    NearbyCommentsLab.get(this).setNearbyCommentsList(mNearbyCommentsList);
                    updateNearby(mNearby);
                    updateUI();
                    showEmptyView(false);
                    mNearbyCommentsAddEditText.setText("");
                }
                break;
        }
    }

    private void updateNearby(Nearby nearby) {
        nearby.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("发表成功");
                    Log.i(TAG, "发表成功");
                } else {
                    showToast(e.getMessage());
                    Log.i(TAG, e.getMessage());
                }
            }
        });
    }


    private void showEmptyView(boolean b) {
        if (b){
            mEmptyView.setVisibility(View.VISIBLE);
            mNearbyCommentsRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mNearbyCommentsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
