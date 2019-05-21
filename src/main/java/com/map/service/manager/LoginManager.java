package com.map.service.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.map.service.bean.User;


/**
 * Created by lgx on 2019/4/24.
 *
 */

public class LoginManager {

    private static LoginManager gManager= null;
    private User mLoginUser;
    private SharedPreferences sp;
    private Context mContext;

    private LoginManager(Context context) {
        this.mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static LoginManager getInstance(Context context) {
        if (gManager==null){
            gManager = new LoginManager(context);
        }
        return gManager;
    }

    //登录
    public void doLogin(final User user,final LoginListenter listenter){

        if (isRemindPwd())
        {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name",user.getName());
            editor.putString("pwd",user.getPwd());
            editor.commit();
        }
        DBManager.getInstance(mContext).doLogin(user, new DBManager.DBManagerListener() {
            @Override
            public void onSuccess(User user) {
                mLoginUser = user;
                listenter.onSuccess();
            }

            @Override
            public void onFail(int error) {
                listenter.onError(error);
            }
        });

    }

    //登出
    public void logout(){
        mLoginUser = null;

    }

    //是否记住密码
    public void savePWd(boolean save, String name, String pwd){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("remind_pwd",save);
        if (save){
            editor.putString("name",name);
            editor.putString("pwd",pwd);
        }else {
            editor.remove("name");
            editor.remove("pwd");
        }

        editor.commit();
    }

    public boolean isRemindPwd(){
        boolean isRemind = sp.getBoolean("remind_pwd",false);
        return isRemind;
    }

    public String getPreName(){
        return sp.getString("name","");
    }

    public String getPrePwd(){
        return sp.getString("pwd","");
    }

    public User getUser(){
        return mLoginUser;
    }

    public void setUser(User user){
        this.mLoginUser = user;
    }

    public interface LoginListenter{
        public void onSuccess();
        public void onError(int error);
    }
}
