package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.CircularAnimUtils;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Author: Inno Fang
 * Time: 2016/7/20 20:57
 * Description:  登录界面
 */

public class LogInActivity extends BaseActivity implements View.OnClickListener{

    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private Button mLogInButton;
    private ProgressBar mLogInProgressBar;
    private TextView mSignUpTextView;
    private TextView mForgetPasswordTextView;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    private User mUser;

    @SuppressWarnings("deprecation")/*消除过时方法警告*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        /*检查是否具有Bmob当前对象*/
        mUser = BmobUser.getCurrentUser(User.class);
        if(mUser != null){
            Intent intent = MainActivity.newIntent(this);
            startActivity(intent);
            finish();
        }else{
            mUser = new User();
        }

        mLogInProgressBar = (ProgressBar) findViewById(R.id.login_in_progress_bar);
        mAccountEditText = (EditText) findViewById(R.id.account_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mLogInButton = (Button) findViewById(R.id.log_in_button);
        mSignUpTextView = (TextView) findViewById(R.id.sign_up_text_view);
        mForgetPasswordTextView = (TextView) findViewById(R.id.forget_password_text_view);


         /*设置html格式，高亮文字*/
        mSignUpTextView.setText(Html.fromHtml(getString(R.string.sign_up_text_view)));
        /*设置html格式，高亮文字*/
        mForgetPasswordTextView.setText(Html.fromHtml(getString(R.string.forget_password_text_view)));

        mLogInButton.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);
        mForgetPasswordTextView.setOnClickListener(this);
    }

    /*用于活动跳转*/
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LogInActivity.class);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.log_in_button:
                /*登录操作*/
                String account = mAccountEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                if (account.equals("") || password.equals("")){
                    Snackbar.make(mLogInButton, "账号或密码不能为空" , Snackbar.LENGTH_LONG).show();
                }else{
                    /*动画隐藏按钮，并显示隐藏在按钮下的progressBar*/
                    mLogInProgressBar.setVisibility(View.VISIBLE);
                    CircularAnimUtils.hide(mLogInButton);

                    BmobUser.loginByAccount(account, password, new LogInListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (null != user){
                                Toast.makeText(LogInActivity.this, "登录成功", Toast.LENGTH_LONG).show();
//                                CircularAnimUtils.startActivity(LogInActivity.this, MainActivity.class, view, R.color.material_login_login_color);
                                Intent intent = MainActivity.newIntent(LogInActivity.this);
                                startActivity(intent);
                                finish();
                            }else{
                                Snackbar.make(mLogInButton, e.getMessage(), Snackbar.LENGTH_LONG).show();

                                /*隐藏显示的progressBar，并动画显示按钮*/
                                mLogInProgressBar.setVisibility(View.GONE);
                                CircularAnimUtils.show(mLogInButton);
                                if (mLogInButton.getVisibility() == View.INVISIBLE || mLogInButton.getVisibility() == View.GONE){
                                    mLogInButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });


                }
                break;
            case R.id.sign_up_text_view:
                /*带动画跳转*/
                CircularAnimUtils.startActivity(LogInActivity.this, SignUpActivity.class, mSignUpTextView, R.color.material_login_login_color);
                break;
            case R.id.forget_password_text_view:
                /*带动画跳转*/
                CircularAnimUtils.startActivity(LogInActivity.this, ForgetPasswordActivity.class, mForgetPasswordTextView, R.color.material_login_login_color);
                break;
        }
    }
}
