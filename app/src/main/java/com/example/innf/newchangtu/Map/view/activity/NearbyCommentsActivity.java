package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.adapter.NearbyCommentsAdapter;
import com.example.innf.newchangtu.Map.bean.Nearby;
import com.example.innf.newchangtu.Map.bean.NearbyComments;
import com.example.innf.newchangtu.Map.model.NearbyCommentsLab;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.util.List;

public class NearbyCommentsActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXTRA_NEARBY = "nearby";

    private Nearby mNearby;
    private NearbyComments mNearbyComments;
    private NearbyCommentsAdapter mNearbyCommentsAdapter;

    private TextView mNearbyNameTextView;
    private TextView mNearbyContentTextView;
    private TextView mNearbyDateTextView;
    private RecyclerView mNearbyCommentsRecyclerView;
    private EditText mNearbyCommentsAddEditText;
    private ImageView mNearbyCommentsAddImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_comments);

        mNearby = (Nearby) getIntent().getSerializableExtra(EXTRA_NEARBY);
//        mNearbyComments = mNearby.getNearbyComments();
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mNearbyNameTextView = (TextView) findViewById(R.id.nearby_name_text_view);
        mNearbyContentTextView = (TextView) findViewById(R.id.nearby_content_text_view);
        mNearbyDateTextView = (TextView) findViewById(R.id.nearby_date_text_view);
        mNearbyCommentsAddEditText = (EditText) findViewById(R.id.nearby_comments_add_edit_text);
        mNearbyCommentsAddImageView = (ImageView) findViewById(R.id.nearby_comments_add_image_view);
        mNearbyCommentsRecyclerView = (RecyclerView) findViewById(R.id.nearby_comments_recycler_view);

        mNearbyCommentsAddImageView.setOnClickListener(this);
        mNearbyCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    private void updateUI() {
        mNearbyNameTextView.setText(mNearby.getName());
        mNearbyDateTextView.setText(mNearby.getDate());
        mNearbyContentTextView.setText(mNearby.getContent());

        NearbyCommentsLab nearbyCommentsLab = NearbyCommentsLab.get(this);
        List<NearbyComments> nearbyCommentsList = nearbyCommentsLab.getNearbyCommentsList();


        mNearbyCommentsAdapter = new NearbyCommentsAdapter(nearbyCommentsList);
        mNearbyCommentsRecyclerView.setAdapter(mNearbyCommentsAdapter);

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
                    showToast(comments);
                }
                break;
        }
    }
}
