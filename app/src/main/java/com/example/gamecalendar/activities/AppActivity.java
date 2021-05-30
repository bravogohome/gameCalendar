package com.example.gamecalendar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamecalendar.appinfo.AppAdapter;
import com.example.gamecalendar.appinfo.AppInfo;
import com.example.gamecalendar.appinfo.GetAppsInfo;
import com.example.gamecalendar.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity implements View.OnClickListener{
    private List<AppInfo> apps = new ArrayList<>();
    private TextView cancel,confirm;
    public static int choosePosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        choosePosition=-1;

        getSupportActionBar().hide();   //隐藏标题栏,noActionBar不需要

        cancel=findViewById(R.id.text_cancel);
        cancel.setOnClickListener(this);
        confirm=findViewById(R.id.text_confirm);
        confirm.setOnClickListener(this);

        GetAppsInfo getAppsInfo=new GetAppsInfo(this);
        apps=getAppsInfo.getAppList();
        RecyclerView recyclerView = findViewById(R.id.app_list);
        AppAdapter adapter=new AppAdapter(apps);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_cancel:
                finish();
                break;
            case R.id.text_confirm:
                if (choosePosition==-1){
                    Toast.makeText(this, "未选择应用!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent();
                    intent.putExtra("chooseAppName",apps.get(choosePosition).getAppName());
                    intent.putExtra("chooseAppPackage",apps.get(choosePosition).getPackageName());
                    byte[] appIcon=bitmap2Bytes(drawableToBitmap(apps.get(choosePosition).getAppIcon()));
                    intent.putExtra("chooseAppIcon",appIcon);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
            default:
        }
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, AppActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        choosePosition=-1;
        MainActivity.progressDialog.dismiss();
    }

    public static  Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转化成byte数组
     * @param bm 需要转换的Bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}