package com.bluetooth;
//参考网址：http://www.cnblogs.com/RGogoing/p/4680306.html
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//蓝牙数据传输
public class BluetoothActivity extends Activity {

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    public RadioButton it1,it2,it3,it4; //菜单按钮
    private RadioGroup tab_menu;
    public FragmentItem1 Fitem1;
    public FragmentItem2 Fitem2;
    public FragmentItem3 Fitem3;
    public baidumapFragment Fitem4;
    private DBManager mgr;  //数据库相关操作类

    private BMapManager mapManager;

    //public List<String> msgList=new ArrayList<String>();  //用于存储消息列表
    public ArrayList<String> mssgList=new ArrayList<String>();
    public myApplication msgList ;

    public static List<String> test = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private EditText editMsgView;
    private Button sendButton;
    private Button disconnectButton;
    private BluetoothSocket socket = null;
    private clientThread clientConnectThread = null;
    private BluetoothDevice device = null;

    private readThread mreadThread = null;
    private ServerThread startServerThread = null;
    private BluetoothServerSocket mserverSocket = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Context mContext;
    int flag = 0; //标志哪一个页面
    public String str = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.data_transfer);
        //获取全局变量
        msgList = (myApplication) getApplicationContext();

/*        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidu_map);*/
        //setContentView(R.layout.test);
        menuInit(); //菜单初始化
        //dbInit();//数据库初始化---待测试
        //bluetoothInit();//蓝牙初始化
        blueInit();//蓝牙连接初始化
    }

    private void menuInit() {
        it1=(RadioButton)findViewById(R.id.it1);
        it2=(RadioButton)findViewById(R.id.it2);
        it3=(RadioButton)findViewById(R.id.it3);
        it4=(RadioButton)findViewById(R.id.it4);

        Fitem1 = new FragmentItem1(BluetoothActivity.this);
/*        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("mssgList", mssgList);
        Fitem1.setArguments(bundle);*/
        getFragmentManager().beginTransaction().replace(R.id.main_content, Fitem1).commit();
        flag = 1;

        //设置页面切换
        tab_menu = (RadioGroup) findViewById(R.id.tab_menu);
        tab_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId) {
                    case R.id.it1:
                        Fitem1 = new FragmentItem1(BluetoothActivity.this);
                        getFragmentManager().beginTransaction().replace(R.id.main_content, Fitem1)
                                .commit();
                        flag = 1;
                        break;
                    case R.id.it2:
                        Fitem2=new FragmentItem2(BluetoothActivity.this);
                        getFragmentManager().beginTransaction().replace(R.id.main_content, Fitem2)
                                .commit();
                        flag = 2;
                        break;
                    case R.id.it3:
                        Fitem3 = new FragmentItem3(BluetoothActivity.this);
                        getFragmentManager().beginTransaction().replace(R.id.main_content, Fitem3)
                                .commit();
                        flag = 3;
                        break;
                    case R.id.it4:
                        Fitem4 = new baidumapFragment(BluetoothActivity.this);
                        getFragmentManager().beginTransaction().replace(R.id.main_content, Fitem4)
                                .commit();
                        flag = 4;
                        break;
                    default:
                        break;
                }
            }
        });
    }
    //数据库测试
    private void dbInit() {
        mgr = new DBManager(this);
        add("sun", 18, "nice");
    }

    //发送数据
    public void sendMessageHandle(String msg)
    {
        if (socket == null)
        {
            Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();//如果连接上了，那么获取输出流...
            os.write(msg.getBytes());//获取所有的字节然后往外发送...
        } catch (IOException e) {
            e.printStackTrace();
        }

        msgList.setMsgList(msg);
        //msgList.add(msg); //将msg放置到listview中
        //mAdapter.notifyDataSetChanged();  //重新绘制ListView
        //mListView.setSelection(msgList.size() - 1);//设置list保存的信息的位置...说白了该条信息始终在上一条信息的下方...
    }
    public void sendMessageHandle(byte[] b2) {
        if (socket == null) {
            Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(b2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //开启客户端
    class clientThread extends Thread {
        @Override
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                //连接
                Message msg2 = new Message();
                msg2.obj = "请稍候，正在连接服务器:"+BluetoothMsg.BlueToothAddress;
                msg2.what = 0;
                //LinkDetectedHandler.sendMessage(msg2);

                socket.connect();

                Message msg = new Message();
                msg.obj = "已经连接上服务端！可以发送信息。";
                msg.what = 0;
                //LinkDetectedHandler.sendMessage(msg);
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            }
            catch (IOException e)
            {
                Log.e("connect", "", e);
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = 0;
                LinkDetectedHandler.sendMessage(msg);
            }
        }
    };
    // 停止客户端连接
    private void shutdownClient() {
        new Thread() {
            @Override
            public void run() {
                if(clientConnectThread!=null)
                {
                    clientConnectThread.interrupt();
                    clientConnectThread= null;
                }
                if(mreadThread != null)
                {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            };
        }.start();
    }
    private Handler LinkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
            ////这步多了一个判断..判断的是ListView保存的数据是我们要传输给服务端的数据，还是那些我们定义好的提示数据...
            if(msg.what==1)
            {
                msgList.setMsgList((String)msg.obj);
                //if(flag == 1)  Fitem1.dataCallBack();
                Fitem2.setData();
            }
            else
            {
                msgList.setMsgList((String)msg.obj);
                //if(flag == 1) Fitem1.dataCallBack();
                Fitem2.setData();
            }
            //mAdapter.notifyDataSetChanged();
            //mListView.setSelection(msgList.size() - 1);
        }
    };

    //// 通过socket获取InputStream流.
    private class readThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[2048];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();//获取服务器发过来的所有字节...
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {//读取过程，将数据信息保存在ListView中...
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
                       // Toast.makeText(mContext, bytes, Toast.LENGTH_SHORT).show();
                        byte[] buf_data = new byte[bytes];
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }

                        String s = new String(buf_data);
                        //s.concat(s);
                        //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                        //Log.d("接收的数据",s);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = 1; //这里的meg.what=1...表示的是服务器发送过来的数据信息..
                        LinkDetectedHandler.sendMessage(msg);


                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    //开启服务器
    private class ServerThread extends Thread {
        @Override
        public void run() {

            try {
                    /* 创建一个蓝牙服务器
                     * 参数分别：服务器名称、UUID   */
                mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                Log.d("server", "wait cilent connect...");

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = 0;
                LinkDetectedHandler.sendMessage(msg);//调用线程，显示msg信息...

                    /* 接受客户端的连接请求 */
                socket = mserverSocket.accept();// 通过socket连接服务器，正式形成连接...这是一个阻塞过程，直到连接建立或者连接失效...
                Log.d("server", "accept success !");//如果实现了连接，那么服务端和客户端就共享一个RFFCOMM信道...

                Message msg2 = new Message();
                String info = "客户端已经连接上！可以发送信息。";
                msg2.obj = info;
                msg.what = 0;
                LinkDetectedHandler.sendMessage(msg2);//调用线程，显示msg信息...
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    /* 停止服务器 */
    private void shutdownServer() {
        new Thread() {
            @Override
            public void run() {
                if(startServerThread != null)
                {
                    startServerThread.interrupt();
                    startServerThread = null;
                }
                if(mreadThread != null)
                {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                try {
                    if(socket != null)
                    {
                        socket.close();
                        socket = null;
                    }
                    if (mserverSocket != null)
                    {
                        mserverSocket.close();/* 关闭服务器 */
                        mserverSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("server", "mserverSocket.close()", e);
                }
            };
        }.start();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void blueInit(){
        BluetoothMsg.serviceOrCilent=BluetoothMsg.ServerOrCilent.CILENT;

        if(BluetoothMsg.isOpen)
        {
            Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(BluetoothMsg.serviceOrCilent==BluetoothMsg.ServerOrCilent.CILENT)
        {
            String address = BluetoothMsg.BlueToothAddress;
            if(!address.equals("null"))
            {
                device = mBluetoothAdapter.getRemoteDevice(address);//通过Mac地址去尝试连接一个设备
                clientConnectThread = new clientThread();
                clientConnectThread.start();
                BluetoothMsg.isOpen = true;
            }
            else
            {
                Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
            }
        }
        else if(BluetoothMsg.serviceOrCilent==BluetoothMsg.ServerOrCilent.SERVICE)
        {
            startServerThread = new ServerThread();
            startServerThread.start();
            BluetoothMsg.isOpen = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT)
        {
            shutdownClient();
        }
        else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE)
        {
            shutdownServer();
        }
        BluetoothMsg.isOpen = false;
        BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.NONE;
    }

    public void add(String name,int age,String info) {
        boolean state=true;
        Person person=new Person(name, age, info);
        ArrayList<Person> persons = new ArrayList<Person>();
        List<Person> personsQuery = mgr.query();
        for (Person person1:personsQuery)
        {
            if(person.name.equals(person1.name))
            {
                state=false;
            }
        }
        if(state==true)
        {
            persons.add(person);
            mgr.add(persons);
        }
        else
        {
            mgr.updatePerson(person);
        }
    }

}
