//public class ShareLocActivity extends CheckPermissionsActivity implements View.OnClickListener,
//        AMap.OnMapClickListener,
//        PoiSearch.OnPoiSearchListener, AMap.OnCameraChangeListener, Animation.AnimationListener, GeocodeSearch.OnGeocodeSearchListener {
//    private static final int OPEN_SEARCH = 0X0001;
//    private MapView mapview;
//    private AMap mAMap;
//    private PoiResult poiResult; // poi返回的结果
//    private int currentPage = 0;// 当前页面，从0开始计数
//    private PoiSearch.Query query;// Poi查询条件类
//    private LatLonPoint lp;//
//    private Marker locationMarker; // 选择的点
//    private PoiSearch poiSearch;
//    private List<PoiItem> poiItems;// poi数据
//    private RelativeLayout mPoiDetail;
//    private TextView mPoiName, mPoiAddress;
//    private String keyWord = "";
//    private String city;
//    private TextView mTvHint;
//    private RelativeLayout search_bar_layout;
//
//    private ImageView mIvCenter;
//    private Animation animationMarker;
//    private LatLng mFinalChoosePosition; //最终选择的点
//    private GeocodeSearch geocoderSearch;
//
//    private String addressName;
//    private RecyclerView mRvAddress;
//    private RvAddressSearchTextAdapter mRvAddressAdapter;
//    private ArrayList<AddressSearchTextEntity> mDatas = new ArrayList<>();
//    private AddressSearchTextEntity mAddressEntityFirst = null;
//    private TextView mTvSearch;
//    private boolean isHandDrag = true;
//    private boolean isFirstLoadList = true;
//    private boolean isBackFromSearchChoose = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_share_loc);
//
//        Intent intent = getIntent();
//        double lon = intent.getDoubleExtra("lon", 0);
//        double lat = intent.getDoubleExtra("lat", 0);
//        city = intent.getStringExtra("cityCode");
//        lp = new LatLonPoint(lat, lon);
//
//        mapview = (MapView) findViewById(R.id.mapView);
//        mIvCenter = (ImageView) findViewById(R.id.mIvCenter);
//
//        mapview.onCreate(savedInstanceState);
//        animationMarker = AnimationUtils.loadAnimation(this,
//                R.anim.bounce_interpolator);
//        mRvAddress = (RecyclerView) findViewById(R.id.mRvAddress);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(ShareLocActivity.this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRvAddress.setLayoutManager(layoutManager);
//        mRvAddressAdapter = new RvAddressSearchTextAdapter(ShareLocActivity.this, mDatas);
//
//        mRvAddress.setAdapter(mRvAddressAdapter);
//        mRvAddressAdapter.setOnItemClickLitener(new RvAddressSearchTextAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                mFinalChoosePosition = convertToLatLng(mDatas.get(position).latLonPoint);
//                for (int i = 0; i < mDatas.size(); i++) {
//                    mDatas.get(i).isChoose = false;
//                }
//                mDatas.get(position).isChoose = true;
//                L.d("点击后的最终经纬度：  纬度" + mFinalChoosePosition.latitude + " 经度 " + mFinalChoosePosition.longitude);
//                isHandDrag = false;
//                // 点击之后，我利用代码指定的方式改变了地图中心位置，所以也会调用 onCameraChangeFinish
//                // 只要地图发生改变，就会调用 onCameraChangeFinish ，不是说非要手动拖动屏幕才会调用该方法
//                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), 20));
//            }
//            @Override
//            public void onItemLongClick(View view, int position) {
//            }
//        });
//        init();
//    }
//
//    /**
//     * 初始化AMap对象
//     */
//    private void init() {
//        if (mAMap == null) {
//            mAMap = mapview.getMap();
//            mAMap.setOnMapClickListener(this);
//            mAMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
//
//            search_bar_layout = (RelativeLayout) findViewById(R.id.search_bar_layout);
//            search_bar_layout.setOnClickListener(this);
//            animationMarker.setAnimationListener(this);
//
//            locationMarker = mAMap.addMarker(new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory
//                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
//                    .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
//            mFinalChoosePosition = locationMarker.getPosition();
//        }
//        setup();
//        // 只要地图发生改变，就会调用 onCameraChangeFinish ，不是说非要手动拖动屏幕才会调用该方法
//        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 20));
//    }
//
//    private void setup() {
//        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
//        mPoiDetail.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        mPoiName = (TextView) findViewById(R.id.poi_name);
//        mPoiAddress = (TextView) findViewById(R.id.poi_address);
//        mTvHint = (TextView) findViewById(R.id.mTvHint);
//        mTvHint.setOnClickListener(this);
//
//        mTvSearch = (TextView) findViewById(R.id.mTvSearch);
//        mTvSearch.setOnClickListener(this);
//
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//        mIvCenter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToastUtil.show(ShareLocActivity.this, "当前选择的经度：" + mFinalChoosePosition.longitude + "  纬度：" + mFinalChoosePosition.latitude);
//            }
//        });
//    }
//
//    // 拖动地图
//    @Override
//    public void onCameraChange(CameraPosition cameraPosition) {
//        //L.d("拖动地图 onCameraChange ");
//    }
//    /**
//     * 拖动地图 结束回调
//     *
//     * @param cameraPosition 当地图位置发生变化，就重新查询数据（手动拖动或者代码改变地图位置都会调用）
//     */
//    @Override
//    public void onCameraChangeFinish(CameraPosition cameraPosition) {
//        mFinalChoosePosition = cameraPosition.target;
//        L.d("拖动地图 Finish changeCenterMarker 经度" + mFinalChoosePosition.longitude + "   纬度：" + mFinalChoosePosition.latitude);
//        mIvCenter.startAnimation(animationMarker);
//        if (isHandDrag||isFirstLoadList) {//手动去拖动地图
//            getAddress(cameraPosition.target);
//            doSearchQuery();
//        } else if(isBackFromSearchChoose){
//            doSearchQuery();
//        }else{
//            mRvAddressAdapter.notifyDataSetChanged();
//        }
//        isHandDrag = true;
//        isFirstLoadList = false;
//    }
//
//    // ========  poi搜索 周边  以下 =====================
//    /**
//     * 开始进行poi搜索   重点
//     * 通过经纬度获取附近的poi信息
//     * <p>
//     * 1、keyword 传 ""
//     * 2、poiSearch.setBound(new PoiSearch.SearchBound(lpTemp, 5000, true)); 根据
//     */
//    protected void doSearchQuery() {
//
//        currentPage = 0;
//        query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
//        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页
//
//        LatLonPoint lpTemp = convertToLatLonPoint(mFinalChoosePosition);
//
//        if (lpTemp != null) {
//            poiSearch = new PoiSearch(this, query);
//            poiSearch.setOnPoiSearchListener(this);  // 实现  onPoiSearched  和  onPoiItemSearched
//            poiSearch.setBound(new PoiSearch.SearchBound(lpTemp, 5000, true));//
//            // 设置搜索区域为以lp点为圆心，其周围5000米范围
//            poiSearch.searchPOIAsyn();// 异步搜索
//        }
//    }
//    /**
//     * poi 附近数据搜索
//     *
//     * @param result
//     * @param rcode
//     */
//    @Override
//    public void onPoiSearched(PoiResult result, int rcode) {
//        if (rcode == 1000) {
//            if (result != null && result.getQuery() != null) {// 搜索poi的结果
//                if (result.getQuery().equals(query)) {// 是否是同一条
//                    poiResult = result;
//                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
//                    mDatas.clear();
//                    //if(isFirstLoadList || isBackFromSearchChoose){
//                    mDatas.add(mAddressEntityFirst);// 第一个元素
//
//                    AddressSearchTextEntity addressEntity = null;
//                    for (PoiItem poiItem : poiItems) {
//                        L.d("得到的数据 poiItem " + poiItem.getSnippet());
//                        addressEntity = new AddressSearchTextEntity(poiItem.getTitle(), poiItem.getSnippet(), false, poiItem.getLatLonPoint());
//                        mDatas.add(addressEntity);
//                    }
//                    if (isHandDrag) {
//                        mDatas.get(0).isChoose = true;
//                    }
//                    mRvAddressAdapter.notifyDataSetChanged();
//                }
//            } else {
//                ToastUtil
//                        .show(ShareLocActivity.this, "对不起，没有搜索到相关数据！");
//            }
//        }
//    }
//
//    @Override
//    public void onPoiItemSearched(PoiItem poiitem, int rcode) {
//
//    }
//    /**
//     * 按照关键字搜索附近的poi信息
//     * @param key
//     */
//    protected void doSearchQueryWithKeyWord(String key) {
//        currentPage = 0;
//        query = new PoiSearch.Query(key, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
//        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页
//
//        if (lp != null) {
//            poiSearch = new PoiSearch(this, query);
//            poiSearch.setOnPoiSearchListener(this);   // 实现  onPoiSearched  和  onPoiItemSearched
//            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
//            // 设置搜索区域为以lp点为圆心，其周围5000米范围
//            poiSearch.searchPOIAsyn();// 异步搜索
//        }
//    }
//    // ========  poi搜索 周边  以上   =====================
//    /**
//     * 根据经纬度得到地址
//     */
//    public void getAddress(final LatLng latLonPoint) {
//        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        RegeocodeQuery query = new RegeocodeQuery(convertToLatLonPoint(latLonPoint), 200, GeocodeSearch.AMAP);
//        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//    }
//
//    /**
//     * 逆地理编码回调
//     */
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                addressName = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
//                L.d("逆地理编码回调  得到的地址：" + addressName);
//                mAddressEntityFirst = new AddressSearchTextEntity(addressName, addressName, true, convertToLatLonPoint(mFinalChoosePosition));
//            } else {
//                ToastUtil.show(ShareLocActivity.this, R.string.no_result);
//            }
//        } else if (rCode == 27) {
//            ToastUtil.show(this, R.string.error_network);
//        } else if (rCode == 32) {
//            ToastUtil.show(this, R.string.error_key);
//        } else {
//            ToastUtil.show(this,
//                    getString(R.string.error_other) + rCode);
//        }
//    }
//
//    /**
//     * 地理编码查询回调
//     */
//    @Override
//    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//    }
//    /**
//     * 把LatLonPoint对象转化为LatLon对象
//     */
//    public LatLng convertToLatLng(LatLonPoint latLonPoint) {
//        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
//    }
//
//    /**
//     * 把LatLng对象转化为LatLonPoint对象
//     */
//    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
//        return new LatLonPoint(latlon.latitude, latlon.longitude);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapview.onResume();
//        whetherToShowDetailInfo(false);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapview.onPause();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapview.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapview.onDestroy();
//    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mTvHint:
//            case R.id.search_bar_layout:
//                Intent intent = new Intent(ShareLocActivity.this, SeaechTextAddressActivity.class);
//                intent.putExtra("point", mFinalChoosePosition);
//                startActivityForResult(intent, OPEN_SEARCH);
//                isBackFromSearchChoose = false;
//                break;
//            case R.id.mTvSearch:
//                AddressSearchTextEntity finalChooseEntity = null;
//                for (AddressSearchTextEntity searchTextEntity : mDatas) {
//                    if (searchTextEntity.isChoose) {
//                        finalChooseEntity = searchTextEntity;
//                    }
//                }
//                if (finalChooseEntity != null) {
//                    L.d("最终点击发送到要上一页的数据："
//                            + "\n 经度" + finalChooseEntity.latLonPoint.getLongitude()
//                            + "\n 纬度" + finalChooseEntity.latLonPoint.getLatitude()
//                            + "\n 地址" + finalChooseEntity.mainAddress
//                    );
//                    ToastUtil.show(ShareLocActivity.this,"最终点击发送到要上一页的数据："
//                            + "\n 经度" + finalChooseEntity.latLonPoint.getLongitude()
//                            + "\n 纬度" + finalChooseEntity.latLonPoint.getLatitude()
//                            + "\n 地址" + finalChooseEntity.mainAddress);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void whetherToShowDetailInfo(boolean isToShow) {
//        if (isToShow) {
//            mPoiDetail.setVisibility(View.VISIBLE);
//
//        } else {
//            mPoiDetail.setVisibility(View.GONE);
//        }
//    }
//
//    // 单击地图
//    @Override
//    public void onMapClick(LatLng latlng) {
//        ToastUtil.show(ShareLocActivity.this, "点击地图结果：  经度：" + latlng.longitude + "   纬度： " + latlng.latitude);
//    }
//    /**
//     * poi没有搜索到数据，返回一些推荐城市的信息
//     */
//    private void showSuggestCity(List<SuggestionCity> cities) {
//        String infomation = "推荐城市\n";
//        for (int i = 0; i < cities.size(); i++) {
//            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
//                    + cities.get(i).getCityCode() + "城市编码:"
//                    + cities.get(i).getAdCode() + "\n";
//        }
//        ToastUtil.show(this, infomation);
//    }
//
//    // 动画复写的三个方法
//    @Override
//    public void onAnimationStart(Animation animation) {
//        mIvCenter.setImageResource(R.drawable.poi_marker_pressed);
//    }
//    @Override
//    public void onAnimationRepeat(Animation animation) {
//
//    }
//    @Override
//    public void onAnimationEnd(Animation animation) {
//        mIvCenter.setImageResource(R.drawable.poi_marker_pressed);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == OPEN_SEARCH && resultCode == RESULT_OK) {
//            AddressSearchTextEntity backEntity = (AddressSearchTextEntity) data.getParcelableExtra("backEntity");
//            mAddressEntityFirst = backEntity; // 上一个页面传过来的 item对象
//            mAddressEntityFirst.isChoose = true;
//
//            isBackFromSearchChoose = true;
//            isHandDrag = false;
//            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(backEntity.latLonPoint.getLatitude(), backEntity.latLonPoint.getLongitude()), 20));
//        }
//    }
//}