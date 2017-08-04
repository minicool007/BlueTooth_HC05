package com.bluetooth;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
@SuppressLint("ValidFragment")
public class baidumapFragment extends Fragment {

    Context mContext = null;
    private MapView mapView;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    //覆盖物相关
    private BitmapDescriptor mMarker;
    private BitmapDescriptor mMarker_start;
    private BitmapDescriptor mMarker_end;
    private RelativeLayout mMarkerLy;
    private Boolean targetFlag=false;
    private Boolean startFlag=false;
    public static LatLng targetLatLng=null;
    public static LatLng startLatLng=null;
    InfoWindow infoWindow;
    //定位相关
    public LocationClient mLocationClient;
    public BDLocationListener mLocationListener;
    public boolean isfirstIn=true;
    private double mLatitude;
    private double mLongtitude;
    //自定义定位图标
    private BitmapDescriptor mIconLocation;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;

    //按键单击事件
    private Button locate,start,target;
    private View view;

    public baidumapFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SDKInitializer.initialize(getActivity().getApplication());
        View view=inflater.inflate(R.layout.baidu_map, null);

        mMapView = (MapView)view.findViewById(R.id.map_view);
        locate=(Button) view.findViewById(R.id.locate);
        start=(Button) view.findViewById(R.id.start);
        target=(Button) view.findViewById(R.id.target);

        mBaiduMap=mMapView.getMap();//BaiduMap管理具体的某一个MapView： 旋转，移动，缩放，事件。。。
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(15.0f);//设置缩放级别，默认级别为12
        mBaiduMap.setMapStatus(msu);
        //初始化图标
        //mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        mMapView.setBackgroundColor(Color.WHITE);

        initView();
        initLocation();
        initMarker();
        initMapClick();

        return view;
    }
    private void initView()
    {
        View.OnClickListener listener=new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                switch (v.getId())
                {
                    case R.id.locate:
                        centerToMyLocation();//定位当前位置
                        break;
                    case R.id.start:
                        startFlag=true;
                        targetFlag=false;
                        break;
                    case R.id.target:
                        startFlag=false;
                        targetFlag=true;
                        break;
                }
            }
        };
        locate.setOnClickListener(listener);
        target.setOnClickListener(listener);
        start.setOnClickListener(listener);
    }

    private void initLocation()
    {
        mLocationClient=new LocationClient(mContext);//LocationClient类必须在主线程中声明。需要Context类型的参数
        mLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);//注册监听函数

    }
    private void initMarker()
    {
        mMarker=BitmapDescriptorFactory.fromResource(R.drawable.marker);
        mMarker_start=BitmapDescriptorFactory.fromResource(R.drawable.start);
        mMarker_end=BitmapDescriptorFactory.fromResource(R.drawable.stop);
        //mMarkerLy=(RelativeLayout) findViewById(R.id.markerlayout);
    }
    private void initMapClick()
    {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                TextView tv=new TextView(mContext);
                //tv.setBackgroundResource(R.drawable.tips);
                tv.setPadding(30, 20, 30, 50);
                tv.setTextColor(Color.parseColor("#ff0000"));

                final LatLng latLng=marker.getPosition();
                if (latLng==startLatLng)
                {
                    tv.setText("这是起点");
                }
                if (latLng==targetLatLng)
                {
                    tv.setText("这是终点");
                }
//				tv.setText("经度："+String.valueOf(latLng.longitude).toString()+"\n"+
//							"纬度："+String.valueOf(latLng.latitude).toString());
                Point p=mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y-=47;
                LatLng ll=mBaiduMap.getProjection().fromScreenLocation(p);

                infoWindow=new InfoWindow(tv, ll, -17);

                mBaiduMap.showInfoWindow(infoWindow);
                //mMarkerLy.setVisibility(View.VISIBLE);
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener()
        {
            @Override
            public boolean onMapPoiClick(MapPoi arg0)
            {
                // TODO Auto-generated method stub
                Marker marker;
                OverlayOptions options;
                InfoWindow infoWindow1;
                TextView tv1=new TextView(mContext);
                //tv1.setBackgroundResource(R.drawable.tips);
                tv1.setPadding(30, 20, 30, 50);
                if (startFlag==true)
                {
                    tv1.setText("这是起点");
                }
                if(targetFlag==true)
                {
                    tv1.setText("这是终点");
                }
                //tv1.setText(arg0.getName());
                tv1.setTextColor(Color.parseColor("#ff0000"));

                LatLng latLng1=arg0.getPosition();
                Point p1=mBaiduMap.getProjection().toScreenLocation(latLng1);
                p1.y-=47;
                LatLng ll1=mBaiduMap.getProjection().fromScreenLocation(p1);

                infoWindow1=new InfoWindow(tv1, ll1, -17);
                //图标
                if ((targetFlag==false)&&(startFlag==false))
                {
                    mBaiduMap.hideInfoWindow();
                }
                if (targetFlag==true)
                {
                    mBaiduMap.showInfoWindow(infoWindow1);
                    if(targetLatLng!=null)
                    {
                        mBaiduMap.clear();
                        if (startLatLng!=null)
                        {
                            options=new MarkerOptions().position(startLatLng).icon(mMarker_start).zIndex(1);
                            marker=(Marker) mBaiduMap.addOverlay(options);
                        }
                    }
                    targetLatLng=arg0.getPosition();
                    options=new MarkerOptions().position(targetLatLng).icon(mMarker_end).zIndex(1);
                    marker=(Marker) mBaiduMap.addOverlay(options);
                    targetFlag=false;
                }
                if (startFlag==true)
                {
                    mBaiduMap.showInfoWindow(infoWindow1);
                    if(startLatLng!=null)
                    {
                        mBaiduMap.clear();
                        if (targetLatLng!=null)
                        {
                            options=new MarkerOptions().position(targetLatLng).icon(mMarker_end).zIndex(1);
                            marker=(Marker) mBaiduMap.addOverlay(options);
                        }
                    }
                    startLatLng=arg0.getPosition();

                    options=new MarkerOptions().position(startLatLng).icon(mMarker_start).zIndex(1);
                    marker=(Marker) mBaiduMap.addOverlay(options);
                    startFlag=false;
                }
                return true;
            }

            @Override
            public void onMapClick(LatLng arg0)
            {
                // TODO Auto-generated method stub
                Marker marker;
                OverlayOptions options;
                InfoWindow infoWindow1;
                TextView tv1=new TextView(mContext);
                //tv1.setBackgroundResource(R.drawable.tips);
                tv1.setPadding(30, 20, 30, 50);
                if (startFlag==true)
                {
                    tv1.setText("这是起点");
                }
                if(targetFlag==true)
                {
                    tv1.setText("这是终点");
                }
                tv1.setTextColor(Color.parseColor("#ff0000"));

                Point p1=mBaiduMap.getProjection().toScreenLocation(arg0);
                p1.y-=47;
                LatLng ll1=mBaiduMap.getProjection().fromScreenLocation(p1);
                infoWindow1=new InfoWindow(tv1, ll1, -17);
                if (targetFlag==true)
                {
                    mBaiduMap.showInfoWindow(infoWindow1);
                    if(targetLatLng!=null)
                    {
                        mBaiduMap.clear();
                        if (startLatLng!=null)
                        {
                            options=new MarkerOptions().position(startLatLng).icon(mMarker_start).zIndex(1);
                            marker=(Marker) mBaiduMap.addOverlay(options);
                        }
                    }
                    targetLatLng=arg0;
                    options=new MarkerOptions().position(targetLatLng).icon(mMarker_end).zIndex(1);
                    marker=(Marker) mBaiduMap.addOverlay(options);
                    targetFlag=false;
                }
                else if (startFlag==true)
                {
                    mBaiduMap.showInfoWindow(infoWindow1);
                    if(startLatLng!=null)
                    {
                        mBaiduMap.clear();
                        if (targetLatLng!=null)
                        {
                            options=new MarkerOptions().position(targetLatLng).icon(mMarker_end).zIndex(1);
                            marker=(Marker) mBaiduMap.addOverlay(options);
                        }
                    }
                    startLatLng=arg0;
                    options=new MarkerOptions().position(startLatLng).icon(mMarker_start).zIndex(1);
                    marker=(Marker) mBaiduMap.addOverlay(options);
                    startFlag=false;
                }
                else
                {
                    mBaiduMap.hideInfoWindow();
                }
//					mMarkerLy.setVisibility(View.GONE);
            }
        });
    }

    /**
     * BDLocationListener接口实现以下方法：
       接收异步返回的定位结果，参数是BDLocation类型参数。。
     */
    public class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            MyLocationData data=new MyLocationData.Builder()//
                    .direction(mCurrentX)
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);
            //设置自定义图标
            MyLocationConfiguration configuration=new MyLocationConfiguration(mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(configuration);
            //更新经纬度
            mLatitude=location.getLatitude();
            mLongtitude=location.getLongitude();

            if (isfirstIn)
            {
                LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isfirstIn=false;
            }
        }
    }
    private void centerToMyLocation()
    {
        LatLng latLng=new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    public void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (mLocationClient.isStarted())
        {
        }
        else
        {
            mLocationClient.start();
        }
    }

    @Override
    public void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();

        mMapView.onDestroy();
        mMapView=null;
    }
    public void onPause()
    {
        // TODO Auto-generated method stub
        mMapView.setVisibility(View.GONE);
        mMapView.onPause();
        /*if (startLatLng!=null)
        {
            ((BluetoothActivity)getActivity()).setStartLatLng(startLatLng);
        }
        if (targetLatLng!=null)
        {
            ((BluetoothActivity)getActivity()).setTargetLatLng(targetLatLng);
        }*/
        super.onPause();

    }
    public void onResume()
    {
        // TODO Auto-generated method stub
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
    }
}
