package com.example.myapplication.activities;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;
import com.example.myapplication.helper.DialogHelper;

/**
 * @Author: weidie zeng
 * @mail 1061875902@qq.com
 * @CreateDate: 2020/8/19 10:24
 * @Description:
 */
public class AccountBaseActivity extends AppCompatActivity {
    private ProgressDialog mDialog;
    public static final String ACTION_ACCOUNT_FINISH_ALL="com.eample.myapplication.action.account.finish";
    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;
    protected InputMethodManager mInputMethodManager;
    protected Toast mToast;
    private boolean mKeyBoardIsActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideWaitDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            hideKeyBoard(getCurrentFocus().getWindowToken());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mManager!=null){
            if(mReceiver!=null){
                mManager.unregisterReceiver(mReceiver);
            }
        }
    }

    /**
     * 初始化数据
     * @
     */
    protected void initData(){
        registerLocalReceiver();
        mInputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }
    private void showToast(String text){
        Toast toast=this.mToast;
        if(toast==null){
            toast=initToast();
        }
        View rootView= LayoutInflater.from(this).inflate(R.layout.view_toast,null,false);
        TextView textView=(TextView)rootView.findViewById(R.id.title_tv);
        textView.setText(text);
        toast.setView(rootView);
        initToastGravity(toast);
        toast.show();
    }
    private void showToast( int id) {
        Toast toast = this.mToast;
        if (toast == null) {
            toast = initToast();
        }
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_toast, null, false);
        TextView textView = (TextView) rootView.findViewById(R.id.title_tv);
        textView.setText(id);
        toast.setView(rootView);
        initToastGravity(toast);
        toast.show();
    }

    private void initToastGravity(Toast toast) {
        //如果键盘还显示
        if(this.mKeyBoardIsActive){
            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
        }else{
            toast.setGravity(Gravity.BOTTOM,0,0);
        }
    }

    /**
     *
     * @return
     */
    private Toast initToast() {
        Toast toast=new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        this.mToast=toast;
        return toast;
    }

    protected boolean sendLocalReceiver(){
        if(mManager!=null){
            Intent intent=new Intent();
            intent.setAction(ACTION_ACCOUNT_FINISH_ALL);
            return mManager.sendBroadcast(intent);
        }
        return false;
    }

    protected void hideKeyBoard(IBinder windowToken) {
        if(windowToken==null){
            return;
        }
        InputMethodManager inputMethodManager=this.mInputMethodManager;
        if(inputMethodManager==null)return;
        boolean active=inputMethodManager.isActive();
        if(active){
            inputMethodManager.hideSoftInputFromWindow(windowToken,0);
        }
    }

    protected void showToastForKeyBord(@StringRes int id) {
        showToast(id);
    }

    protected void showToastForKeyBord(String message) {
        showToast(message);
    }
    protected void updateKeyBoardActiveStatus(boolean isActive){
        this.mKeyBoardIsActive=isActive;
    }
    protected void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.cancel();
                // dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * register localReceiver
     */
    private void registerLocalReceiver() {
        if(mManager==null){
            mManager=LocalBroadcastManager.getInstance(this);
            IntentFilter filter=new IntentFilter();
            filter.addAction(ACTION_ACCOUNT_FINISH_ALL);
            if(mReceiver==null){
                mReceiver=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action =intent.getAction();
                        if(ACTION_ACCOUNT_FINISH_ALL.equals(action)){
                            finish();
                        }
                    }
                };
                mManager.registerReceiver(mReceiver,filter);
            }
        }
    }
    /**
     * show WaitDialog
     */
    //@SuppressLint("ResourceType")
    @SuppressLint("ResourceType")
    protected ProgressDialog showWaitDialog(@StringRes int messageId) {
        if (mDialog == null) {
            if (messageId <= 0) {
                mDialog = DialogHelper.getProgressDialog(this, true);
            } else {
                String message = getResources().getString(messageId);
                mDialog = DialogHelper.getProgressDialog(this, message, true);
            }
        }
        mDialog.show();

        return mDialog;
    }
    /**
     * show FocusWaitDialog
     *
     * @return progressDialog
     */
    protected ProgressDialog showFocusWaitDialog() {

        String message = getResources().getString(R.string.progress_submit);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message, false);//DialogHelp.getWaitDialog(this, message);
        }
        mDialog.show();

        return mDialog;
    }

    /**
     * request network error
     *
     * @param throwable throwable
     */
    protected void requestFailureHint(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
        showToastForKeyBord(R.string.request_error_hint);
    }


}
