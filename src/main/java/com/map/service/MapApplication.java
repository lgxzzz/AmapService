package com.map.service;

import android.app.Activity;
import android.app.Application;

import com.map.service.manager.DBManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lgx on 2019/4/24.
 */

public class MapApplication extends Application {

    DBManager mDBManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mDBManager = new DBManager(this);
    }
    /**打开的activity**/
    private List<Activity> activities = new ArrayList<Activity>();
    /**应用实例**/
    private static MapApplication instance;
    /**
     *  获得实例
     * @return
     */
    public static MapApplication getInstance(){
        return instance;
    }
    /**
     * 新建了一个activity
     * @param activity
     */
    public void addActivity(Activity activity){
        activities.add(activity);
    }
    /**
     *  结束指定的Activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if (activity!=null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**
     * 应用退出，结束所有的activity
     */
    public void exit(){
        for (Activity activity : activities) {
            if (activity!=null) {
                activity.finish();
            }
        }
        System.exit(0);
    }
    /**
     * 关闭Activity列表中的所有Activity*/
    public void finishActivity(){
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
