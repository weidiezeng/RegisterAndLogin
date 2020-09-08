package com.example.myapplication.activities;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.Api;
import com.example.myapplication.R;
import com.example.myapplication.bean.LoginResult;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AccountBaseActivity implements View.OnClickListener,View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener {



    @BindView(R.id.ly_retrieve_bar)
    LinearLayout mLayBackBar;

    @BindView(R.id.iv_login_logo)
    ImageView mIvLoginLogo;

    @BindView(R.id.ll_login_username)
    LinearLayout mLlLoginUsername;
    @BindView(R.id.et_login_username)
    EditText mEtLoginUsername;
    @BindView(R.id.iv_login_username_del)
    ImageView mIvLoginUsernameDel;

    @BindView(R.id.ll_login_pwd)
    LinearLayout mLlLoginPwd;
    @BindView(R.id.et_login_pwd)
    EditText mEtLoginPwd;
    @BindView(R.id.iv_login_pwd_del)
    ImageView mIvLoginPwdDel;

    @BindView(R.id.iv_login_hold_pwd)
    ImageView mIvHoldPwd;
    @BindView(R.id.tv_login_forget_pwd)
    TextView mTvLoginForgetPwd;

    @BindView(R.id.bt_login_submit)
    Button mBtLoginSubmit;
    @BindView(R.id.bt_login_register)
    Button mBtLoginRegister;

    @BindView(R.id.ll_login_layer)
    View mLlLoginLayer;
    @BindView(R.id.ll_login_pull)
    LinearLayout mLlLoginPull;

    @BindView(R.id.ll_login_options)
    LinearLayout mLlLoginOptions;

    @BindView(R.id.ib_login_weibo)
    ImageView mIbLoginWeiBo;
    @BindView(R.id.ib_login_wx)
    ImageView mIbLoginWx;
    @BindView(R.id.ib_login_qq)
    ImageView mImLoginQq;

    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";
    public static final String HOLD_USERPWD_KEY="holdUserpwdKey";
    private String HOLD_ACCOUNT="hold_account";
    public static final String HOLD_ACCOUNT_STATE="holdAccountState";
    private int mLogoHeight;
    private int mLogoWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initWidget();
    }

    private void initWidget() {
        mLlLoginLayer.setVisibility(View.GONE);
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username=editable.toString().trim();
                if(username.length()>0){
                    mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mIvLoginUsernameDel.setVisibility(View.VISIBLE);
                }else{
                    mLlLoginUsername.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
                }
                String pwd=mEtLoginPwd.getText().toString().trim();
                if(!TextUtils.isEmpty(pwd)){
                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                }else{
                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                }
            }
        });
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length=editable.length();
                if(length>0){
                    mLlLoginPwd.setBackgroundResource(R.drawable.bg_login_input_ok);
                    mIvLoginPwdDel.setVisibility(View.VISIBLE);
                }else{
                    mIvLoginPwdDel.setVisibility(View.INVISIBLE);
                }
                String username=mEtLoginUsername.getText().toString().trim();
                if(TextUtils.isEmpty(username)){
                    showToastForKeyBord(R.string.message_username_null);
                }
                String pwd=mEtLoginPwd.getText().toString().trim();
                if(!TextUtils.isEmpty(pwd)){
                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.white));
                }else{
                    mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                }
            }
        });
        TextView label=mLayBackBar.findViewById(R.id.tv_navigation_label);
        label.setVisibility(View.INVISIBLE);


    }

    protected  void initData() {
        //
        super.initData();
        //初始化控件状态数据
        SharedPreferences sharedPreferences=getSharedPreferences(HOLD_ACCOUNT, Context.MODE_PRIVATE);
        String holdUsername=sharedPreferences.getString(HOLD_USERNAME_KEY,null);
        mEtLoginUsername.setText(holdUsername);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加监听
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            hideKeyBoard(getCurrentFocus().getWindowToken());
        }catch (Exception e){
            e.printStackTrace();
        }
        mLayBackBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    @OnClick({R.id.ib_navigation_back, R.id.et_login_username, R.id.et_login_pwd, R.id.tv_login_forget_pwd,
            R.id.iv_login_hold_pwd, R.id.bt_login_submit, R.id.bt_login_register, R.id.ll_login_pull, R.id.ib_login_csdn,
            R.id.ib_login_weibo, R.id.ib_login_wx, R.id.ib_login_qq, R.id.ll_login_layer,
            R.id.iv_login_username_del, R.id.iv_login_pwd_del, R.id.lay_login_container})
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.et_login_username:
                mEtLoginPwd.clearFocus();
                mEtLoginUsername.setFocusableInTouchMode(true);
                mEtLoginUsername.requestFocus();
                break;
            //
            case R.id.et_login_pwd:
                mEtLoginUsername.clearFocus();
                mEtLoginPwd.setFocusableInTouchMode(true);
                mEtLoginPwd.requestFocus();
                break;
            //忘记密码
            case R.id.tv_login_forget_pwd:
                //RetrieveActivity.show(LoginActivity.this)
                break;
             //登录
            case R.id.bt_login_submit:
                loginRequest();
                break;
            case R.id.iv_login_hold_pwd:
                break;
            case R.id.bt_login_register:
                //RegisterStepOneActivity.show(LoginActivity.this);
                break;
            case R.id.ll_login_layer:
            case R.id.ll_login_pull:
                mLlLoginPull.animate().cancel();
                mLlLoginLayer.animate().cancel();
                int height=mLlLoginOptions.getHeight();
                float progress=(mLlLoginLayer.getTag()!=null&&mLlLoginLayer.getTag() instanceof  Float)?
                        (float)mLlLoginLayer.getTag():1;
                int time=(int)(360*progress);
                if(mLlLoginPull.getTag()!=null){
                    mLlLoginPull.setTag(null);
                    //沉下
                    glide(height,progress,time);
                }else {
                    mLlLoginPull.setTag(true);
                    //上升
                    upGlide(height,progress,time);
                }
                break;
            case R.id.ib_login_csdn:
                //csdn登录
               // csdnLogin();
                break;
            case R.id.ib_login_qq:
                //QQ登录
                //qqLogin();
                break;
            case R.id.ib_login_weibo:
                //微博登陆
                //weiboLogin();
                break;
            case R.id.ib_login_wx:
                //微信登录
                //wxLogin();
                break;
            case R.id.iv_login_username_del:
                mEtLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                mEtLoginPwd.setText(null);
                break;
            case R.id.lay_login_container:
                try{
                    hideKeyBoard(getCurrentFocus().getWindowToken());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 上升
     * @param height
     * @param progress
     * @param time
     */
    private void upGlide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height* progress)
                .translationY(0)
                .setDuration(time)
                .start();
        mLlLoginLayer.animate()
                .alphaBy(1-progress)
                .alpha(1)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        if(animation instanceof ValueAnimator){
                            mLlLoginLayer.setTag(((ValueAnimator)animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(animation instanceof ValueAnimator){
                            mLlLoginLayer.setTag(((ValueAnimator)animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mLlLoginLayer.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 沉下
     * @param height
     * @param progress
     * @param time
     */
    private void glide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height-height*progress)
                .translationY(height)
                .setDuration(time)
                .start();
        mLlLoginLayer.animate()
                .alphaBy(1*progress)
                .alpha(0)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        if(animation instanceof ValueAnimator){
                            mLlLoginLayer.setTag((((ValueAnimator) animation).getAnimatedValue()));
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mLlLoginLayer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }
                })
                .start();
    }

    private void loginRequest() {
        String username=mEtLoginUsername.getText().toString().trim();
        String password=mEtLoginPwd.getText().toString().trim();
        if(!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(username)){
            if(hasInternet()){
                login(username,password);
            }else{
                showToastForKeyBord(R.string.footer_type_net_error);
            }
        }else{
            showToastForKeyBord(R.string.login_input_username_hint_error);
        }
    }

    private void login(String username, String password) {
        //打开进度对话框
        showFocusWaitDialog();
        Api.login(username, password, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requestFailureHint(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                hideWaitDialog();
                String result=response.body().toString();
                //字符串转换为bean
                LoginResult loginResult=new Gson().fromJson(result,LoginResult.class);
                if(loginResult.isSuccess()){

                    loginSucceed();
                }else {
                    int code=loginResult.getCode();
                    String message=loginResult.getMessage();
                    if(code==211){
                        mEtLoginPwd.setFocusableInTouchMode(false);
                        mEtLoginPwd.clearFocus();
                        mEtLoginUsername.requestFocus();
                        mEtLoginUsername.setFocusableInTouchMode(true);
                        mEtLoginUsername.setBackgroundResource(R.drawable.bg_login_input_error);
                    }else if(code==212){
                        mEtLoginUsername.setFocusableInTouchMode(false);
                        mEtLoginUsername.clearFocus();
                        mEtLoginPwd.requestFocus();
                        mEtLoginPwd.setFocusableInTouchMode(true);
                        message+=","+getResources().getString(R.string.message_pwd_error);
                        mEtLoginPwd.setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    showToastForKeyBord(message);
                }
            }
        });

    }

    private void loginSucceed() {
        View view;
        if((view=getCurrentFocus())!=null){
            hideKeyBoard(view.getWindowToken());
        }
        Toast toast=Toast.makeText(getApplicationContext(),R.string.login_success_hint,Toast.LENGTH_SHORT);
        toast.show();
        setResult(RESULT_OK);
        sendLocalReceiver();
        holdAccount();

    }

    //保存用户名和密码到本地
    private void holdAccount() {
        String username=mEtLoginUsername.getText().toString().trim();
        String password=mEtLoginPwd.getText().toString().trim();
        if(!TextUtils.isEmpty(username)){
            SharedPreferences sharedPreferences=getSharedPreferences(HOLD_ACCOUNT,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(HOLD_USERNAME_KEY,username)
                          .putString(HOLD_USERPWD_KEY,password)
                            .putBoolean(HOLD_ACCOUNT_STATE,true)
                            .apply();
        }
    }


    /**
     * 判断网络是否链接
     * @return
     */
    private boolean hasInternet() {
        ConnectivityManager cm= (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        return info !=null && info.isAvailable() && info.isConnected();

    }

    //获取焦点回调
    @Override
    public void onFocusChange(View view, boolean b) {
        int id=view.getId();
        if(id==R.id.et_login_username){
            if(b){
                mEtLoginUsername.setActivated(true);
                mEtLoginPwd.setActivated(false);
            }
        }else{
            if(b){
                mEtLoginPwd.setActivated(true);
                mEtLoginUsername.setActivated(false);
            }
        }
    }



    @Override
    public void onGlobalLayout() {

        final ImageView ivLogo=this.mIvLoginLogo;
        Rect keypadRect=new Rect();
        //获取可展示的面积
        mLayBackBar.getWindowVisibleDisplayFrame(keypadRect);
        int screenHeight=mLayBackBar.getRootView().getHeight();
        int keypadHeight=screenHeight-keypadRect.bottom;
        //
        if(keypadHeight>0){
            updateKeyBoardActiveStatus(true);
        }else{
            updateKeyBoardActiveStatus(false);
        }
        //键盘弹出，ivLogo动画改变
        if(keypadHeight>0&&ivLogo.getTag()==null){
            final int height=ivLogo.getHeight();
            final int width=ivLogo.getWidth();
            this.mLogoHeight=height;
            this.mLogoWidth=width;
            ivLogo.setTag(true);
            ValueAnimator valueAnimator= ValueAnimator.ofFloat(1,0);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value=(float)valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams=ivLogo.getLayoutParams();
                    layoutParams.width=(int)(width*value);
                    layoutParams.height=(int)(height*value);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(value);
                }
            });
            if(valueAnimator.isRunning()){
                valueAnimator.cancel();
            }
            valueAnimator.start();
        //关闭键盘，ivLogo动画改变
        }else if(keypadHeight==0&&ivLogo.getTag()!=null){
            final int height=mLogoHeight;
            final int width=mLogoWidth;
            ivLogo.setTag(null);
            ValueAnimator valueAnimator=ValueAnimator.ofFloat(0,1);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value=(float)valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams=ivLogo.getLayoutParams();
                    layoutParams.width=(int)(value*width);
                    layoutParams.height=(int)(value*height);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(value);

                }
            });
            if(valueAnimator.isRunning()){
                valueAnimator.cancel();
            }
        }

    }
}
