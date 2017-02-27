package com.example.innf.newchangtu.Map.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.manager.UserLab;
import com.example.innf.newchangtu.Map.utils.MyApplication;
import com.example.innf.newchangtu.Map.utils.RegexValidateUtil;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: Inno Fang
 * Time: 2016/7/20 21:15
 * Description:  账号绑定
 */


public class BindOnAccountActivity extends BaseActivity implements View.OnClickListener{


    private EditText mBindEmailEditText;
    private EditText mBindPhoneEditText;
    private CardView mBindEmailCardView;
    private CardView mBindPhoneCardView;
    private Button mBindPhoneButton;
    private Button mBindEmailButton;

    private boolean isPhoneBindOpen = true;
    private boolean isEmailBindOpen = true;

    private User mUser;
    private BmobQuery<User> bmobQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_on_account);

        mUser = BmobUser.getCurrentUser(User.class);
        bmobQuery = new BmobQuery<>();

        mBindEmailEditText = (EditText) findViewById(R.id.bind_email_edit_text);
        mBindPhoneEditText = (EditText) findViewById(R.id.bind_phone_edit_text);
        mBindEmailCardView = (CardView) findViewById(R.id.bind_email_card_view);
        mBindPhoneCardView = (CardView) findViewById(R.id.bind_phone_card_view);
        mBindEmailButton = (Button) findViewById(R.id.bind_email_button);
        mBindPhoneButton = (Button) findViewById(R.id.bind_phone_button);

        mBindEmailCardView.setOnClickListener(this);
        mBindPhoneCardView.setOnClickListener(this);
        mBindEmailButton.setOnClickListener(this);
        mBindPhoneButton.setOnClickListener(this);

        bmobQuery.getObject(mUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    String phoneNumber = (String) BmobUser.getObjectByKey("mobilePhoneNumber");
                    String email = (String) BmobUser.getObjectByKey("email");

                    if (phoneNumber != null) {
                        mBindPhoneEditText.setText(phoneNumber);
                    }
                    if (email != null) {
                        mBindEmailEditText.setText(email);
                    }
                }
            }
        });
    }
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, BindOnAccountActivity.class);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bind_phone_card_view:
                if (isPhoneBindOpen){
                    mBindPhoneEditText.setVisibility(View.VISIBLE);
                    mBindPhoneButton.setVisibility(View.VISIBLE);
                }else {
                    mBindPhoneEditText.setVisibility(View.GONE);
                    mBindPhoneButton.setVisibility(View.GONE);
                }
                isPhoneBindOpen = !isPhoneBindOpen;
                break;
            case R.id.bind_phone_button:
                String phoneNumber = mBindPhoneEditText.getText().toString();
                if (!phoneNumber.equals("")){/*验证是否是手机号码*/
                    if (RegexValidateUtil.checkMobileNumber(phoneNumber)){
                        mUser.setMobilePhoneNumber(phoneNumber);
                        mUser.setMobilePhoneNumberVerified(true);
                        UserLab.get(MyApplication.getContext()).updateBmobUserData(mUser);
                    }else {
                        Toast.makeText(BindOnAccountActivity.this,"请检查手机号码格式！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bind_email_card_view:
                if (isEmailBindOpen){
                    mBindEmailEditText.setVisibility(View.VISIBLE);
                    mBindEmailButton.setVisibility(View.VISIBLE);
                }else {
                    mBindEmailEditText.setVisibility(View.GONE);
                    mBindEmailButton.setVisibility(View.GONE);
                }
                isEmailBindOpen = !isEmailBindOpen;
                break;
            case R.id.bind_email_button:
                final String email = mBindEmailEditText.getText().toString();
                if (!email.equals("")){
                    if (!RegexValidateUtil.checkEmail(email)){/*验证是否是邮箱*/
                        mUser.setEmail(email);
                        UserLab.get(MyApplication.getContext()).updateBmobUserData(mUser);
                    }else {
                        Toast.makeText(BindOnAccountActivity.this,"请检查邮箱格式！",Toast.LENGTH_SHORT).show();
                    }
                }
                Dialog alertDialog = new AlertDialog.Builder(BindOnAccountActivity.this)
                        .setTitle(R.string.bind_on_account_verify_email)
                        .setMessage("是否验证该邮箱？")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BmobUser.requestEmailVerify(email, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(BindOnAccountActivity.this, "请求验证邮件成功，请到" + email + "邮箱中进行激活。", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(BindOnAccountActivity.this, "失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                alertDialog.show();
                break;
        }
    }
}
