package com.example.gamecalendar.appinfo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamecalendar.R;
import com.example.gamecalendar.activities.AppActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.litepal.LitePalApplication.getContext;


public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private List<AppInfo> mAppList;

    private Map<Integer, Boolean> map = new HashMap<>();
    private boolean onBind;
    private int checkedPosition = -1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        CheckBox checkBox;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_icon);
            name = itemView.findViewById(R.id.app_name);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            view = itemView;
        }
    }

    public AppAdapter(List<AppInfo> appList) {
        mAppList = appList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //得到当前选中的位置
    public int getCheckedPosition() {
        return checkedPosition;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(mAppList.get(position).getAppName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                }else{
                    holder.checkBox.setChecked(true);
                }
            }
        });
        holder.icon.setImageBitmap(drawableToBitmap(mAppList.get(position).getAppIcon()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    map.clear();
                    map.put(position, true);
                    checkedPosition = position;
                    AppActivity.choosePosition=position;
                } else {
                    map.remove(position);
                    if (map.size() == 0) {
                        checkedPosition = -1;
                        AppActivity.choosePosition=-1;
                    }
                }
                if (!onBind) {
                    notifyDataSetChanged();
                }
            }
        });
        onBind = true;
        if (map != null && map.containsKey(position)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
