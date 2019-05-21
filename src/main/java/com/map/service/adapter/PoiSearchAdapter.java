package com.map.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.map.service.R;
import com.map.service.bean.PoiSearchEntity;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<PoiSearchEntity> mData = new ArrayList<>();

    public PoiSearchAdapter(Context mContext, List<PoiItem> data){
        this.mContext = mContext;
        for (int i =0;i<data.size();i++){
            PoiSearchEntity poiSearchEntity = new PoiSearchEntity();
            poiSearchEntity.setChoose(false);
            poiSearchEntity.setPoiItem(data.get(i));
            this.mData.add(poiSearchEntity);
        }
    }

    @Override
    public int getCount() {
        return this.mData.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PoiSearchEntity data = this.mData.get(i);
        ViewHoler holer = null;
        if (view == null){
            holer = new ViewHoler();
            view = LayoutInflater.from(mContext).inflate(R.layout.poi_list_item,null);
            holer.mTitle = (TextView) view.findViewById(R.id.poi_title);
            holer.mContent = (TextView) view.findViewById(R.id.poi_content);
            view.setTag(holer);
        }else{
            holer = (ViewHoler) view.getTag();
        }

        holer.mTitle.setText(data.getPoiItem().getTitle());
        holer.mContent.setText(data.getPoiItem().getSnippet());
        return view;
    }

    class ViewHoler{
        TextView mTitle;
        TextView mContent;
    }


}
