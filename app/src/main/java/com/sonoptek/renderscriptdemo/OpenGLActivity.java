package com.sonoptek.renderscriptdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by zhangchongshan on 2020/11/24.
 */
public class OpenGLActivity extends AppCompatActivity {
    private MyGLView glView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView=new MyGLView(this);
        setContentView(glView);
    }
}
