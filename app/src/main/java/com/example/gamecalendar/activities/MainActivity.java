package com.example.gamecalendar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamecalendar.R;
import com.example.gamecalendar.appinfo.AppInfo;
import com.example.gamecalendar.appinfo.GetAppsInfo;
import com.example.gamecalendar.scheme.Scheme;
import com.example.gamecalendar.scheme.SchemeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.timmy.tdialog.TDialog;
import com.timmy.tdialog.base.BindViewHolder;
import com.timmy.tdialog.listener.OnBindViewListener;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        DatePicker.OnDateChangedListener,
        View.OnClickListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnCalendarSelectListener{
    public static TDialog progressDialog;

    private CalendarView calendarView;
    private TextView day, curLunar, curMonthDay, curYear, yearView;
    private FrameLayout toToday;
    private Calendar mCalendar;
    private int mYear;
    private CalendarLayout mCalendarLayout;
    private FloatingActionButton floatingActionButton;
    private TDialog tDialog;
    private TextView fragmentStartDay,fragmentEndDay,fragmentAppName;
    private DatePicker startDatePicker,endDatePicker;
    private ImageView selectApp;
    private int isChooseApp=0;
    private List<Scheme> schemes=new ArrayList<>();
    private RecyclerView recyclerView;
    private String appPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();   //隐藏标题栏,noActionBar不需要
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消全屏

        initView();
        initViewData();
        initScheme();
    }

    private void initView() {
        //日历
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        day = (TextView) findViewById(R.id.tv_current_day);
        curMonthDay = (TextView) findViewById(R.id.tv_month_day);
        curLunar = (TextView) findViewById(R.id.tv_lunar);
        curYear = (TextView) findViewById(R.id.tv_year);
        mCalendarLayout = findViewById(R.id.calendarLayout);
        toToday = (FrameLayout) findViewById(R.id.fl_current);
        yearView = findViewById(R.id.text_year);
        calendarView.setOnYearChangeListener(this);
        calendarView.setOnCalendarSelectListener(this);
        toToday.setOnClickListener(this);
        curMonthDay.setOnClickListener(this);
        //floatingbutton
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        //recycleView
        recyclerView=findViewById(R.id.scheme);
    }

    private void initViewData() {
        curLunar.setVisibility(View.VISIBLE);
        curYear.setVisibility(View.VISIBLE);
        toToday.setVisibility(View.VISIBLE);
        curMonthDay.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
        yearView.setVisibility(View.GONE);
        mCalendar = calendarView.getSelectedCalendar();
        day.setText(String.valueOf(calendarView.getCurDay()));
        curMonthDay.setText(mCalendar.getMonth() + "月" + mCalendar.getDay() + "日");
        curYear.setText(String.valueOf(mCalendar.getYear()));
        curLunar.setText(mCalendar.getLunar());
        mYear = mCalendar.getYear();

    }
    /**
     * 比较两个时间相差多少天
     * @param startCalendar
     * @param endCalendar
     * @return
     */
    static int differDay(java.util.Calendar startCalendar,java.util.Calendar endCalendar){
        long start =startCalendar.getTimeInMillis();//取得毫秒数
        long end =endCalendar.getTimeInMillis();
        /**
         * 一天 24*60*60*1000 24小时*60分钟*60秒*1000毫秒
         */
        return (int) ((end-start)/(24*60*60*1000));
    }

    private void initScheme(){
        schemes=DataSupport.findAll(Scheme.class);
        Map<String, Calendar> map = new HashMap<>();
        if (!schemes.isEmpty()) {
            try {
                for (int i = 0; i < schemes.size(); i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    Date startDate = sdf.parse("" + schemes.get(i).getStartYear() + (schemes.get(i).getStartMonth() >= 10 ? schemes.get(i).getStartMonth() : ("0" + schemes.get(i).getStartMonth())) + (schemes.get(i).getStartDay() >= 10 ? schemes.get(i).getStartDay() : ("0" + schemes.get(i).getStartDay())));
                    Date endDate = sdf.parse("" + schemes.get(i).getEndYear() + (schemes.get(i).getEndMonth() >= 10 ? schemes.get(i).getEndMonth() : ("0" + schemes.get(i).getEndMonth())) + (schemes.get(i).getEndDay() >= 10 ? schemes.get(i).getEndDay() : ("0" + schemes.get(i).getEndDay())));
                    java.util.Calendar startDay = java.util.Calendar.getInstance();//以系统当前时间初始化Calendar实例
                    java.util.Calendar endDay = java.util.Calendar.getInstance();
                    startDay.clear();//在setXXX方法之前一定要清除当前系统时间的信息
                    endDay.clear();
                    startDay.setTime(startDate);
                    endDay.setTime(endDate);
                    for (; differDay(startDay, endDay) >= 0; startDay.add(java.util.Calendar.DATE, 1)) {
                        map.put(getSchemeCalendar(startDay.get(java.util.Calendar.YEAR), startDay.get(java.util.Calendar.MONTH) + 1, startDay.get(java.util.Calendar.DAY_OF_MONTH), schemes.get(i).getDescription()).toString(),
                                getSchemeCalendar(startDay.get(java.util.Calendar.YEAR), startDay.get(java.util.Calendar.MONTH) + 1, startDay.get(java.util.Calendar.DAY_OF_MONTH), schemes.get(i).getDescription()));
                    }
                }

                initRecycleView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        calendarView.setSchemeDate(map);
    }
    private void initRecycleView(){
            SchemeAdapter adapter=new SchemeAdapter(schemes);
            LinearLayoutManager layoutManager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
    }

    private Calendar getSchemeCalendarWithColor(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }
    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_current:
                calendarView.scrollToCurrent();
                break;
            case R.id.tv_month_day:
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                calendarView.showYearSelectLayout(mYear);
                curLunar.setVisibility(View.GONE);
                curYear.setVisibility(View.GONE);
                toToday.setVisibility(View.GONE);
                curMonthDay.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                yearView.setVisibility(View.VISIBLE);
                break;
            case R.id.fab:
                tDialog=new TDialog.Builder(getSupportFragmentManager())
                        .setLayoutRes(R.layout.fragment_dialog_add_scheme)
                        .setWidth(800)
                        .setHeight(1000)
                        .setScreenWidthAspect(MainActivity.this, 1f) //动态设置弹窗宽度为屏幕宽度百分比(取值0-1f)
                        .setScreenHeightAspect(MainActivity.this, 0.8f)//设置弹窗高度为屏幕高度百分比(取值0-1f)
                        .setGravity(Gravity.CENTER)    //设置弹窗展示位置
                        .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
                        .setCancelableOutside(true)     //弹窗在界面外是否可以点击取消
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {//弹窗隐藏时回调方法
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                fragmentAppName.setText(null);
                                fragmentAppName=null;
                                fragmentStartDay=null;
                                fragmentEndDay=null;
                                endDatePicker=null;
                                startDatePicker=null;
                                selectApp.setImageResource(R.mipmap.icon_select_app);
                                selectApp=null;
                                isChooseApp=0;
                                appPackage=null;
                                initScheme();
                            }
                        })
                        .setOnBindViewListener(new OnBindViewListener() {   //通过BindViewHolder拿到控件对象,进行修改
                            @Override
                            public void bindView(BindViewHolder bindViewHolder) {
                                //  bindViewHolder.setText(R.id.text_test, "弹窗测试");
                                fragmentAppName=bindViewHolder.getView(R.id.app_name);
                                fragmentStartDay=bindViewHolder.getView(R.id.start_day);
                                fragmentEndDay=bindViewHolder.getView(R.id.end_day);
                                selectApp=bindViewHolder.getView(R.id.select_app);
                                startDatePicker=bindViewHolder.getView(R.id.datePicker_start);
                                endDatePicker=bindViewHolder.getView(R.id.datePicker_end);
                                bindViewHolder.getView(R.id.cancel_button).setOnClickListener(MainActivity.this);
                                bindViewHolder.getView(R.id.confirm_button).setOnClickListener(MainActivity.this);
                                selectApp.setOnClickListener(MainActivity.this);
                                fragmentAppName.setOnClickListener(MainActivity.this);
                                java.util.Calendar today=java.util.Calendar.getInstance();
                                fragmentStartDay.setText(today.get(java.util.Calendar.YEAR)+" 年 "+(today.get(java.util.Calendar.MONTH)+1)+" 月 "+today.get(java.util.Calendar.DAY_OF_MONTH)+" 日 ");
                                fragmentEndDay.setText(today.get(java.util.Calendar.YEAR)+" 年 "+(today.get(java.util.Calendar.MONTH)+1)+" 月 "+today.get(java.util.Calendar.DAY_OF_MONTH)+" 日 ");
                                startDatePicker.setOnDateChangedListener(MainActivity.this);
                                endDatePicker.setOnDateChangedListener(MainActivity.this);
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.select_app:
            case R.id.app_name:
                 progressDialog=new TDialog.Builder(getSupportFragmentManager())
                        .setLayoutRes(R.layout.fragment_progress)
                        .setWidth(800)
                        .setHeight(1000)
                        .setScreenWidthAspect(MainActivity.this, 0.6f) //动态设置弹窗宽度为屏幕宽度百分比(取值0-1f)
                        .setScreenHeightAspect(MainActivity.this, 0.6f)//设置弹窗高度为屏幕高度百分比(取值0-1f)
                        .setGravity(Gravity.CENTER)    //设置弹窗展示位置
                        .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
                        .setCancelableOutside(false)     //弹窗在界面外是否可以点击取消
                        .create()
                        .show();
                Intent intent = new Intent(MainActivity.this, AppActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.cancel_button:
                tDialog.dismiss();
                break;
            case R.id.confirm_button:
                if (isChooseApp==1){
                    Scheme scheme=new Scheme();
                    scheme.setStartYear(startDatePicker.getYear());
                    scheme.setStartMonth(startDatePicker.getMonth()+1);
                    scheme.setStartDay(startDatePicker.getDayOfMonth());
                    scheme.setEndYear(endDatePicker.getYear());
                    scheme.setEndMonth(endDatePicker.getMonth()+1);
                    scheme.setEndDay(endDatePicker.getDayOfMonth());
                    scheme.setDescription(fragmentAppName.getText().toString());
                    AppInfo appInfo =new GetAppsInfo(this).getAppByPackageName(appPackage);
                    scheme.setAppIcon(AppActivity.bitmap2Bytes(AppActivity.drawableToBitmap(appInfo.getAppIcon())));
                    scheme.save();
                    tDialog.dismiss();
                }else{
                    Toast.makeText(this, "请选择APP", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (view.getId()){
            case R.id.datePicker_start:
                fragmentStartDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                if (endDatePicker.getYear()<year){
                    endDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentEndDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else if(endDatePicker.getYear()==year&&endDatePicker.getMonth()<monthOfYear){
                    endDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentEndDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else if(endDatePicker.getYear()==year&&endDatePicker.getMonth()==monthOfYear&&endDatePicker.getDayOfMonth()<dayOfMonth){
                    endDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentEndDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else{
                    startDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentStartDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }
                break;
            case R.id.datePicker_end:
                fragmentEndDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                if (startDatePicker.getYear()>year){
                    startDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentStartDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else if(startDatePicker.getYear()==year&&startDatePicker.getMonth()>monthOfYear){
                    startDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentStartDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else if(startDatePicker.getYear()==year&&startDatePicker.getMonth()==monthOfYear&&startDatePicker.getDayOfMonth()>dayOfMonth){
                    startDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentStartDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }else{
                    endDatePicker.init(year, monthOfYear, dayOfMonth,MainActivity.this);
                    fragmentEndDay.setText(year+" 年 "+(monthOfYear+1)+" 月 "+dayOfMonth+" 日 ");
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String appName = data.getStringExtra("chooseAppName");
                    fragmentAppName.setText(appName);
                    appPackage = data.getStringExtra("chooseAppPackage");
                    byte[] appIcon=data.getByteArrayExtra("chooseAppIcon");
                    selectApp.setImageBitmap(BitmapFactory.decodeByteArray(appIcon,0,appIcon.length));
                    isChooseApp=1;
                    //Toast.makeText(this, appName + " " + appPackage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mCalendar = calendar;
        initViewData();
    }

    @Override
    public void onYearChange(int year) {
        yearView.setText(String.valueOf(year));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
    }
}