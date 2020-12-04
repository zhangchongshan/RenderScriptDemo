package com.sonoptek.renderscriptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    RenderscriptTest mrsTest;

    @ViewById(R.id.img)
    ImageView mImg;

    @ViewById(R.id.tx_result)
    TextView mTxResult;

    @AfterViews
    void afterViewProcess(){
        Log.e("TTTT", "afterViewProcess: " );
        mrsTest=new RenderscriptTest(this);
//        mImg.setImageResource(R.drawable.timg);
    }

    @Click({R.id.invert_bitmap})
    void invertBitmap(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.timg);
        bitmap=mrsTest.invertBitmap(bitmap);
        mImg.setImageBitmap(bitmap);
    }

    @Click({R.id.add})
    void add(){
        /*int[] arr=new int[10];
        for (int i=0;i<arr.length;i++){
            arr[i]=(i+1);
        }
        Log.e("TTT", "add: start"+Arrays.toString(arr) );
        int addResult=mrsTest.add(arr);
//        Log.e("TTTT", "add: java" );
//        int addJava=addJava(arr);
        Log.e("TTTT", "add: end");
        mTxResult.setText("Add: ="+addResult);*/

        arrayTest();
    }

    @Click({R.id.min_max})
    void minAndMax(){
        long[] arr= new long[100];
        Random random=new Random();
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<arr.length;i++){
            Long rand= Long.valueOf(random.nextInt(1000));
            arr[i]=rand;
            stringBuilder.append(rand+",");
        }
//        Arrays.sort(arr);
//        Log.e("TTTT", "minAndMax: "+Arrays.toString(arr) );
        Log.e("TTTT", "minAndMax: rs start" );
        long[] ret=mrsTest.MinAndMax(arr);
        Log.e("TTTT", "minAndMax: rs end" );

//        List<Long> longs = Arrays.stream(arr).boxed().collect(Collectors.toList());
//        Log.e("TTTT", "minAndMax: java start" );
//        long max=Collections.max(longs);
//        long min=Collections.min(longs);
//        Log.e("TTTT", "minAndMax: java end max :"+max+"  min: "+min );


        mTxResult.setText("Min: "+ret[0]+" Max： "+ret[1] );
    }

    @Click({R.id.yuv2rgb})
    void yuv2RGB(){
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.timg);
        bitmap=mrsTest.yuv2RGB(bitmap);
        mImg.setImageBitmap(bitmap);

    }

    @Click({R.id.gl_view})
    void intentToGL(){
        Intent intent=new Intent(this,OpenGLActivity.class);
        startActivity(intent);
    }
    @Click({R.id.surface_view})
    void intentToSurface(){
        Intent intent=new Intent(this,SurfaceActivity.class);
        startActivity(intent);
    }

    private int addJava(int[] array){
        int add=0;
        for (int i:array){
            add+=i;
        }
        return add;
    }

    private void arrayTest(){
        int [] src={1,2,5,6,8,2};
        int[] ints = Arrays.copyOf(src, 5);
        int[] ints1 = Arrays.copyOfRange(src, 2, 100);
        List<Integer> collect = Arrays.stream(ints1).boxed().collect(Collectors.toList());
        Collections.reverse(collect);
        Collections.sort(collect);
        HashSet<Integer> integers = new HashSet<>(collect);
        collect=new ArrayList<>(integers);
        int[] ints2 = collect.stream().mapToInt(Integer::intValue).toArray();
        System.out.println(Arrays.toString(ints2));
        System.out.println(Arrays.toString(ints1));

    }
}