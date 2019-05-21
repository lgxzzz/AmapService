package com.map.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.map.service.adapter.PoiSearchAdapter;
import com.map.service.amap.api.GeoSearchMgr;
import com.map.service.amap.api.LocationMgr;
import com.map.service.amap.api.PoiSearchMgr;
import com.map.service.bean.PoiSearchEntity;
import com.map.service.overlay.DrivingRouteOverlay;
import com.map.service.overlay.WalkRouteOverlay;
import com.map.service.util.AMapUtil;
import com.map.service.util.ToastUtil;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment  implements AMap.OnMapClickListener,RouteSearch.OnRouteSearchListener,
       AMap.OnCameraChangeListener, Animation.AnimationListener, GeocodeSearch.OnGeocodeSearchListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout mGeoSearchLayout;
    private LinearLayout mPoiSearchLayout;
    private RelativeLayout mLayerLayout;

    private EditText mGeoStartEdit;
    private EditText mGeoEndEdit;

    private Button mRouteSearchBtn;
    private Button mRouteCancelBtn;

    private CheckBox mRouteDriveCheckBox;
    private CheckBox mRouteWalkCheckBox;
    private CheckBox mRouteBusCheckBox;

    private MapView mMapView = null;
    private EditText mSearchEdit;
    private Button mDeleteBtn;
    private Button mCancelBtn;

    private Button mLayerStandard;
    private Button mLayerNight;
    private Button mLayerSatelite;
    private Button mLayerRoute;
    private Button mGeoSearchBtn;
    private Button mReturnBtn;
    private Button mRouteNaviBtn;

    private ListView mTextPoiListView;
    private ListView mMovePoiListView;

    private PoiSearchAdapter mMovePoiAapter;
    private PoiSearchAdapter mTextPoiAapter;

    private PoiSearchMgr mPoiSearchMgr;
    private PoiSearchMgr mTextSearchMgr;
    private LocationMgr mLocationMgr;

    private Double mLongitude;
    private Double mLatitude;
    private String cityCode;
    private String mKeyWord="美食";

    private Animation animationMarker;
    private LatLng mFinalChoosePosition; //最终选择的点
    private Marker locationMarker; // 选择的点
    private AMap mAMap;

    private boolean isFirstLoadList = true;
    private boolean isHandDrag = true;
    private boolean isSearchText = false;
    private boolean isTraffic = false;

    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;

    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;

    private int mCurrentRouteType = ROUTE_TYPE_DRIVE;

    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private LatLonPoint mStartPoint_bus = new LatLonPoint(40.818311, 111.670801);//起点，111.670801,40.818311
    private LatLonPoint mEndPoint_bus = new LatLonPoint(44.433942, 125.184449);//终点，

    private GeoSearchMgr startGeoSearchMgr;
    private GeoSearchMgr endGeoSearchMgr;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragemnt_map, container, false);
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mLocationMgr  = new LocationMgr(getContext());

        mPoiSearchMgr = new PoiSearchMgr(getContext());

        mTextSearchMgr = new PoiSearchMgr(getContext());

        mSearchEdit = (EditText) view.findViewById(R.id.search_edit);
        mDeleteBtn = (Button) view.findViewById(R.id.search_back);
        mCancelBtn = (Button) view.findViewById(R.id.search_cancel);

        mLayerStandard = (Button) view.findViewById(R.id.btn_standar);
        mLayerNight = (Button) view.findViewById(R.id.btn_night);
        mLayerSatelite = (Button) view.findViewById(R.id.btn_satelite);
        mLayerRoute = (Button) view.findViewById(R.id.btn_route);
        mReturnBtn = (Button) view.findViewById(R.id.btn_return);
        mMovePoiListView = (ListView) view.findViewById(R.id.poi_list);
        mTextPoiListView = (ListView) view.findViewById(R.id.search_list);

        mGeoSearchLayout = (LinearLayout)view.findViewById(R.id.geocode_layout);
        mPoiSearchLayout = (LinearLayout)view.findViewById(R.id.poisearch_layout);
        mLayerLayout = (RelativeLayout) view.findViewById(R.id.layer_layout);

        mRouteSearch = new RouteSearch(getContext());
        mRouteSearch.setRouteSearchListener(this);

        mGeoStartEdit = (EditText) view.findViewById(R.id.geo_start_edit);
        mGeoEndEdit = (EditText) view.findViewById(R.id.geo_end_edit);

        mRouteDriveCheckBox = (CheckBox) view.findViewById(R.id.route_car);
        mRouteWalkCheckBox = (CheckBox) view.findViewById(R.id.route_walk);
        mRouteBusCheckBox = (CheckBox) view.findViewById(R.id.route_bus);

        mGeoSearchBtn = (Button)view.findViewById(R.id.geo_search_btn);
        mRouteSearchBtn = (Button)view.findViewById(R.id.route_search_btn);
        mRouteCancelBtn = (Button)view.findViewById(R.id.route_search_cancel_btn);
        mRouteNaviBtn = (Button)view.findViewById(R.id.route_navi_btn);

        startGeoSearchMgr = new GeoSearchMgr(getContext());
        endGeoSearchMgr = new GeoSearchMgr(getContext());

        startGeoSearchMgr.setGeoSearchListener(new GeoSearchMgr.GeoSearchListener() {
            @Override
            public void onSuccess(GeocodeAddress address) {
                mStartPoint = address.getLatLonPoint();
            }

            @Override
            public void onFail(String error) {
                ToastUtil.show(getContext(),error);
            }
        });

        endGeoSearchMgr.setGeoSearchListener(new GeoSearchMgr.GeoSearchListener() {
            @Override
            public void onSuccess(GeocodeAddress address) {
                mEndPoint = address.getLatLonPoint();
            }

            @Override
            public void onFail(String error) {
                ToastUtil.show(getContext(),error);
            }
        });

        initView();
        return view;
    }

    public void initView(){


        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0)
                {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                    mTextSearchMgr.doSearchQuery(charSequence.toString(),mLatitude,mLongitude);
                }else {
                    mDeleteBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdit.setText("");
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextPoiListView.setVisibility(View.GONE);
                mCancelBtn.setVisibility(View.GONE);
                mSearchEdit.setText("");
            }
        });

        mTextSearchMgr.setPoiListener(new PoiSearchMgr.PoiSearchListener() {
            @Override
            public void onSuccess(List<PoiItem> poiItems) {
                mTextPoiAapter = new PoiSearchAdapter(getContext(),poiItems);
                mTextPoiListView.setAdapter(mTextPoiAapter);
                mTextPoiListView.setVisibility(View.VISIBLE);
                mTextPoiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        isSearchText = true;
                        PoiSearchEntity poiSearchEntity = (PoiSearchEntity)mTextPoiAapter.getItem(i);
                        mFinalChoosePosition =  convertToLatLng(poiSearchEntity.getPoiItem().getLatLonPoint());
                        Log.d("lgx","点击后的最终经纬度：  纬度" + mFinalChoosePosition.latitude + " 经度 " + mFinalChoosePosition.longitude);
                        // 只要地图发生改变，就会调用 onCameraChangeFinish
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), 20));

                        mTextPoiListView.setVisibility(View.GONE);
                        mCancelBtn.setVisibility(View.GONE);

                        hideInput();
                    }
                });
                mCancelBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            }
        });

        mPoiSearchMgr.setPoiListener(new PoiSearchMgr.PoiSearchListener() {

            @Override
            public void onSuccess(List<PoiItem> poiItems) {
                mMovePoiAapter = new PoiSearchAdapter(getContext(),poiItems);
                mMovePoiListView.setAdapter(mMovePoiAapter);
                mMovePoiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        PoiSearchEntity poiSearchEntity = (PoiSearchEntity)mMovePoiAapter.getItem(i);
                        mFinalChoosePosition =  convertToLatLng(poiSearchEntity.getPoiItem().getLatLonPoint());
                        Log.d("lgx","点击后的最终经纬度：  纬度" + mFinalChoosePosition.latitude + " 经度 " + mFinalChoosePosition.longitude);
                        // 只要地图发生改变，就会调用 onCameraChangeFinish
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), 15));
                    }
                });
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            }
        });

        mLayerStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置标准模式
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });

        mLayerNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置夜间模式
                mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
            }
        });

        mLayerSatelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置卫星模式
                mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            }
        });

        mLayerRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 开启关闭路况模式
                mAMap.setTrafficEnabled(isTraffic);
                isTraffic = !isTraffic;
            }
        });

        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 返回定位点
               getPosition();
            }
        });

        mGeoSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayerLayout.setVisibility(View.GONE);
                mGeoSearchLayout.setVisibility(View.VISIBLE);
                mPoiSearchLayout.setVisibility(View.GONE);
                mMovePoiListView.setVisibility(View.GONE);
            }
        });

        mRouteCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayerLayout.setVisibility(View.VISIBLE);
                mGeoSearchLayout.setVisibility(View.GONE);
                mPoiSearchLayout.setVisibility(View.VISIBLE);
                mMovePoiListView.setVisibility(View.VISIBLE);
                moveMapCamera();
            }
        });

        mRouteDriveCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCurrentRouteType = ROUTE_TYPE_DRIVE;
                    mRouteBusCheckBox.setChecked(false);
                    mRouteWalkCheckBox.setChecked(false);
                }
            }
        });
        mRouteWalkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCurrentRouteType = ROUTE_TYPE_WALK;
                    mRouteBusCheckBox.setChecked(false);
                    mRouteDriveCheckBox.setChecked(false);
                }
            }
        });
        mRouteBusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCurrentRouteType = ROUTE_TYPE_BUS;
                    mRouteDriveCheckBox.setChecked(false);
                    mRouteWalkCheckBox.setChecked(false);
                }
            }
        });

        mGeoStartEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                String key = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                delayRun = new GeoSearchRunnable();
                delayRun.setGeoSearchMgr(startGeoSearchMgr,key,cityCode);
                handler.postDelayed(delayRun, 1200);
            }
        });

        mGeoEndEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                String key = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                delayRun = new GeoSearchRunnable();
                delayRun.setGeoSearchMgr(endGeoSearchMgr,key,cityCode);
                handler.postDelayed(delayRun, 1200);

            }
        });

        mRouteSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAMap.setOnCameraChangeListener(null);


                searchRouteResult(mCurrentRouteType);
            }
        });

        mRouteNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Poi start = new Poi("", new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude()), "");
                Poi end = new Poi("", new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()), "");
                AmapNaviPage.getInstance().showRouteActivity(getContext(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER), null);
            }
        });

        getPosition();




    };

    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private GeoSearchRunnable delayRun = new GeoSearchRunnable();

    class GeoSearchRunnable implements Runnable{
        GeoSearchMgr geoSearchMgr;
        String key;
        String city;
        public void setGeoSearchMgr(GeoSearchMgr geoSearchMgr,String key,String city){
            this.geoSearchMgr = geoSearchMgr;
            this.key = key;
            this.city = city;
        }

        public GeoSearchRunnable(){

        }

        @Override
        public void run() {
            geoSearchMgr.getGeoInfo(key,city);
        }
    }

    public void getPosition(){
        //获取定位信息并且查询当前的POI点周边
        mLocationMgr.getLonLat(getContext(), new LocationMgr.LonLatListener() {
            @Override
            public void getLonLat(AMapLocation aMapLocation) {
                mLongitude = aMapLocation.getLongitude();
                mLatitude = aMapLocation.getLatitude();
                cityCode = aMapLocation.getCityCode();
                mFinalChoosePosition = new LatLng(mLatitude,mLongitude);
                moveMapCamera();
            }
        });
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        View v =  getActivity().getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    public void doSearchQueryWithKeyWord(String keyword){
        mPoiSearchMgr.doSearchQuery(keyword,mFinalChoosePosition.latitude,mFinalChoosePosition.longitude);
    }

    //移动地图
    public void moveMapCamera(){
        mAMap = mMapView.getMap();
        mAMap.setOnMapClickListener(this);
        mAMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
//            animationMarker.setAnimationListener(this);
        addmark(mLatitude, mLongitude);
        mFinalChoosePosition = locationMarker.getPosition();

        //设置Marker的点击事件
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                searchRouteResult(RouteSearch.DrivingDefault);

                return true;
            }
        });
        // 只要地图发生改变，就会调用 onCameraChangeFinish ，不是说非要手动拖动屏幕才会调用该方法
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mFinalChoosePosition, 15));


    }

    //增加图标
    private void addmark(double latitude, double longitude) {
        mAMap.clear();
        locationMarker = mAMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.navi_icon_1)))
                .draggable(true));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        mAMap.clear();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    if(drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getContext(), mAMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();

//                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    DriveRouteDetailActivity.class);
//                            intent.putExtra("drive_path", drivePath);
//                            intent.putExtra("drive_result",
//                                    mDriveRouteResult);
//                            startActivity(intent);
//                        }
//                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(getContext(), R.string.no_result);
                }

            } else {
                ToastUtil.show(getContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        mAMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    if(walkPath == null) {
                        return;
                    }
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            getContext(), mAMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
//                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+ AMapUtil.getFriendlyLength(dis)+")";
//                    mRotueTimeDes.setText(des);
//                    mRouteDetailDes.setVisibility(View.GONE);
//                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    WalkRouteDetailActivity.class);
//                            intent.putExtra("walk_path", walkPath);
//                            intent.putExtra("walk_result",
//                                    mWalkRouteResult);
//                            startActivity(intent);
//                        }
//                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(getContext(), R.string.no_result);
                }

            } else {
                ToastUtil.show(getContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getContext(), errorCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        mFinalChoosePosition = cameraPosition.target;
        Log.e("lgx","拖动地图 Finish changeCenterMarker 经度" + mFinalChoosePosition.longitude + "   纬度：" + mFinalChoosePosition.latitude);
        if (isHandDrag||isFirstLoadList) {//手动去拖动地图
            doSearchQueryWithKeyWord(mKeyWord);
            addmark(mFinalChoosePosition.latitude,mFinalChoosePosition.longitude);
        }
        isHandDrag = true;
        isFirstLoadList = false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationEnd() {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType) {
        int mode=-1;
        if (mStartPoint == null) {
            ToastUtil.show(getContext(), "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(getContext(), "终点未设置");
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint
                , mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
            mode = RouteSearch.BusDefault;
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    "", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            mode = RouteSearch.DrivingDefault;
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            mode =  RouteSearch.WalkDefault;
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude()),new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude())),20));
    }
}
