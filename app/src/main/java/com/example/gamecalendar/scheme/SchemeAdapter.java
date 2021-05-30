package com.example.gamecalendar.scheme;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamecalendar.R;
import com.example.gamecalendar.appinfo.AppAdapter;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.ViewHolder> {
    private List<Scheme> mSchemeList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name,startDay,endDay;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.scheme_app_icon);
            name = itemView.findViewById(R.id.scheme_app_name);
            startDay=itemView.findViewById(R.id.scheme_start_day);
            endDay=itemView.findViewById(R.id.scheme_end_day);
            view = itemView;
        }
    }

    public SchemeAdapter(List<Scheme> schemeList) {
        mSchemeList=schemeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scheme, parent, false);
        final SchemeAdapter.ViewHolder holder = new SchemeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mSchemeList.get(position).getDescription());
        holder.startDay.setText(mSchemeList.get(position).getStartYear()+"-"+(mSchemeList.get(position).getStartMonth()>=10?mSchemeList.get(position).getStartMonth():("0"+mSchemeList.get(position).getStartMonth()))+"-"+(mSchemeList.get(position).getStartDay()>=10?mSchemeList.get(position).getStartDay():("0"+mSchemeList.get(position).getStartDay())));
        holder.endDay.setText(mSchemeList.get(position).getEndYear()+"-"+(mSchemeList.get(position).getEndMonth()>=10?mSchemeList.get(position).getEndMonth():("0"+mSchemeList.get(position).getEndMonth()))+"-"+(mSchemeList.get(position).getEndDay()>=10?mSchemeList.get(position).getEndDay():("0"+mSchemeList.get(position).getEndDay())));
        holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(mSchemeList.get(position).getAppIcon(),0,mSchemeList.get(position).getAppIcon().length));
    }

    @Override
    public int getItemCount() {
        return mSchemeList.size();
    }
}
