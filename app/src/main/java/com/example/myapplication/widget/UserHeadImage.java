package com.example.myapplication.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * @Author: weidie zeng
 * @mail 1061875902@qq.com
 * @CreateDate: 2020/8/31 10:44
 * @Description: 用户头像自定义控件
 */
public class UserHeadImage extends androidx.appcompat.widget.AppCompatImageView {

    public UserHeadImage(Context context) {
        super(context);
    }

    public UserHeadImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UserHeadImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
