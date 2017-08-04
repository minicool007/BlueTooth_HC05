package com.bluetooth;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
@SuppressLint("ValidFragment")
public class FragmentItem4 extends Fragment {

    Context mContext=null;

    public FragmentItem4(Context context)
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
        View view=inflater.inflate(R.layout.item4, null);

        return view;
    }


}
