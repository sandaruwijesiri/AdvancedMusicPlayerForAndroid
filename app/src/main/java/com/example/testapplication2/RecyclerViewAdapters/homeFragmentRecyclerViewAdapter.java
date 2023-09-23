package com.example.testapplication2.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.MainActivity;
import com.example.testapplication2.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.testapplication2.Enums.EnumMessages.SHOW_ARTISTS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_MORE_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_SETTINGS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_SONGS_FRAGMENT;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class homeFragmentRecyclerViewAdapter extends RecyclerView.Adapter<homeFragmentRecyclerViewAdapter.ViewHolder> {

    public ArrayList<String> titles = new ArrayList<>(Arrays.asList("Songs","Artists","Albums","Genres","Settings","More"));
    ArrayList<Drawable> drawables = new ArrayList<>();
    public  int color = Color.parseColor("#000000");

    public homeFragmentRecyclerViewAdapter(Context context) {
        drawables.clear();
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_audio_file_24));
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_people_24));
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_album_24));
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_category_24));
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_settings_24));
        drawables.add(ContextCompat.getDrawable(context,R.drawable.ic_menu_slash_more_24));
    }

    @NonNull
    @Override
    public homeFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_home_fragment_recyclerview, parent, false);
        homeFragmentRecyclerViewAdapter.ViewHolder viewHolder = new homeFragmentRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                if ("Songs".equals(titles.get(pos))){
                    weakReferencesToFragments.getMessage(MainActivity.name, SHOW_SONGS_FRAGMENT,-1,null);
                }else if ("Settings".equals(titles.get(pos))){
                    weakReferencesToFragments.getMessage(MainActivity.name, SHOW_SETTINGS_FRAGMENT,-1,null);
                }else if ("More".equals(titles.get(pos))){
                    weakReferencesToFragments.getMessage(MainActivity.name, SHOW_MORE_FRAGMENT,-1,null);
                }else if ("Artists".equals(titles.get(pos))){
                    weakReferencesToFragments.getMessage(MainActivity.name, SHOW_ARTISTS_FRAGMENT,-1,null);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull homeFragmentRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.icon.setImageDrawable(drawables.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.homeItemTextView);
            icon = itemView.findViewById(R.id.homeItemIcon);
        }
    }

    public void updateHomeRecyclerViewItemColors(){
        for (Drawable drawable:drawables){
            DrawableCompat.setTint(drawable,baseColor);
        }
    }
}
