package com.example.innf.newchangtu.Map.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.AppCompatActivityCollector;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 隐私设置 界面，目前只有更改密码功能
 */
public class PrivacySettingActivity extends BaseActivity implements View.OnClickListener{

    private User mUser;

    private EditText mOldPasswordEditText;
    private EditText mNewPasswordEditText;
    private EditText mRepeatPasswordEditText;
    private CardView mModifyPasswordCardView;
    private Button mModifyPasswordButton;

    private boolean isOpen = true;/*“更改密码”功能开关*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);

        mUser = BmobUser.getCurrentUser(User.class);

        mOldPasswordEditText = (EditText) findViewById(R.id.old_password);
        mNewPasswordEditText = (EditText) findViewById(R.id.new_password);
        mRepeatPasswordEditText = (EditText) findViewById(R.id.repeat_password_edit_text);
        mModifyPasswordCardView = (CardView) findViewById(R.id.modify_password);
        mModifyPasswordButton = (Button) findViewById(R.id.modify_password_ok);


        mModifyPasswordCardView.setOnClickListener(this);
        mModifyPasswordButton.setOnClickListener(this);

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PrivacySettingActivity.class);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.modify_password:
                /*显示 更改密码 相关功能*/
                if (isOpen){
                    mOldPasswordEditText.setVisibility(View.VISIBLE);
                    mNewPasswordEditText.setVisibility(View.VISIBLE);
                    mRepeatPasswordEditText.setVisibility(View.VISIBLE);
                    mModifyPasswordButton.setVisibility(View.VISIBLE);
                }else{
                    mOldPasswordEditText.setVisibility(View.GONE);
                    mNewPasswordEditText.setVisibility(View.GONE);
                    mRepeatPasswordEditText.setVisibility(View.GONE);
                    mModifyPasswordButton.setVisibility(View.GONE);
                }
                isOpen = !isOpen;
                break;
            case R.id.modify_password_ok:
                /*更改密码相关操作*/
                if (isOpen){
                    mModifyPasswordButton.setVisibility(View.VISIBLE);
                }
                String oldPassword = mOldPasswordEditText.getText().toString().trim();
                String newPassword = mNewPasswordEditText.getText().toString().trim();
                String repeatPassword = mRepeatPasswordEditText.getText().toString().trim();
                if (newPassword.equals("") || oldPassword.equals("") || repeatPassword.equals("")){
                    Snackbar.make(mModifyPasswordButton, "不能为空", Snackbar.LENGTH_LONG).show();
                }else if (!newPassword.equals(repeatPassword)){
                    Snackbar.make(mModifyPasswordButton, "两次输入的密码不一致", Snackbar.LENGTH_LONG).show();
                }else {
                    BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Snackbar.make(mModifyPasswordButton, "密码修改成功，可以用新密码进行登录啦", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(mModifyPasswordButton, "失败:" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                Toast.makeText(PrivacySettingActivity.this, "密码修改成功，请重新登陆！", Toast.LENGTH_SHORT).show();
                User.logOut();/*清除用户缓存对象*/
                AppCompatActivityCollector.finishAll();
                Intent intent = LogInActivity.newIntent(this);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}
