package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.AppCompatActivityCollector;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

/**
 * Author: Inno Fang
 * Time: 2016/8/6 21:20
 * Description: 个人设置界面
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private CardView mPersonalSettingCardView;
    private CardView mInviteFriendsCardView;
    private CardView mBindOnAccountCardView;
    private CardView mPrivacySettingCardView;
    private CardView mExitsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPersonalSettingCardView = (CardView) findViewById(R.id.personal_setting);
        mInviteFriendsCardView = (CardView) findViewById(R.id.invite_friends);
        mBindOnAccountCardView = (CardView) findViewById(R.id.bind_on_account);
        mPrivacySettingCardView = (CardView) findViewById(R.id.privacy_setting);
        mExitsCardView = (CardView) findViewById(R.id.exits);

        mPersonalSettingCardView.setOnClickListener(this);
        mInviteFriendsCardView.setOnClickListener(this);
        mBindOnAccountCardView.setOnClickListener(this);
        mPrivacySettingCardView.setOnClickListener(this);
        mExitsCardView.setOnClickListener(this);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }


    /*设置界面相应的item点击事件*/
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.personal_setting:
                intent = PersonalSettingActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.bind_on_account:
                intent = BindOnAccountActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.invite_friends:

                break;
            case R.id.privacy_setting:
                intent = PrivacySettingActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.exits:
                /*一键退出登录，并返回登陆界面*/
                User.logOut();/*清除用户缓存对象*/
                AppCompatActivityCollector.finishAll();
                intent = LogInActivity.newIntent(this);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
