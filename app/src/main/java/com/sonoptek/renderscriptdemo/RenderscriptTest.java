package com.sonoptek.renderscriptdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.Int2;
import androidx.renderscript.RenderScript;
import androidx.renderscript.Script;
import androidx.renderscript.ScriptIntrinsic3DLUT;
import androidx.renderscript.ScriptIntrinsicBlur;
import androidx.renderscript.ScriptIntrinsicYuvToRGB;
import androidx.renderscript.Type;

import java.io.ByteArrayOutputStream;

/**
 * Created by zhangchongshan on 2020/11/19.
 */
public class RenderscriptTest {

    RenderScript mRs;
    ScriptC_renderscriptTest c_renderscriptTest;
    ScriptIntrinsicYuvToRGB yuvToRGB;
    ScriptIntrinsicBlur blur;
    public RenderscriptTest(Context context){
        mRs=RenderScript.create(context);
        c_renderscriptTest=new ScriptC_renderscriptTest(mRs);
        yuvToRGB=ScriptIntrinsicYuvToRGB.create(mRs,Element.U8(mRs));
    }

    public Bitmap invertBitmap(Bitmap in){
        Allocation inAllocation=Allocation.createFromBitmap(mRs,in);
        Allocation outAllocation=Allocation.createTyped(mRs,inAllocation.getType());
        c_renderscriptTest.set_color(255);
        c_renderscriptTest.forEach_invert(inAllocation,outAllocation);
        Bitmap retBit=Bitmap.createBitmap(in);
        outAllocation.copyTo(retBit);
        return retBit;
    }

    public int add(int[] array){

        ScriptC_renderscriptTest.result_int result_int = c_renderscriptTest.reduce_addint(array);


        Type typeInt=Type.createX(mRs, Element.I32(mRs),array.length);
        Allocation allocationIn=Allocation.createTyped(mRs,typeInt);
        allocationIn.copy1DRangeFrom(0,array.length,array);
        ScriptC_renderscriptTest.result_int result_int1 = c_renderscriptTest.reduce_addint(allocationIn);

        int add=result_int1.get();
        return add;
    }

    public long[] MinAndMax(long[] array){
        ScriptC_renderscriptTest.result_int2 result_int2 = c_renderscriptTest.reduce_findMinAndMax(array);
        long[] ret=new long[2];
        Int2 int2 = result_int2.get();
        ret[0]=array[int2.x];
        ret[1]=array[int2.y];
        return ret;
    }

    public int findMax(int[] array){
        ScriptC_renderscriptTest.result_int result_int = c_renderscriptTest.reduce_findMax(array);
        return result_int.get();

    }

    public Bitmap yuv2RGB(Bitmap bitmap){

        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int length=bitmap.getWidth()*bitmap.getHeight();
        int[] pixels = new int[length];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

        calcMap();
        byte[] frameData = new byte[width * height * 3 / 2];
        RGB2YUV420Planar(pixels,width,height,frameData);


//        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//        byte[] pixels=byteArrayOutputStream.toByteArray();


//        Allocation inI32=Allocation.createSized(mRs,Element.I32(mRs),pixels.length);
        Allocation outUChar=Allocation.createSized(mRs,Element.U8(mRs),frameData.length);
        outUChar.copyFrom(frameData);
//        inI32.copyFrom(pixels);
//        c_renderscriptTest.forEach_I32toU8(inI32,outUChar);

//        Allocation in=outUChar;
        Allocation out=Allocation.createSized(mRs,Element.RGBA_8888(mRs),bitmap.getWidth()*bitmap.getHeight());

        yuvToRGB.setInput(outUChar);
        yuvToRGB.forEach(out);
        Bitmap retBit=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        out.copyTo(retBit);
        return retBit;
    }

    protected int[] mapY = new int[256];
    protected int[] mapU = new int[256];
    protected int[] mapV = new int[256];
    protected boolean mapReady = false;
    private void calcMap() {
        if (mapReady)
            return;
        int R,G,B;
        int Y,U,V;
        for (int gray=0; gray<256;gray++) {
            R = G = B = gray;
            Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;      Y = Y < 0 ?  0 : Y;     Y = Y > 255 ? 255 : Y;
            U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;    U = U < 0 ?  0 : U;     U = U > 255 ? 255 : U;
            V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;     V = V < 0 ?  0 : V;     V = V > 255 ? 255 : V;
            mapY[gray] = Y;
            mapU[gray] = U;
            mapV[gray] = V;
        }
        mapReady = true;
    }
    private void RGB2YUV420SemiPlanar(int[] pixels, int width, int height, byte[] frameData) {
        int a,R,G,B,Y,U,V;
        boolean isGray = false;
        int indexRGB = 0;
        int indexY = 0;
        int indexUV = width*height;
        int index = 0;
        for (int y=0;y<height;y++) {
            for (int x=0;x<width;x++) {
                int argb = pixels[index];
                index++;
                a = (argb & 0xFF000000) >> 24;
                R = (argb & 0x00FF0000) >> 16;
                G = (argb & 0x0000FF00) >> 8;
                B = (argb & 0x000000FF) >> 0;

                if (R==G && G==B) {
                    isGray = true;
                } else {
                    isGray = false;
                }

                if (isGray) {
                    Y = mapY[R];
                } else {
                    Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                    Y = Y < 0 ? 0 : Y;
                    Y = Y > 255 ? 255 : Y;
                }

                frameData[indexY] = (byte) Y;
                indexY++;

                if (x % 2 == 0 && y % 2 == 0) {
                    if (isGray) {
                        U = mapU[R];
                        V = mapV[R];
                    } else {
                        U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                        U = U < 0 ? 0 : U;
                        U = U > 255 ? 255 : U;
                        V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                        V = V < 0 ? 0 : V;
                        V = V > 255 ? 255 : V;
                    }

                    frameData[indexUV] = (byte) U;
                    indexUV++;
                    frameData[indexUV] = (byte) V;
                    indexUV++;
                }
            }
        }
    }

    private void RGB2YUV420Planar(int[] pixels, int width, int height, byte[] frameData) {
        int a,R,G,B,Y,U,V;
        boolean isGray = false;
        int indexRGB = 0;
        int indexY = 0;
        int indexU = width*height;
        int indexV = indexU + (width*height)/4;
        int index = 0;
        for (int y=0;y<height;y++) {
            for (int x=0;x<width;x++) {
                int argb = pixels[index];
                index++;
                a = (argb & 0xFF000000) >> 24;
                R = (argb & 0x00FF0000) >> 16;
                G = (argb & 0x0000FF00) >> 8;
                B = (argb & 0x000000FF) >> 0;

                if (R==G && G==B) {
                    isGray = true;
                } else {
                    isGray = false;
                }

                if (isGray) {
                    Y = mapY[R];
                } else {
                    Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                    Y = Y < 0 ? 0 : Y;
                    Y = Y > 255 ? 255 : Y;
                }

                frameData[indexY] = (byte) Y;
                indexY++;

                if (x % 2 == 0 && y % 2 == 0) {
                    if (isGray) {
                        U = mapU[R];
                    } else {
                        U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                        U = U < 0 ? 0 : U;
                        U = U > 255 ? 255 : U;
                    }
                    frameData[indexU] = (byte)U;
                    indexU++;
                } else if (x%2 == 0) {
                    if (isGray) {
                        V = mapV[R];
                    } else {
                        V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                        V = V < 0 ? 0 : V;
                        V = V > 255 ? 255 : V;
                    }
                    frameData[indexV] = (byte)V;
                    indexV++;
                }
            }
        }
    }
}
