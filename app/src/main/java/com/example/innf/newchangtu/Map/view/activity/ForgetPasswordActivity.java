package com.example.innf.newchangtu.Map.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.utils.RegexValidateUtil;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{

    private CardView mUseEmailCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mUseEmailCardView = (CardView) findViewById(R.id.use_email_find_password_card_view);

        mUseEmailCardView.setOnClickListener(this);
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.use_email_find_password_card_view:
                LayoutInflater layoutInflater = LayoutInflater.from(ForgetPasswordActivity.this);
                View v = layoutInflater.inflate(R.layout.input_text, null);

                final EditText editText = (EditText) v.findViewById(R.id.input_text);
                editText.setHint(R.string.setting_user_email);

                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle(R.string.use_email_find_password).
                        setIcon(R.drawable.user_email).
                        setView(v).
                        setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String email = editText.getText().toString();
                                if (email.equals("")){
                                    Toast.makeText(ForgetPasswordActivity.this, "输入为空！" , Toast.LENGTH_SHORT).show();
                                }else if (!RegexValidateUtil.checkEmail(email)){
                                    Toast.makeText(ForgetPasswordActivity.this, "邮箱格式错误！" , Toast.LENGTH_SHORT).show();
                                }else {
                                    BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(ForgetPasswordActivity.this, "重置密码请求成功，请到" + email + "邮箱进行密码重置操作" , Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(ForgetPasswordActivity.this, "失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).
                        create();
                alertDialog.show();
                break;
        }
    }


}
