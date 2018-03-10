package com.music.ych.emusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by HUAHUA on 2018/2/14.
 */

public class HomePageActivity extends AppCompatActivity {
    private ImageView imageView_local;



    //执行事件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        setEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_online_music, menu);
        return true;
    }
    //所有按钮实例成对象
    public void initViews() {
        imageView_local = (ImageView)findViewById(R.id.roundImageView_local);

    }
    //所有的对按钮的事件进行监听
    public void setEvents() {
        //匿名方法
        MyListener listener = new MyListener();
        imageView_local.setOnClickListener(listener);
    }
    //选择触发的事件
    public class MyListener implements  View.OnClickListener { /*用接口的方式*/
        public void onClick(View v) {
            Intent intent = null;
            int id = v.getId();   /*得到v的id付给id*/
            switch (id) {
                case R.id.roundImageView_local:{
                    intent = new Intent(HomePageActivity.this, LocalMusicActivity.class);
                    startActivity(intent);
                    Log.d("本地音乐点击事件", "成功点击 ");
                    break;
                }
                default:
                    break;
            }

        }
    }
}
