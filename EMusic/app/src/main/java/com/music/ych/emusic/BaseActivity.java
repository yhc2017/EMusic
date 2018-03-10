package com.music.ych.emusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by HUAHUA on 2018/3/6.
 */

public class BaseActivity extends AppCompatActivity{
    /**
     * windowManager对象
     */
    private WindowManager windowManager;
    /**
     * 根视野
     */
    private FrameLayout mContentContainer;
    /**
     * 浮动视野
     */
    private View mFloatView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        mContentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        mFloatView =  LayoutInflater.from(getBaseContext()).inflate(R.layout.include_play_bar, null);

  }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        mContentContainer.addView(mFloatView,layoutParams);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    /***
     * 重点，设置这个可以实现前进Activity时候的无动画切换
     * @param intent
     */
    @Override
    public void startActivity(Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//设置切换没有动画，用来实现活动之间的无缝切换
        super.startActivity(intent);
    }

    /**
     *  重点，在这里设置按下返回键，或者返回button的时候无动画
     */
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(0, 0);//设置返回没有动画
    }
}
