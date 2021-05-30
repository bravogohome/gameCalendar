package com.example.gamecalendar.appinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetAppsInfo {

    private PackageManager packageManager;
    private int mIconDpi;
    private List<AppInfo> appInfos = new ArrayList<AppInfo>();

    public GetAppsInfo(Context mContext) {
        ActivityManager activityManager =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = mContext.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();
    }

    private void loadAppsInfo() {
        List<ResolveInfo> apps = null;
        Intent filterIntent = new Intent(Intent.ACTION_MAIN, null);
        //Intent.CATEGORY_LAUNCHER主要的过滤条件
        filterIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = packageManager.queryIntentActivities(filterIntent, 0);
        for (ResolveInfo resolveInfo : apps) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(resolveInfo.activityInfo.applicationInfo.packageName);
            appInfo.setAppName(resolveInfo.loadLabel(packageManager).toString());
            appInfo.setAppIcon(getResIconFormActyInfo(resolveInfo.activityInfo));
            appInfo.setAppPinyin(toPinyin(appInfo.getAppName()));
            appInfos.add(appInfo);
        }
    }

    private String toPinyin(String name) {
        HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
        hanyuPinyin.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String pinyin = null;
        char firstChar = name.charAt(0);
        try {
            //是否在汉字范围内
            if (firstChar >= 0x4e00 && firstChar <= 0x9fa5) {
                pinyin = PinyinHelper.toHanyuPinyinString(name, hanyuPinyin, "");
            }else if (firstChar>=97&&firstChar<=122){
                //小写字母转大写
                char[] chars=name.toCharArray();
                chars[0]-=32;
                pinyin=new String(chars);
            }else {
                pinyin = name;
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return pinyin;
    }

    private Drawable getResIconFormActyInfo(ActivityInfo info) {
        Resources resources;
        try {
            resources = packageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getResIconFormActyInfo(resources, iconId);
            }
        }
        return getDefaultIcon();
    }


    private Drawable getResIconFormActyInfo(Resources resources, int iconId) {
        Drawable drawable;
        try {
            drawable = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            drawable = null;
        }
        return (drawable != null) ? drawable : getDefaultIcon();
    }

    //获取一个默认的图标，避免为空的情况
    private Drawable getDefaultIcon() {
        return getResIconFormActyInfo(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public AppInfo getAppByPackageName(String packageName){
        AppInfo appInfo=new AppInfo();
        try{
            ApplicationInfo applicationInfo=packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
            appInfo.setAppIcon(packageManager.getApplicationIcon(applicationInfo));
            appInfo.setPackageName(packageName);
            appInfo.setAppName(packageManager.getApplicationLabel(applicationInfo).toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return appInfo;
    }

    //外部获取信息的方法
    public List<AppInfo> getAppList() {
        loadAppsInfo();
        sortList();
        return appInfos;
    }

    //排序
    // final Collator pinyin=Collator.getInstance(Locale.CHINA);
    private void sortList() {
        //
        List<String> appPinyin = new ArrayList<>();
        for (AppInfo appInfo : appInfos) {
            appPinyin.add(appInfo.getAppPinyin());
        }

     /*   Collections.sort(appPinyin, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return pinyin.compare(o1, o2);
            }
        });*/
        Collections.sort(appPinyin);
        List<AppInfo> appInfosTemp = new ArrayList<>();
        for (String string : appPinyin) {
            for (AppInfo appInfo : appInfos) {
                if (appInfo.getAppPinyin().equals(string)) {
                    appInfosTemp.add(appInfo);
                    //Log.d("TAG", "sortList: "+string);
                }
            }
        }
        appInfos = appInfosTemp;
    }
}