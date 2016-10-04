package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.CircularAnimUtils;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Author: Inno Fang
 * Time: 2016/8/6 21:20
 * Description: 用户注册界面
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener{

    private User mUser;
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private EditText mRepeatPasswordEditText;
    private CardView mDefaultCreatAccountCardView;
    private Button mSignUpButton;
    private ProgressBar mSignUpProgressBar;

    private boolean isDefaultOpen = true;/*默认注册开关*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        /*检查是否具有Bmob当前对象*/
        mUser = BmobUser.getCurrentUser(User.class);
        if (null == mUser){
            mUser = new User();
        }
        mAccountEditText = (EditText) findViewById(R.id.account_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpProgressBar = (ProgressBar) findViewById(R.id.sign_up_progress_bar);
        mRepeatPasswordEditText = (EditText) findViewById(R.id.repeat_password_edit_text);
        mDefaultCreatAccountCardView = (CardView) findViewById(R.id.default_create_account);

        mDefaultCreatAccountCardView.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, SignUpActivity.class);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.default_create_account:
                /*显示默认注册的相关操作*/
                if (isDefaultOpen){
                    mAccountEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                    mRepeatPasswordEditText.setVisibility(View.VISIBLE);
                    mSignUpButton.setVisibility(View.VISIBLE);
                }else {
                    mAccountEditText.setVisibility(View.GONE);
                    mPasswordEditText.setVisibility(View.GONE);
                    mRepeatPasswordEditText.setVisibility(View.GONE);
                    mSignUpButton.setVisibility(View.GONE);
                }
                isDefaultOpen = !isDefaultOpen;
                break;
            case R.id.sign_up_button:
                /*默认注册操作*/
                String username = mAccountEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String repeatPassword = mRepeatPasswordEditText.getText().toString().trim();
                if (username.equals("") || password.equals("")){
                    Snackbar.make(mSignUpButton, "不能为空", Snackbar.LENGTH_LONG).show();
                }else if (!password.equals(repeatPassword)){
                    Snackbar.make(mSignUpButton, "两次输入的密码不一致", Snackbar.LENGTH_LONG).show();
                }else {
                    mUser.setUsername(username);
                    mUser.setPassword(password);
                    mUser.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (null == e){
                                Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                mSignUpProgressBar.setVisibility(View.VISIBLE);
                                CircularAnimUtils.hide(mSignUpButton);
                                finish();
                            }else{
                                Snackbar.make(mSignUpButton, e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
        }
    }
}
