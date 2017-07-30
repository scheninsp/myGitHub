package com.infi.uiwidgetttest;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    int imgId_p1 = R.drawable.ssy_p2;
    int imgId_p4 = R.drawable.ssy_p4;
    int imgId_p0 = R.drawable.jd_q_s;
    private Button button_pick, button_lvlup, button_clear;
    private ImageView imgPhoto, imgSupport;
    private enum State {P0,P1,P4};
    private State sys_state;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPhoto = (ImageView) findViewById(R.id.img_main);
        imgSupport = (ImageView) findViewById(R.id.img_small);

        mp = MediaPlayer.create(this,R.raw.ssy_sound2);

        sys_state=State.P0;
        setState(sys_state,imgPhoto,imgSupport);

        button_pick = (Button) findViewById(R.id.button_pick);
        button_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sys_state=State.P1;
                setState(sys_state,imgPhoto,imgSupport);
            }
        });

        button_lvlup = (Button) findViewById(R.id.button_lvlup);
        button_lvlup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sys_state == State.P0){
                    sys_state=State.P0;
                }
                else{
                    sys_state=State.P4;
                    setState(sys_state,imgPhoto,imgSupport);
                }
            }
        });

        button_clear = (Button) findViewById(R.id.button_clear);
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sys_state=State.P0;
                setState(sys_state,imgPhoto,imgSupport);
            }
        });

    }

    private void setState(State statevar, ImageView imgPhoto, ImageView imgSupport){
        switch (statevar) {
            case P0:
                //new MyThread().start();
                imgPhoto.setVisibility(View.INVISIBLE);
                imgSupport.setVisibility(View.VISIBLE);
                imgSupport.setImageResource(imgId_p0);
                break;

            case P1:
                imgSupport.setVisibility(View.INVISIBLE);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPhoto.setImageResource(imgId_p1);
                mp.start();
                break;

            case P4:
                imgSupport.setVisibility(View.INVISIBLE);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPhoto.setImageResource(imgId_p4);
                break;
            default:
                break;
        }
    }

    /*
    class MyThread extends Thread {
        public Handler handler;
        @Override
        public void run() {
            while (true) {
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);

                try{Thread.sleep(1000);}
                catch (InterruptedException e){}

            }
        }
    }*/

}
