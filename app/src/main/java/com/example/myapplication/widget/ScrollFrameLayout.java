package com.example.myapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

/**
 * @Author: weidie zeng
 * @mail 1061875902@qq.com
 * @CreateDate: 2020/8/16 10:24
 * @Description: 滚动图
 */

public class ScrollFrameLayout extends FrameLayout {
    /*
    *重绘间隔时间
    */
    private final static long DEFAULT_DRAW_INTERVALS_TIME=5L;
    /**
     * 间隔时间内平移距离
     */

    private float mPanDistance=0;
    /**
     * 间隔时间内平移增距
     */
    private float mIntervalIncreaseDistance=0.5f;
    /**
     * 填满当前View所需bitap数量
     */
    private int mBitmapCount=0;
    /**
     * 是否开始滚动
     */
    private boolean isSroll;
    /**
     * 遮罩层颜色
     */
    private int mMaskLayerColor;
    private Drawable mDrawable;
    private Bitmap mSrcBitmap;
    private Paint mPaint;
    private Matrix mMatrix;
    public ScrollFrameLayout(@NonNull Context context) {
        this(context,null,0);
    }
    public ScrollFrameLayout(@NonNull Context context, @NonNull AttributeSet attributeSet){
        this(context,attributeSet,0);
    }

    public ScrollFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context,attributeSet,i);
        TypedArray array=context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.ScrollFrameLayout,i,0);
        int speed=array.getInteger(R.styleable.ScrollFrameLayout_speed,3);
        mIntervalIncreaseDistance=speed*mIntervalIncreaseDistance;
        mDrawable=array.getDrawable(R.styleable.ScrollFrameLayout_src);
        isSroll=array.getBoolean(R.styleable.ScrollFrameLayout_isScorll,true);
        mMaskLayerColor=array.getInteger(R.styleable.ScrollFrameLayout_maskLayerColor, Color.TRANSPARENT);
        array.recycle();

        //调用Draw
        setWillNotDraw(true);
        mPaint=new Paint();
        mMatrix=new Matrix();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mDrawable==null||!(mDrawable instanceof BitmapDrawable)){
            return;
        }
        if(getVisibility()==GONE){
            return;
        }
        if(w==0||h==0){
            return;
        }
        if(mSrcBitmap==null){
            Bitmap bitmap=((BitmapDrawable) mDrawable).getBitmap();
            //按当前View宽度比例缩放Bitmap
            mSrcBitmap=scaleBitmap(bitmap,getMeasuredWidth());
            //计算至少需要几个bitmap才能填满当前View
            mBitmapCount=getMeasuredHeight()/mSrcBitmap.getHeight()+1;
            if(!bitmap.isRecycled()){
                bitmap.recycle();
                System.gc();
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(mSrcBitmap==null){
            return;
        }
        if(mSrcBitmap.getHeight()+mPanDistance!=0){
            //第一张图片未完全滚出屏幕
            mMatrix.reset();
            mMatrix.postTranslate(0,mPanDistance);
            canvas.drawBitmap(mSrcBitmap,mMatrix,mPaint);
        }
        if(mSrcBitmap.getHeight()+mPanDistance<getMeasuredHeight()){
            //用于补充留白的图片出现在屏幕
            for(int i=0;i<mBitmapCount;i++){
                mMatrix.reset();
                mMatrix.postTranslate(0,(i+1)*mSrcBitmap.getHeight()+mPanDistance);
                canvas.drawBitmap(mSrcBitmap,mMatrix,mPaint);
            }
        }
        //绘制遮罩层
        if(mMaskLayerColor!=Color.TRANSPARENT){
            canvas.drawColor(mMaskLayerColor);
        }
        //延时重绘实现滚动效果
        if(isSroll){
            getHandler().postDelayed(mRedrawRunable,DEFAULT_DRAW_INTERVALS_TIME);
        }

    }
    private Runnable mRedrawRunable=new Runnable() {
        @Override
        public void run() {
            if(mSrcBitmap.getHeight()+mPanDistance<=0){
                //第一张已完全滚出屏幕，重置平移距离
                mPanDistance=0;
            }
            mPanDistance=mPanDistance-mIntervalIncreaseDistance;
            invalidate();
        }
    };

//    public void setSrcBitmap(Bitmap srcBitmap){
//        boolean oldScrollStatus=isSroll;
//        if(oldScrollStatus){
//
//        }
//    }


    private Bitmap scaleBitmap(Bitmap originBitmap, int newWidth) {
        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();
        float scaleWidth = (float) newWidth / width;
        float newHeight = scaleWidth * height;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(originBitmap, 0, 0, width, height, matrix, true);
    }
}
