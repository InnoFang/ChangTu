package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.innf.newchangtu.R;

public class OneClickFastSendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click_fast_send);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /*跳转至设置紧急联系人界面按钮点击事件*/
    public void setEmergencyContracts(View view) {
        Intent intent = SetEmergencyContractsActivity.newIntent(this);
        startActivity(intent);
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, OneClickFastSendActivity.class);
        return intent;
    }
}
