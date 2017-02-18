package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.RegexValidateUtil;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SetEmergencyContractsActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SetEmergencyContractsActivity";

    private User mUser;
    private BmobQuery<User> mBmobQuery;/*用于查询Bmob用户数据*/


    private Button mFirstContractButton;
    private Button mSecondContractButton;
    private Button mThirdContractButton;
    private EditText mFirstContractPhoneEditText;
    private EditText mSecondContractPhoneEditText;
    private EditText mThirdContractPhoneEditText;
    private EditText mFirstContractNameEditText;
    private EditText mSecondContractNameEditText;
    private EditText mThirdContractNameEditText;
    private CardView mFirstContractCardView;
    private CardView mSecondContractCardView;
    private CardView mThirdContractCardView;
    private LinearLayout mFirstContractLayout;
    private LinearLayout mSecondContractLayout;
    private LinearLayout mThirdContractLayout;

    private boolean isFirstContractOpen = true;
    private boolean isSecondContractOpen = true;
    private boolean isThirdContractOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_emergency_contracts);

        mUser = BmobUser.getCurrentUser(User.class);
        mBmobQuery = new BmobQuery<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        updateUI();
    }

    private void init() {
        mFirstContractButton = (Button) findViewById(R.id.first_contract_button);
        mSecondContractButton = (Button) findViewById(R.id.second_contract_button);
        mThirdContractButton = (Button) findViewById(R.id.third_contract_button);
        mFirstContractPhoneEditText = (EditText) findViewById(R.id.first_contracts_phone_edit_text);
        mSecondContractPhoneEditText = (EditText) findViewById(R.id.second_contracts_phone_edit_text);
        mThirdContractPhoneEditText = (EditText) findViewById(R.id.third_contracts_phone_edit_text);
        mFirstContractNameEditText = (EditText) findViewById(R.id.first_contracts_name_edit_text);
        mSecondContractNameEditText = (EditText) findViewById(R.id.second_contracts_name_edit_text);
        mThirdContractNameEditText = (EditText) findViewById(R.id.third_contracts_name_edit_text);
        mFirstContractCardView = (CardView) findViewById(R.id.first_contracts_card_view);
        mSecondContractCardView = (CardView) findViewById(R.id.second_contracts_card_view);
        mThirdContractCardView = (CardView) findViewById(R.id.third_contracts_card_view);
        mFirstContractLayout = (LinearLayout) findViewById(R.id.first_contract_layout);
        mSecondContractLayout = (LinearLayout) findViewById(R.id.second_contract_layout);
        mThirdContractLayout = (LinearLayout) findViewById(R.id.third_contract_layout);


        mFirstContractCardView.setOnClickListener(this);
        mSecondContractCardView.setOnClickListener(this);
        mThirdContractCardView.setOnClickListener(this);
        mFirstContractButton.setOnClickListener(this);
        mSecondContractButton.setOnClickListener(this);
        mThirdContractButton.setOnClickListener(this);
    }

    private void updateUI() {
         /*若是保存有数据，EditText就直接显示数据*/
        mBmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    String firstName = (String) BmobUser.getObjectByKey("mFirstContractName");
                    String firstPhone = (String) BmobUser.getObjectByKey("mFirstContractPhone");
                    String secondName = (String) BmobUser.getObjectByKey("mSecondContractName");
                    String secondPhone = (String) BmobUser.getObjectByKey("mSecondContractPhone");
                    String thirdName = (String) BmobUser.getObjectByKey("mThirdContractName");
                    String thirdPhone = (String) BmobUser.getObjectByKey("mThirdContractPhone");
                    if(null != firstName){
                        mFirstContractNameEditText.setText(firstName);
                    }
                    if(null != secondName){
                        mSecondContractNameEditText.setText(secondName);
                    }
                    if (null != thirdName){
                        mThirdContractNameEditText.setText(thirdName);
                    }
                    if (null != firstPhone){
                        mFirstContractPhoneEditText.setText(firstPhone);
                    }
                    if (null != secondPhone){
                        mSecondContractPhoneEditText.setText(secondPhone);
                    }
                    if (null != thirdPhone){
                        mThirdContractPhoneEditText.setText(thirdPhone);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_contracts_card_view:
                if (isFirstContractOpen){
//                    mFirstContractLayout.setVisibility(View.VISIBLE);
                    mFirstContractButton.setVisibility(View.VISIBLE);
                    mFirstContractNameEditText.setVisibility(View.VISIBLE);
                    mFirstContractPhoneEditText.setVisibility(View.VISIBLE);
                } else {
//                    mFirstContractLayout.setVisibility(View.GONE);
                    mFirstContractButton.setVisibility(View.GONE);
                    mFirstContractNameEditText.setVisibility(View.GONE);
                    mFirstContractPhoneEditText.setVisibility(View.GONE);
                }
                Log.i("set", "first contract" + isFirstContractOpen);
                isFirstContractOpen = !isFirstContractOpen;
                break;
            case R.id.second_contracts_card_view:
                if (isSecondContractOpen){
//                    mSecondContractLayout.setVisibility(View.VISIBLE);
                    mSecondContractButton.setVisibility(View.VISIBLE);
                    mSecondContractNameEditText.setVisibility(View.VISIBLE);
                    mSecondContractPhoneEditText.setVisibility(View.VISIBLE);
                } else {
//                    mSecondContractLayout.setVisibility(View.GONE);
                    mSecondContractButton.setVisibility(View.GONE);
                    mSecondContractNameEditText.setVisibility(View.GONE);
                    mSecondContractPhoneEditText.setVisibility(View.GONE);
                }
                Log.i("set", "second contract" + isSecondContractOpen);

                isSecondContractOpen = !isSecondContractOpen;
                break;
            case R.id.third_contracts_card_view:
                if (isThirdContractOpen){
//                    mThirdContractLayout.setVisibility(View.VISIBLE);
                    mThirdContractButton.setVisibility(View.VISIBLE);
                    mThirdContractNameEditText.setVisibility(View.VISIBLE);
                    mThirdContractPhoneEditText.setVisibility(View.VISIBLE);
                } else {
//                    mThirdContractLayout.setVisibility(View.GONE);
                    mThirdContractButton.setVisibility(View.GONE);
                    mThirdContractNameEditText.setVisibility(View.GONE);
                    mThirdContractPhoneEditText.setVisibility(View.GONE);
                }
                Log.i("set", "third contract" + isThirdContractOpen);
                isThirdContractOpen = !isThirdContractOpen;
                break;
            case R.id.first_contract_button:
                String firstName = mFirstContractNameEditText.getText().toString();
                String firstPhone = mFirstContractPhoneEditText.getText().toString();
                if (!firstName.equals("") && !firstPhone.equals("") && RegexValidateUtil.checkMobileNumber(firstPhone)) {
                    mUser.setFirstContractName(firstName);
                    mUser.setFirstContractPhone(firstPhone);
                    mUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (null == e){
                                showToast("上传成功");
                            } else {
                                showToast(e.getMessage());
                            }
                        }
                    });
                }
                break;
            case R.id.second_contract_button:
                String secondName = mSecondContractNameEditText.getText().toString();
                String secondPhone = mSecondContractPhoneEditText.getText().toString();
                if (!secondName.equals("") && !secondPhone.equals("") && RegexValidateUtil.checkMobileNumber(secondPhone)) {
                    mUser.setSecondContractName(secondName);
                    mUser.setSecondContractPhone(secondPhone);
                    mUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (null == e){
                                showToast("上传成功");
                            } else {
                                showToast(e.getMessage());
                            }
                        }
                    });
                }
                break;
            case R.id.third_contract_button:
                String thirdName = mThirdContractNameEditText.getText().toString();
                String thirdPhone = mThirdContractPhoneEditText.getText().toString();
                if (!thirdName.equals("") && !thirdPhone.equals("") && RegexValidateUtil.checkMobileNumber(thirdPhone)) {
                    mUser.setThirdContractName(thirdName);
                    mUser.setThirdContractPhone(thirdPhone);
                    mUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (null == e){
                                showToast("上传成功");
                            } else {
                                showToast(e.getMessage());
                            }
                        }
                    });
                }
                break;
        }
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, SetEmergencyContractsActivity.class);
        return intent;
    }
}
