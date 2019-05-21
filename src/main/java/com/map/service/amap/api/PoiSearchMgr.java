package com.map.service.amap.api;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

public class PoiSearchMgr implements PoiSearch.OnPoiSearchListener {
    private Context mContext;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint latLonPoint;
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private String keyWord;
    private final int ADDRESS_LOCATION_GET = 3242;
    private String POI_SEARCH_TYPE = "汽车服务|汽车销售|" +
            "//汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
            "//住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
            "//金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";

    private PoiSearchListener mListener;


    public PoiSearchMgr(Context mContext){
        this.mContext = mContext;
    }

    public void setPoiListener(PoiSearchListener listener){
        this.mListener = listener;
    }

    /**
     * 开始进行poi搜索
     */
    public void doSearchQuery(String keyWord, double lat, double lon ) {
        latLonPoint = new LatLonPoint(lat,lon);// 116.472995,39.993743
        currentPage = 0;
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query = new PoiSearch.Query(keyWord, POI_SEARCH_TYPE, "");
        query.setPageSize(30);// 设置每页最多返回多少条poiItem
        query.setPageNum(currentPage);// 设置查第一页
        if (latLonPoint != null) {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 3000, true));//设置搜索范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }

    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {

        //DialogUtils.dismissProgressDialog();
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                Log.e("lgx","搜索的code为===="+code+", result数量=="+result.getPois().size());
                if (result.getQuery().equals(query)) {// 是否是同一次搜索
                    poiResult = result;
                    Log.e("lgx","搜索的code为===="+code+", result数量=="+poiResult.getPois().size());
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        poiItems.clear();
                    }
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    mListener.onSuccess(poiItems);
                }
            } else {
                Log.e("lgx","没有搜索结果");
                mListener.onFail("没有搜索结果");
            }
        } else {
            Log.e("lgx","搜索出现错误");
            mListener.onFail("搜索出现错误");
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    public interface PoiSearchListener{
        public void onSuccess( List<PoiItem> poiItems);
        public void onFail(String error);
    }

}
