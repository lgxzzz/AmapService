package com.map.service;

public class TabDb {
    public static String[] getTabsTxt(){
        String[] tabs={"地图","我的"};
        return tabs;
    }
    public static int[] getTabsImg(){
        int[] ids={
                R.drawable.map,
//                R.drawable.eat,
                R.drawable.myself,
        };
        return ids;
    }
    public static int[] getTabsImgLight(){
        int[] ids={
                R.drawable.map,
//                R.drawable.eat,
                R.drawable.myself,
        };
        return ids;
    }
    public static Class[] getFragments(){
        Class[] clz={MapFragment.class,MyFragment.class};
        return clz;
    }
}
