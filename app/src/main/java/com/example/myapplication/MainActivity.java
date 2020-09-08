package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.activities.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_login_head)
    Button mBtLoginHead;
    @BindView(R.id.bt_login_out_submit)
    Button mBtLoginOutSubmit;
    @BindView(R.id.tv_login_username)
    TextView mTvLoginUsername;
    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    public static final String HOLD_USERPWD_KEY="holdUserpwdKey";
    private String HOLD_ACCOUNT="hold_account";
    public static final String HOLD_ACCOUNT_STATE="holdAccountState";

    public boolean isLogin=false;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    private void initData(){
        SharedPreferences sharedPreferences=getSharedPreferences(HOLD_ACCOUNT, Context.MODE_PRIVATE);
        isLogin=sharedPreferences.getBoolean(HOLD_ACCOUNT_STATE,false);
        //已登录
        if(isLogin){
            username=sharedPreferences.getString(HOLD_USERNAME_KEY,null);
            mTvLoginUsername.setText(username);
            //用户名首字母
            String first=username.substring(0,1);
            mBtLoginHead.setText(first);
        }


    }

    @OnClick({R.id.bt_login_out_submit,R.id.bt_login_head})
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.bt_login_head:
                if(!isLogin){
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.bt_login_out_submit:
                if(isLogin){
                    mBtLoginHead.setText(null);
                    mTvLoginUsername.setText(getResources().getString(R.string.login_head));
                    Toast toast=Toast.makeText(this,"退出成功",Toast.LENGTH_SHORT);
                    toast.show();
                    SharedPreferences sharedPreferences=getSharedPreferences(HOLD_ACCOUNT, Context.MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putBoolean(HOLD_ACCOUNT_STATE,false)
                            .apply();
                }
                break;
            default:
                break;
        }
    }
}
