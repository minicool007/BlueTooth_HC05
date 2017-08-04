package com.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private BluetoothAdapter blueadapter=null;
    private ListView deviceListview; //可用蓝牙设备列表
    private ArrayAdapter<String> adapter;
    private List<String> deviceList=new ArrayList<String>();
    private Button btserch,btopen,btmain;
    private boolean hasregister=false; //注册广播
    private DeviceReceiver mydevice=new DeviceReceiver(); //广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //组件注册
        blueadapter=BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器
        deviceListview=(ListView)findViewById(R.id.devicelist);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
        deviceListview.setAdapter(adapter);
        deviceListview.setOnItemClickListener(this);//设置listView的点击事件

        btserch=(Button)findViewById(R.id.start_seach);
        btmain=(Button)findViewById(R.id.open_main);
        btopen=(Button)findViewById(R.id.open_bluetooth);
        btserch.setOnClickListener(new ClinckMonitor());
        btopen.setOnClickListener(new ClinckMonitor());
        btmain.setOnClickListener(new ClinckMonitor());

    }

    //设置listView的点击事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
    {
        // TODO Auto-generated method stub
        Log.e("msgParent", "Parent= "+arg0);
        Log.e("msgView", "View= "+arg1);
        Log.e("msgChildView", "ChildView= "+arg0.getChildAt(pos-arg0.getFirstVisiblePosition()));

        final String msg = deviceList.get(pos);

        if(blueadapter!=null&&blueadapter.isDiscovering()){
            blueadapter.cancelDiscovery();
            btserch.setText("repeat search");
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);// 定义一个弹出框对象
        dialog.setTitle("确定是否连接设备？");
        dialog.setMessage(msg);
        dialog.setPositiveButton("连接",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothMsg.BlueToothAddress=msg.substring(msg.length()-17);//获取MAC地址...其实就是硬件地址...因为连接设别必然从物理地址下手...

                        if(BluetoothMsg.lastblueToothAddress!=BluetoothMsg.BlueToothAddress){
                           BluetoothMsg.lastblueToothAddress=BluetoothMsg.BlueToothAddress;
                        }
                        Intent in=new Intent(MainActivity.this,BluetoothActivity.class);
                        startActivity(in);

                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothMsg.BlueToothAddress = null;
                    }
                });
        dialog.show();
    }

    private class ClinckMonitor implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.open_bluetooth:
                    if (blueadapter.getState()==BluetoothAdapter.STATE_OFF)
                    {
                        setBluetooth();
                        btopen.setText("关闭蓝牙"); //改变按钮的文字
                    }
                    else if (blueadapter.getState()==BluetoothAdapter.STATE_ON)
                    {
                        blueadapter.disable();
                        btopen.setText("打开蓝牙");
            }
            break;
                case R.id.start_seach:
                    if (blueadapter.getState()==BluetoothAdapter.STATE_OFF)
                    {
                        Toast.makeText(getApplicationContext(), "请先打开蓝牙", Toast.LENGTH_LONG).show();
                    }
                    else if (blueadapter.getState()==BluetoothAdapter.STATE_ON)
                    {
                        if(blueadapter.isDiscovering()){
                            blueadapter.cancelDiscovery();
                            btserch.setText("再次搜索");
                        }else{
                            findAvalibleDevice();
                            blueadapter.startDiscovery();
                            btserch.setText("停止搜索");
                        }
                    }
                    break;
                case R.id.open_main:
                    Intent intent=new Intent().setClass(MainActivity.this, testActivity.class);
                    startActivity(intent);
                default:
                    break;
            }

        }
    }
    //开启蓝牙
    private void setBluetooth(){

        if(blueadapter!=null){  //Device support Bluetooth
            //确认开启蓝牙
            if(!blueadapter.isEnabled()){
                //请求用户开启
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RESULT_FIRST_USER);//打开本机的蓝牙功能...使用startActivityForResult（）方法...这里我们开启的这个Activity是需要它返回执行结果给主Activity的...
                //使蓝牙设备可见，方便配对
                Intent in=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);// 设置蓝牙的可见性，最大值3600秒，默认120秒，0表示永远可见(作为客户端，可见性可以不设置，服务端必须要设置)
                startActivity(in);//这里只需要开启另一个activity，让其一直显示蓝牙...没必要把信息返回..因此调用startActivity()
                //直接开启，不经过提示
                blueadapter.enable();//这步才是真正打开蓝牙的部分....
            }
        }
        else{   //Device does not support Bluetooth

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("没有蓝牙设备");
            dialog.setMessage("你的设备不支持蓝牙，请更换设备");

            dialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialog.show();
        }
    }
    //搜索蓝牙设备
    private void findAvalibleDevice(){
        //获取可配对蓝牙设备
        Set<BluetoothDevice> device=blueadapter.getBondedDevices();

        if(blueadapter!=null&&blueadapter.isDiscovering()){
            deviceList.clear();
            adapter.notifyDataSetChanged();
        }
        if(device.size()>0){ //存在已经配对过的蓝牙设备
            for(Iterator<BluetoothDevice> it = device.iterator(); it.hasNext();){
                BluetoothDevice btd=it.next();
                deviceList.add(btd.getName()+'\n'+btd.getAddress());
                adapter.notifyDataSetChanged();
            }
        }else{  //不存在已经配对过的蓝牙设备
            deviceList.add("不存在已经配对过的蓝牙设备");
            adapter.notifyDataSetChanged();
        }
    }
    //注册广播
    @Override
    protected void onStart()
    {
        //注册蓝牙接收广播
        if(!hasregister){
            hasregister=true;
            //添加过滤器，并注册广播，用于监听蓝牙发现信息
            IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mydevice, filterStart);
            registerReceiver(mydevice, filterEnd);
        }
        super.onStart();
    }
    //注销广播
    @Override
    protected void onDestroy()
    {
        //使蓝牙设备不可见
        if(blueadapter!=null&&blueadapter.isDiscovering()){
            blueadapter.cancelDiscovery();
        }
        //取消注册
        if(hasregister){
            hasregister=false;
            unregisterReceiver(mydevice);
        }
        super.onDestroy();
    }
    //定义BroadcastReceiver
    private class DeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){    //搜索到新设备
                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索没有配过对的蓝牙设备
                if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                    deviceList.add(btd.getName()+'\n'+btd.getAddress());
                    adapter.notifyDataSetChanged();
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){   //搜索结束

                if (deviceListview.getCount() == 0) {
                    deviceList.add("没有可匹配的设备");
                    adapter.notifyDataSetChanged();
                }
                btserch.setText("重新搜索");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(resultCode){
            case RESULT_OK:
                findAvalibleDevice();
                break;
            case RESULT_CANCELED:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
