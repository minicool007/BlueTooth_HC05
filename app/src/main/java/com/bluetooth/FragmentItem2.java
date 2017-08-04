package com.bluetooth;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@SuppressLint("ValidFragment")
public class FragmentItem2 extends Fragment{

    Context mContext=null;
    ListView lv;
    View view;
    String[] title={"飞行高度：","横滚角：","俯仰角：",
            "偏航角：","经度：","纬度：","飞行参数1：","飞行参数2：","飞行参数3：","飞行参数4："};
    String[] text={"0","0","0","0","0","0","0","0","0","0"};
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
    SimpleAdapter mSimpleAdapter;
    private List<String> msgList;  //用于存储消息列表
    private myApplication msgList1;

    public FragmentItem2(Context context)
    {
        mContext = context;// 获取上下文对象
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //获取界面元素
        view=inflater.inflate(R.layout.item2, null);
        lv=(ListView)view.findViewById(R.id.lv);

        //数据显示部分
        for(int i=0;i<10;i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.logo);
            map.put("ItemText", text[i]);
            map.put("ItemTitle", title[i]);
            listItem.add(map);
        }
        mSimpleAdapter=new SimpleAdapter(mContext,listItem, R.layout.rows,
                new String[] {"ItemImage","ItemTitle", "ItemText"},
                new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText});
        lv.setAdapter(mSimpleAdapter);
        return view;
    }

    public void setData()
    {
        //串口接收的数据显示在手机界面上
        msgList1 = ((myApplication)getActivity().getApplicationContext());
        msgList = msgList1.getMsgList();
        //this.high = high;
        HashMap<String, Object> map = new HashMap<String, Object>();
        //Person person=mgr.findName("Height");
        map.put("ItemImage", R.drawable.logo);
        map.put("ItemText",0);
        //map.put("ItemTitle", "高度值：");
        listItem.add(map);
        mSimpleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
