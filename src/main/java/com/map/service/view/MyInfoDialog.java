package com.map.service.view;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


import com.map.service.R;
import com.map.service.bean.User;
import com.map.service.manager.LoginManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MyInfoDialog extends Dialog {

    private boolean iscancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;

    private TextView mName;
    private TextView mAge;
    private TextView mCollege;
    private TextView mNumber;

    private String mContent;
    private String mTchName;
    private String mCourseName;

    public MyInfoDialog(Context context, int layoutid, boolean isCancelable, boolean isBackCancelable) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.view = LayoutInflater.from(context).inflate(layoutid, null);
        this.iscancelable = isCancelable;
        this.isBackCancelable = isBackCancelable;

        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);//这行一定要写在前面
        setCancelable(iscancelable);//点击外部不可dismiss
        setCanceledOnTouchOutside(isBackCancelable);


    }

    public void initView() {
        mName = (TextView) this.view.findViewById(R.id.my_name);
        mNumber = (TextView) this.view.findViewById(R.id.my_number);

        User user = LoginManager.getInstance(getContext()).getUser();

        mName.setText(user.getName());
//        mNumber.setText(user.get());


    }

}