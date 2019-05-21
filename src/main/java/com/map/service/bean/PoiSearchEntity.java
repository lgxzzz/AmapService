package com.map.service.bean;


import com.amap.api.services.core.PoiItem;

/**
 * Created by lgx on 2019/5/14.
 */

public class PoiSearchEntity {
    boolean isChoose = false;
    PoiItem poiItem;

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public PoiItem getPoiItem() {
        return poiItem;
    }

    public void setPoiItem(PoiItem poiItem) {
        this.poiItem = poiItem;
    }
}
