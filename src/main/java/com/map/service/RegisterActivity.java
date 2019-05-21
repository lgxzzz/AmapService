package com.map.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.map.service.bean.User;
import com.map.service.constant.ErrorCode;
import com.map.service.manager.DBManager;
import com.map.service.manager.LoginManager;


public class RegisterActivity extends BaseActivity {
    private TextView mRegNumberTv;

    private Button mRegSure;

    private EditText mRegName;
    private EditText mRegNumber;
    private EditText mRegPwd;
    private EditText mRegConfirmPwd;

    private String mName;
    private String mNumber;
    private String mPwd;
    private String mConfirmPwd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initView();
    }

    public void initView(){
        mRegSure = (Button) findViewById(R.id.reg_sure);

        mRegName = (EditText) findViewById(R.id.reg_name);
        mRegPwd = (EditText) findViewById(R.id.reg_pwd);
        mRegConfirmPwd = (EditText) findViewById(R.id.reg_confirm);

        mRegName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mName = editable.toString();
            }
        });


        mRegPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPwd = editable.toString();
            }
        });

        mRegConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mConfirmPwd = editable.toString();
            }
        });

        mRegSure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (mName ==null || mName.length()==0){
                    Toast.makeText(RegisterActivity.this,"用户名不能未空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPwd ==null|| mPwd.length()==0){
                    Toast.makeText(RegisterActivity.this,"密码不能未空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mConfirmPwd ==null|| mConfirmPwd.length()==0){
                    Toast.makeText(RegisterActivity.this,"确认密码不能未空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!mPwd.equals(mConfirmPwd)){
                    Toast.makeText(RegisterActivity.this,"密码不一致，请重试！", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setPwd(mPwd);
                user.setName(mName);

                DBManager.getInstance(RegisterActivity.this).addUser(user, new DBManager.DBManagerListener() {
                    @Override
                    public void onSuccess(User user) {
                        LoginManager.getInstance(getBaseContext()).setUser(user);
                        Intent mIntent = new Intent();
                        mIntent.setClassName("com.map.service","com.map.service.MainActivity");
                        startActivity(mIntent);
                    }

                    @Override
                    public void onFail(int error) {
                       switch (error){
                           case ErrorCode.ERROR_ALREADY_REGISTERED:
                               Toast.makeText(RegisterActivity.this,"已经注册过该用户", Toast.LENGTH_LONG).show();
                               break;
                           case ErrorCode.ERROR_NOT_EXIST_PERSON:
                               Toast.makeText(RegisterActivity.this,"未找到该用户", Toast.LENGTH_LONG).show();
                               break;
                       }
                    }
                });
            }
        });

     }
}
