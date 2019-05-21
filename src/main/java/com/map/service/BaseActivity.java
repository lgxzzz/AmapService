package com.map.service;

import android.app.Activity;
import android.os.Bundle;


public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapApplication.getInstance().addActivity(this);
    }

}
