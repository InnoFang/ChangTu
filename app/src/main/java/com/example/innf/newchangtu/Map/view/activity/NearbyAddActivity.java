package com.example.innf.newchangtu.Map.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.bean.Nearby;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.manager.NearbyLab;
import com.example.innf.newchangtu.Map.utils.AddressInitTask;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class NearbyAddActivity extends BaseActivity {

    private static final String TAG = "NearbyAddActivity";

    private EditText mContentEditText;
    private TextView mAddressTextView;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_add);

        /*检查是否具有Bmob当前对象*/
        mUser = BmobUser.getCurrentUser(User.class);
        if(mUser == null){
            mUser = new User();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContentEditText = (EditText) findViewById(R.id.nearby_content_edit_text);
        mAddressTextView = (TextView) findViewById(R.id.address_text_view);
        String address = (String) BmobUser.getObjectByKey("address");
        if (null == address){
            address = "无法确定你的位置";
        }
        mAddressTextView.setText(address);
        mAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddressInitTask(NearbyAddActivity.this, mAddressTextView).execute("山西", "太原", "尖草坪区");
            }
        });
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, NearbyAddActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nearby_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_nearby:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.show();
                String content = mContentEditText.getText().toString();
                if (!content.equals("")){
                    final Nearby nearby = new Nearby();
                    NearbyLab.get(this).addNearby(nearby);
                    nearby.setContent(content);
                    String address = mAddressTextView.getText().toString();
                    nearby.setAddress(address);
                    Log.d(TAG, "onOptionsItemSelected: " + address);
                    String name = (String) BmobUser.getObjectByKey("name");
                    if (null == name){
                        name = "用户" + BmobUser.getCurrentUser(User.class).getObjectId();
                    }
                    nearby.setName(name);
                    nearby.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (null == e){
                                nearby.setObjectId(s);
                                nearby.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        progressDialog.cancel();
                                        if (null == e){
                                            showToast("发表成功!");
                                            finish();
                                        } else {
                                            showToast("发表失败!" + e.getMessage());
                                        }

                                    }
                                });
                            }else {
                                showToast(e.getMessage());
                            }
                        }
                    });
                }else {
                    progressDialog.cancel();
                    showToast("内容为空~");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
