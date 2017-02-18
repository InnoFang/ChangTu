package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.model.UserLab;
import com.example.innf.newchangtu.Map.utils.AddressInitTask;
import com.example.innf.newchangtu.Map.utils.MyApplication;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * “个人资料” 界面
 */
public class PersonalSettingActivity extends BaseActivity implements View.OnClickListener{

    private static final int REFRESH_COMPLETE = 0x110;
    private User mUser;
    private BmobQuery<User> bmobQuery;/*用于查询Bmob用户数据*/
    private EditText mUsernameEditText;
    private EditText mAgeEditText;
    private TextView mAddressTextView;
    private TextView mPhoneNumberTextView;
    private TextView mEmailTextView;
    private RadioButton mMaleGenderRadioButton;
    private RadioButton mFemaleGenderRadioButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);

        mUser = BmobUser.getCurrentUser(User.class);

        bmobQuery = new BmobQuery<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUsernameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mAgeEditText = (EditText) findViewById(R.id.age_edit_text);
        mAddressTextView = (TextView) findViewById(R.id.address_text_view);
        mEmailTextView = (TextView) findViewById(R.id.email_text_view);
        mPhoneNumberTextView = (TextView) findViewById(R.id.phone_number_text_view);
        mFemaleGenderRadioButton = (RadioButton) findViewById(R.id.gender_female);
        mMaleGenderRadioButton = (RadioButton) findViewById(R.id.gender_male);

        mAddressTextView.setOnClickListener(this);
        mEmailTextView.setOnClickListener(this);
        mPhoneNumberTextView.setOnClickListener(this);
//        mSettingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });

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
        mSwipeRefreshLayout.setRefreshing(true);
         /*若是保存有数据，EditText就直接显示数据*/
        bmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    String name = (String) BmobUser.getObjectByKey("name");
                    Integer age = (Integer) BmobUser.getObjectByKey("age");
                    String address = (String) BmobUser.getObjectByKey("address");
                    String phoneNumber = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
                    String email = (String) BmobUser.getObjectByKey("email");
                    String gender = (String) BmobUser.getObjectByKey("gender");
                    if (name != null) {
                        mUsernameEditText.setText(name);
                    }
                    if (age != null) {
                        mAgeEditText.setText(String.valueOf(age));
                    }
                    if (address != null) {
                        mAddressTextView.setText(address);
                    }
                    if (phoneNumber != null){
                        mPhoneNumberTextView.setText(phoneNumber);
                    }
                    if (email != null){
                        mEmailTextView.setText(email);
                    }
                    if (gender != null){
                        if (gender.equals("男")){
                            mMaleGenderRadioButton.setChecked(true);
                            mFemaleGenderRadioButton.setChecked(false);
                        }else{
                            mFemaleGenderRadioButton.setChecked(true);
                            mMaleGenderRadioButton.setChecked(false);
                        }
                    }

                    }
                }
            });
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    String phoneNumber = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
                    String email = (String) BmobUser.getObjectByKey("email");

                    if (phoneNumber != null) {
                        mPhoneNumberTextView.setText(phoneNumber);
                    }
                    if (email != null) {
                        mEmailTextView.setText(email);
                    }
                }
            }
        });
    }


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PersonalSettingActivity.class);
        return intent;
    }


    /*工具栏菜单操作，创建视图*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.personal_setting, menu);
        return true;
    }

    /*工具栏菜单操作，点击工具栏的“√”，可以保存数据*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_setting:
                String name = mUsernameEditText.getText().toString();
                String age = mAgeEditText.getText().toString();
                String address = mAddressTextView.getText().toString();
                if (!name.equals("")) {
                    mUser.setName(name);
                }
                if (!age.equals("")) {
                    mUser.setAge(Integer.parseInt(age));
                }
                if (!address.equals("")) {
                    mUser.setAddress(address);
                }
                if (mMaleGenderRadioButton.isChecked()){
                    mUser.setGender("男");
                }else {
                    mUser.setGender("女");
                }
                UserLab.get(MyApplication.getContext()).updateBmobUserData(mUser);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.email_text_view:
            case R.id.phone_number_text_view:
                Intent intent = BindOnAccountActivity.newIntent(PersonalSettingActivity.this);
                startActivity(intent);
                break;
            case R.id.address_text_view:
                new AddressInitTask(this, mAddressTextView).execute("山西", "太原", "尖草坪区");
                break;
        }
    }
}
