package com.bluetooth;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Intent.getIntent;
@SuppressLint("ValidFragment")
public class FragmentItem1 extends Fragment {
    Context mContext = null;
    private ListView mListView;
    private EditText editMsgView;
    private Button sendButton;
    private ArrayAdapter<String> mAdapter;
    private List<String> msgList;  //用于存储消息列表
    private myApplication msgList1;
    //private List<String> test;
    private BluetoothSocket socket = null;

    public FragmentItem1() {

    }

    public FragmentItem1(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item1, null);
        mListView = (ListView) view.findViewById(R.id.list);

        msgList1 = ((myApplication)getActivity().getApplicationContext());
        msgList = msgList1.getMsgList();

        mAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, msgList);
        mListView.setAdapter(mAdapter);  //给ListView设置适配器
        mListView.setFastScrollEnabled(true);   //设置快速滚动
        editMsgView= (EditText)view.findViewById(R.id.MessageText);
        editMsgView.clearFocus();

        sendButton= (Button)view.findViewById(R.id.btn_msg_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String msgText =editMsgView.getText().toString();
                if (msgText.length()>0) {
                    ((BluetoothActivity)mContext).sendMessageHandle(msgText);
                    //msgList.add(msgText);
                    editMsgView.setText("");
                    editMsgView.clearFocus();
                    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);//发送完后将输入法隐藏起来
                    //boolean isOpen=imm.isActive();//判断输入法是否打开
                }else
                    Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void dataCallBack(){
        msgList1 = ((myApplication)getActivity().getApplicationContext());
        msgList = msgList1.getMsgList();

        mAdapter.notifyDataSetChanged();  //重新绘制ListView
        mListView.setSelection(msgList.size() - 1);//设置list保存的信息的位置...说白了该条信息始终在上一条信息的下方..
        /*mAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, msgList);
        mListView.setAdapter(mAdapter);  //给ListView设置适配器
        mListView.setFastScrollEnabled(true);   //设置快速滚动*/

    }


}
