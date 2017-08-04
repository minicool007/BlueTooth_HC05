package com.bluetooth;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
@SuppressLint("ValidFragment")
public class cameraFragment extends Fragment {

    Context mContext = null;
    private Button playButton;

    public cameraFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.camera_item, null);
        playButton = view.findViewById(R.id.btn_play);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


            }
        });
        return view;
    }
}
