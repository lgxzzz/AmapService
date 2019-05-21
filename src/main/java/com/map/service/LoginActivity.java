package com.map.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.map.service.bean.User;
import com.map.service.manager.LoginManager;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText mEditTextName;//输入的名称
    EditText mEditTextPwd;//输入的密码

    CheckBox mCheckBoxRemindPwd;//是否记住密码

    TextView mTextForgetPwd;//忘记密码

    Button mButtonLogin; //登陆
    Button mButtonRegister;//注册
    Button mButtonTestDB;//测试数据

    LoginManager mLoginManager;

    String mUserName = null;
    String mPwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mLoginManager = LoginManager.getInstance(this);

        initView();

    }

    //初始化控件，绑定点击事件
    public void initView(){
        mEditTextName = (EditText)findViewById(R.id.name);
        mEditTextPwd  = (EditText)findViewById(R.id.pwd);
        mCheckBoxRemindPwd = (CheckBox) findViewById(R.id.remind_pwd);
        mTextForgetPwd = (TextView) findViewById(R.id.forget_pwd);
        mButtonLogin = (Button) findViewById(R.id.login);
        mButtonRegister = (Button) findViewById(R.id.register);
        mButtonTestDB = (Button) findViewById(R.id.test_db);

        mTextForgetPwd.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mButtonRegister.setOnClickListener(this);
        mButtonTestDB.setOnClickListener(this);

        boolean isRemind = mLoginManager.isRemindPwd();
        if (isRemind){
            mUserName = mLoginManager.getPreName();
            mPwd = mLoginManager.getPrePwd();
            mCheckBoxRemindPwd.setChecked(true);
            mEditTextName.setText(mUserName);
            mEditTextPwd.setText(mPwd);
        }

        mEditTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                 mUserName = editable.toString();
            }
        });

        mEditTextPwd.addTextChangedListener(new TextWatcher() {
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

        mCheckBoxRemindPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mLoginManager.savePWd(b,mUserName,mPwd);
            }
        });
    }



    public void doLogin(){
        if (mUserName ==null || mUserName.length()==0){
            Toast.makeText(LoginActivity.this,"用户名不能未空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPwd ==null|| mPwd.length()==0){
            Toast.makeText(LoginActivity.this,"密码不能未空", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setName(mUserName);
        user.setPwd(mPwd);
        mLoginManager.doLogin(user, new LoginManager.LoginListenter() {
            @Override
            public void onSuccess() {
                Intent mIntent = new Intent();
                mIntent.setClassName("com.map.service","com.map.service.MainActivity");
                startActivity(mIntent);
            }

            @Override
            public void onError(int error) {
                Toast.makeText(LoginActivity.this,"登陆失败，请检查账号密码", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Register(){
        Intent mIntent = new Intent();
        mIntent.setClassName("com.map.service","com.map.service.RegisterActivity");
        startActivity(mIntent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.forget_pwd:
                break;
            case R.id.login:
                doLogin();
                break;
            case R.id.register:
                Register();
                break;
        }
    }



}
