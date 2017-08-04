package com.bluetooth;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by angela on 2017/6/19.
 */
public class myApplication extends Application{

    private ArrayList<String> msgList;  //用于存储消息列表
    private ArrayList<String> list = new ArrayList<String>();

    public myApplication(){
        msgList = new ArrayList<String>();
    };
    public ArrayList<String> getMsgList() {
        return msgList;
    }

    public void setMsgList(String msg) {
        list.add(msg);
        this.msgList = list;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setMsgList("蓝牙已连接");//初始化全局变量
    }
}
