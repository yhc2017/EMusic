package com.music.ych.emusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.music.ych.util.MusicMedia;
import com.music.ych.util.MusicPlayerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by HUAHUA on 2018/3/6.
 * 事件：zhou-jx
 */

public class LocalMusicActivity extends AppCompatActivity {

    private static int currentposition = -1;//当前播放列表里哪首音乐
    private ArrayList<Map<String, Object>> listems = null;//需要显示在listview里的信息
    public static ArrayList<MusicMedia> musicList = null; //音乐信息列表
    private ListView musicListView = null;
    private Intent intent = null;
    private MusicPlayerService musicPlayerService = null;
    private MediaPlayer mediaPlayer = null;
    //public static SeekBar audioSeekBar = null;//定义进度条
    public boolean isplay = false;
    private Handler handler = null;//处理界面更新，seekbar ,textview
    public TextView title;
    public TextView singer;
    public ImageView start;
    public TextView number;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;//保存播放模式
    //执行事件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
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
        start = (ImageView) super.findViewById(R.id.iv_play_bar_play);
        title = (TextView) super.findViewById(R.id.tv_play_bar_title);
        singer = (TextView) super.findViewById(R.id.tv_play_bar_artist);
        number = (TextView) super.findViewById(R.id.textview_local_music_quantity);
        //处理界面更新
        handler = new Handler();
        intent = new Intent();
        intent.setAction("player");
        intent.setPackage(getPackageName());
        //进度条
        //audioSeekBar = (SeekBar) findViewById(R.id.seekBar);
        //临时记录
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        musicListView = (ListView) super.findViewById(R.id.list_view);//音乐列表
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击播放音乐，不过需要判断一下当前是否有音乐在播放，需要关闭正在播放的
                //position 可以获取到点击的是哪一个，去 musicList 里寻找播放
                Log.i("运行位置","获取到position"+position);
                currentposition = position;
                player(currentposition);
            }
        });

        musicList = scanAllAudioFiles();
        //number.setText(musicList.size());
        listems = new ArrayList<Map<String, Object>>();
        for (Iterator iterator = musicList.iterator(); iterator.hasNext();) {
            Map<String, Object> map = new HashMap<String, Object>();
            MusicMedia mp3Info = (MusicMedia) iterator.next();
            map.put("id",mp3Info.getId());
            map.put("title", mp3Info.getTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
            map.put("albumid", mp3Info.getAlbumId());
            map.put("duration", mp3Info.getTime());
            map.put("size", mp3Info.getSize());
            map.put("url", mp3Info.getUrl());
            map.put("bitmap", R.drawable.ic_online_music_logo);
            listems.add(map);
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                this,
                listems,
                R.layout.music_item,
                new String[] {"bitmap","title","artist", "size","duration"},
                new int[] {R.id.video_imageView,R.id.video_title,R.id.video_singer,R.id.video_size,R.id.video_duration}
                //new String[] {"title","artist", "size","duration"},
                //new int[] {R.id.video_title,R.id.video_singer,R.id.video_size,R.id.video_duration}

        );
        //listview里加载数据
        musicListView.setAdapter(mSimpleAdapter);

    }

    //所有的对按钮的事件进行监听
    public void setEvents() {
        //匿名方法
        MyListener listener = new MyListener();
        start.setOnClickListener(listener);
    }

    public class MyListener implements  View.OnClickListener { /*用接口的方式*/
        public void onClick(View v) {
            int id = v.getId();   /*得到v的id付给id*/
            switch (id) {
                case R.id.iv_play_bar_play:{
                    if(isplay){
                        pause();
                    }else {
                        Log.d("播放按钮点击事件", "成功点击 ");
                        int i = sharedPreferences.getInt("position", -1);
                        if(i == -1)
                            player(1);
                        else
                            player(sharedPreferences.getInt("position", 1));
                        isplay = true;
                        break;
                    }
                }
                default:
                    break;
            }

        }
    }

    //播放逻辑
    private void player() {
        player(currentposition);
    }

    private void player(int position){

        //textView4.setText(musicList.get(position).getTitle()+"   playing...");
        currentposition = position;
        intent.putExtra("cu", position);//把位置传回去，方便再启动时调用
        intent.putExtra("url", musicList.get(position).getUrl());
        intent.putExtra("MSG","0");
        //播放时就改变btn_play_pause图标，下面这个过期了
//        btn_play_pause.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause));
        start.setImageResource(R.drawable.ic_play_bar_btn_pause);

        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.i("MusicPlayerService","MusicActivity...bindService.......");

        title.setText(musicList.get(currentposition).getTitle());
        singer.setText(musicList.get(currentposition).getArtist());
    }
    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {

            musicPlayerService = ((MusicPlayerService.musicBinder)service).getPlayInfo();
            mediaPlayer = musicPlayerService.getMediaPlayer();
            Log.i("MusicPlayerService", "MusicActivity...onServiceConnected.......");
            currentposition = musicPlayerService.getCurposition();
            //设置进度条最大值
//            audioSeekBar.setMax(mediaPlayer.getDuration());
            //这里开了一个线程处理进度条,这个方式官方貌似不推荐，说违背什么单线程什么鬼
///           new Thread(seekBarThread).start();
            //使用runnable + handler
            //handler.post(seekBarHandler);


        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            musicPlayerService = null;
        }

    };


    private void pause() {
        intent.putExtra("MSG","1");
        isplay = false;
        //btn_play_pause.setBackgroundResource(R.drawable.play);
        start.setImageResource(R.drawable.ic_play_bar_btn_play);
        startService(intent);
    }

    //1s更新一次进度条
//    Runnable seekBarThread = new Runnable() {
//        @Override
//        public void run() {
//            while (musicPlayerService != null) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                Log.i("MusicPlayerService", "seekBarThread run.......");
//
//                //audioSeekBar.setProgress(musicPlayerService.getCurrentPosition());
//
//            }
//        }
//    };
//    Runnable seekBarHandler = new Runnable() {
//        @Override
//        public void run() {
//
//            Log.i("MusicPlayerService", "MusicActivity...seekBarHandler run......."+Thread.currentThread().hashCode()+" "+handler.hashCode());
//            //audioSeekBar.setProgress(musicPlayerService.getCurrentPosition());
//            //textView4.setText(musicList.get(musicPlayerService.getCurposition()).getTitle() +"       " +
//              //      musicPlayerService.toTime(musicPlayerService.getCurrentPosition()) +
//               //     "  / " + musicPlayerService.toTime(musicPlayerService.getDuration() ));
//            handler.postDelayed(seekBarHandler, 1000);
//
//        }
//    };

    /*加载媒体库里的音频*/
    public ArrayList<MusicMedia> scanAllAudioFiles(){
        //生成动态数组，并且转载数据
        ArrayList<MusicMedia> mylist = new ArrayList<MusicMedia>();

        /*查询媒体数据库
        参数分别为（路径，要查询的列名，条件语句，条件参数，排序）
        视频：MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        图片;MediaStore.Images.Media.EXTERNAL_CONTENT_URI

         */
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲标题
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));


                if (size >1024*800){//大于800K
                    MusicMedia musicMedia = new MusicMedia();
                    musicMedia.setId(id);
                    musicMedia.setArtist(artist);
                    musicMedia.setSize(size);
                    musicMedia.setTitle(tilte);
                    musicMedia.setTime(duration);
                    musicMedia.setUrl(url);
                    musicMedia.setAlbum(album);
                    musicMedia.setAlbumId(albumId);

                    mylist.add(musicMedia);

                }
                cursor.moveToNext();
            }
        }
        return mylist;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MusicPlayerService", "MusicActivity...onPause........." + Thread.currentThread().hashCode());
        //绑定服务了
        if(musicPlayerService != null){
            unbindService(conn);
        }

    }

}
