package com.bluetooth;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by angela on 2017/6/19.
 */
public class testActivity extends Activity {

    SurfaceView sView;
    SurfaceHolder surfaceHolder;
    int screenWidth, screenHeight;
    Camera camera; // 定义系统所用的照相机
    boolean isPreview = false; // 是否在浏览中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.test);

        screenWidth = 500;
        screenHeight = 375;
        sView = (SurfaceView) findViewById(R.id.sView); // 获取界面中SurfaceView组件
        surfaceHolder = sView.getHolder(); // 获得SurfaceView的SurfaceHolder

        // 为surfaceHolder添加一个回调监听器
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera(); // 打开摄像头
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 如果camera不为null ,释放摄像头
                if (camera != null) {
                    if (isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                System.exit(0);
            }
        });
        // 设置该SurfaceView自己不维护缓冲
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initCamera() {
        if (!isPreview) {
            camera = Camera.open();
        }
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(screenWidth, screenHeight); // 设置预览照片的大小
                parameters.setPreviewFpsRange(20, 30); // 每秒显示20~30帧
                parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
                parameters.setPictureSize(screenWidth, screenHeight); // 设置照片的大小
                // camera.setParameters(parameters); // android2.3.3以后不需要此行代码
                camera.setPreviewDisplay(surfaceHolder); // 通过SurfaceView显示取景画面
                //camera.setPreviewCallback(new StreamIt(ipname)); // 设置回调的类
                camera.startPreview(); // 开始预览
                camera.autoFocus(null); // 自动对焦
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

}

class StreamIt implements Camera.PreviewCallback {
    private String ipname;

    public StreamIt(String ipname) {
        this.ipname = ipname;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        try {
            // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
                outstream.flush();
                // 启用线程将图像数据发送出去
                Thread th = new MyThread(outstream, ipname);
                th.start();
            }
        } catch (Exception ex) {
            Log.e("Sys", "Error:" + ex.getMessage());
        }
    }
}

class MyThread extends Thread {
    private byte byteBuffer[] = new byte[1024];
    private OutputStream outsocket;
    private ByteArrayOutputStream myoutputstream;
    private String ipname;

    public MyThread(ByteArrayOutputStream myoutputstream, String ipname) {
        this.myoutputstream = myoutputstream;
        this.ipname = ipname;
        try {
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // 将图像数据通过Socket发送出去
            Socket tempSocket = new Socket(ipname, 6000);
            outsocket = tempSocket.getOutputStream();
            ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            myoutputstream.flush();
            myoutputstream.close();
            tempSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

