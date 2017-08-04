package com.bluetooth;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.BluetoothActivity;
import com.bluetooth.R;

public class PidController
{
	private Context mContext=null;
	private View mView;
	private SeekBar seek1,seek2,seek3,seek4,seek5,seek6,seek7,seek8,seek9,seek_hight;
	private TextView text1,text2,text3,text4,text5,text6,text7,text8,text9,text_hight;
	private Button btnlock,btnunlock,btnrecify,btnfly,btnunfly,btnreset;
	public byte[] b=new byte[4];
	public int[] i=new int[30];
	public String[] text=new String[30];
	public int[] PID1=new int[18];
	public int[] PID2=new int[18];
	public PidController(Context context, View view)
	{
		mContext = context;// 获取上下文对象
		mView=view;
	}
	public void initView()
	{
		// TODO Auto-generated method stub
		seek1=(SeekBar) mView.findViewById(R.id.seekBar1);
		seek2=(SeekBar) mView.findViewById(R.id.seekBar2);
		seek3=(SeekBar) mView.findViewById(R.id.seekBar3);
		seek4=(SeekBar) mView.findViewById(R.id.seekBar4);
		seek5=(SeekBar) mView.findViewById(R.id.seekBar5);
		seek6=(SeekBar) mView.findViewById(R.id.seekBar6);
		seek7=(SeekBar) mView.findViewById(R.id.seekBar7);
		seek8=(SeekBar) mView.findViewById(R.id.seekBar8);
		seek9=(SeekBar) mView.findViewById(R.id.seekBar9);
		seek_hight=(SeekBar) mView.findViewById(R.id.seekBar_high);

		text[1]="P1:";
		text[2]="I1:";
		text[3]="D1:";
		text[4]="P2:";
		text[5]="I2:";
		text[6]="D2:";
		text[7]="P3:";
		text[8]="I3:";
		text[9]="D3:";
		text[10]="高度设定:";

		text1=(TextView) mView.findViewById(R.id.text1);
		text2=(TextView) mView.findViewById(R.id.text2);
		text3=(TextView) mView.findViewById(R.id.text3);
		text4=(TextView) mView.findViewById(R.id.text4);
		text5=(TextView) mView.findViewById(R.id.text5);
		text6=(TextView) mView.findViewById(R.id.text6);
		text7=(TextView) mView.findViewById(R.id.text7);
		text8=(TextView) mView.findViewById(R.id.text8);
		text9=(TextView) mView.findViewById(R.id.text9);
		text_hight=(TextView) mView.findViewById(R.id.hight);

		btnlock=(Button) mView.findViewById(R.id.lock);
		btnunlock=(Button) mView.findViewById(R.id.unlock);
		btnfly=(Button) mView.findViewById(R.id.fly);
		btnunfly=(Button) mView.findViewById(R.id.unfly);
		btnrecify=(Button) mView.findViewById(R.id.recify);
		btnreset=(Button) mView.findViewById(R.id.reset);

		seek1.setOnSeekBarChangeListener(seekListener);
		seek2.setOnSeekBarChangeListener(seekListener);
		seek3.setOnSeekBarChangeListener(seekListener);
		seek4.setOnSeekBarChangeListener(seekListener);
		seek5.setOnSeekBarChangeListener(seekListener);
		seek6.setOnSeekBarChangeListener(seekListener);
		seek7.setOnSeekBarChangeListener(seekListener);
		seek8.setOnSeekBarChangeListener(seekListener);
		seek9.setOnSeekBarChangeListener(seekListener);
		seek_hight.setOnSeekBarChangeListener(seekListener);

		btnlock.setOnClickListener(listener);
		btnunlock.setOnClickListener(listener);
		btnfly.setOnClickListener(listener);
		btnunfly.setOnClickListener(listener);
		btnrecify.setOnClickListener(listener);
		btnreset.setOnClickListener(listener);

	}
	public OnSeekBarChangeListener seekListener=new OnSeekBarChangeListener()
	{

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			// TODO Auto-generated method stub
			switch (seekBar.getId()) {
				case R.id.seekBar1:
					text1.setText(text[1]+String.valueOf(progress));
					PID1[1]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar2:
					text2.setText(text[2]+String.valueOf(progress));
					PID1[2]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar3:
					text3.setText(text[3]+String.valueOf(progress));
					PID1[3]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar4:
					text4.setText(text[4]+String.valueOf(progress));
					PID1[4]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar5:
					text5.setText(text[5]+String.valueOf(progress));
					PID1[5]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar6:
					text6.setText(text[6]+String.valueOf(progress));
					PID1[6]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar7:
					text7.setText(text[7]+String.valueOf(progress));
					PID1[7]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar8:
					text8.setText(text[8]+String.valueOf(progress));
					PID1[8]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar9:
					text9.setText(text[9]+String.valueOf(progress));
					PID1[9]=Integer.parseInt(String.valueOf(progress));
					sendPID1();
					break;
				case R.id.seekBar_high:
					text_hight.setText(text[10]+String.valueOf(progress));
					PID2[1]=Integer.parseInt(String.valueOf(progress));
					sendPID2();
					break;
			}
		}
	};

	public void sendPID1()
	{
		b[0]=-86;//AA
		b[1]=-81;//AF帧头
		b[2]=16;//功能字
		b[3]=18;//长度
		for (int m = 0; m < 9; m++)
		{
			b[5+2*m]=(byte)(PID1[m+1]);
			b[4+2*m]=0;
		}
		for (int i = 0; i < 22; i++)
		{
			b[22]+=b[i];//校验位
		}
		b[22]=(byte) (b[22]+(byte)512);
		((BluetoothActivity)mContext).sendMessageHandle(b);
	}
	public void sendPID2()
	{
		b[0]=-86;//AA
		b[1]=-81;//AF帧头
		b[2]=01;//起飞
		b[3]=80;//长度
		/*b[2]=17;//功能字
		b[3]=18;//长度
		b[4]=(byte)(PID2[1]/256);
		b[5]=(byte)(PID2[1]%256);
		for (int m = 1; m < 9; m++)
		{
			b[5+2*m]=(byte)(PID2[m+1]);
			b[4+2*m]=0;
		}
		for (int i = 0; i < 22; i++)
		{
			b[22]+=b[i];//校验位
		}
		b[22]=(byte) (b[22]+(byte)512);*/
		((BluetoothActivity)mContext).sendMessageHandle(b);
	}
	public void sendPID3() {
		b[0] = -86;//AA
		b[1] = -81;//AF帧头
		b[2] = 02;//停飞
		b[3] = 80;//长度
		((BluetoothActivity)mContext).sendMessageHandle(b);
	}

	public void sendPID4() {
		b[0] = -86;//AA
		b[1] = -81;//AF帧头
		b[2] = 03;//停飞
		b[3] = 80;//长度
		((BluetoothActivity)mContext).sendMessageHandle(b);
	}
	public void sendPID5() {
		b[0] = -86;//AA
		b[1] = -81;//AF帧头
		b[2] = 04;//停飞
		b[3] = 80;//长度
		((BluetoothActivity)mContext).sendMessageHandle(b);
	}

	public OnClickListener listener=new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
				case R.id.lock:
					PID2[2]=1;
					sendPID2();
					break;
				case R.id.unlock:
					PID2[2]=0;
					sendPID4();
					break;
				case R.id.recify:
					PID2[3]=1;
					sendPID5();
					break;
				case R.id.fly:
					PID2[4]=1;
					sendPID2();
					break;
				case R.id.unfly:
					PID2[4]=0;
					sendPID3();
					break;
				case R.id.reset:
					seek1.setProgress(0);
					seek2.setProgress(0);
					seek3.setProgress(0);
					seek4.setProgress(0);
					seek5.setProgress(0);
					seek6.setProgress(0);
					seek7.setProgress(0);
					seek8.setProgress(0);
					seek9.setProgress(0);
					seek_hight.setProgress(0);
					text1.setText("P1:0");
					text2.setText("I1:0");
					text3.setText("D1:0");
					text4.setText("P2:0");
					text5.setText("I2:0");
					text6.setText("D2:0");
					text7.setText("P3:0");
					text8.setText("I3:0");
					text9.setText("D3:0");
					text_hight.setText("高度设定:0");

					//pid清零
					for (int i = 0; i < 10; i++)
					{
						PID1[i]=0;
						PID2[i]=0;
					}
					sendPID1();
					sendPID2();
					break;
			}
		}
	};
}

