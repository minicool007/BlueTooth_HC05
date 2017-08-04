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
public class FragmentItem3 extends Fragment{

    private Context mContext = null;
    public PidController pidController;
    private View view;

    public FragmentItem3(Context context)
    {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pid, container, false);
        pidController=new PidController(mContext,view);
        pidController.initView();
        return view;
    }
}
