package com.example.testapplication2.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.NowPlayingBackgroundCanvas;
import com.example.testapplication2.R;

import static com.example.testapplication2.Enums.EnumMessages.SHOW_NOW_PLAYING_FRAGMENT;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class nowPlayingRecyclerViewAdapter extends RecyclerView.Adapter<nowPlayingRecyclerViewAdapter.ViewHolder> {

    boolean setPaintColor = false;
    boolean updatePosition = false;
    MainActivity mainActivity;
    CardView nowPlayingCardCardView;
    StringBuilder stringBuilder;

    public nowPlayingRecyclerViewAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        stringBuilder = new StringBuilder();
    }

    @NonNull
    @Override
    public nowPlayingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_nowplayingcard, parent, false);
        nowPlayingRecyclerViewAdapter.ViewHolder viewHolder = new nowPlayingRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weakReferencesToFragments.getMessage(MainActivity.name,SHOW_NOW_PLAYING_FRAGMENT,-1,null);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().pause();
                } else{
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
                }
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull nowPlayingRecyclerViewAdapter.ViewHolder holder, int position) {
        int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
        if (index>=0) {
            stringBuilder.setLength(0);
            SongItem item;
            if (index == 0) {
                item = queueObject.QueueArray.get(position);
                if (position == 0) {
                    if (setPaintColor) {
                        holder.nowPlayingBackgroundCanvas.setPaintColor();
                        setPaintColor = false;
                    }
                    if (updatePosition) {
                        holder.nowPlayingBackgroundCanvas.updatePosition();
                        updatePosition = false;
                    }
                    nowPlayingCardCardView = holder.thumbnailNowPlayingCardView;
                } else {
                    holder.nowPlayingBackgroundCanvas.setPositionToZero();
                }
            } else {
                item = queueObject.QueueArray.get(index + position - 1);
                if (position == 1) {
                    if (setPaintColor) {
                        holder.nowPlayingBackgroundCanvas.setPaintColor();
                        setPaintColor = false;
                    }
                    if (updatePosition) {
                        holder.nowPlayingBackgroundCanvas.updatePosition();
                        updatePosition = false;
                    }
                    nowPlayingCardCardView = holder.thumbnailNowPlayingCardView;
                } else {
                    holder.nowPlayingBackgroundCanvas.setPositionToZero();
                }
            }

            int durationInSeconds = item.getDuration()/1000;

            WrapperForCommonDataSongItem wrapper = item.getWrapperForCommonDataSongItem();
            if (wrapper.getSongNameApi()==null) {
                holder.songName.setText(item.getSongName());
                holder.artist.setText(item.getArtist());
                holder.explicitImageView.setVisibility(View.GONE);
            } else {
                holder.songName.setText(wrapper.getSongNameApi());
                holder.artist.setText(wrapper.getArtistApi());
                if (wrapper.isExplicitAPI()){
                    holder.explicitImageView.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(holder.explicitImageView.getDrawable(),wrapper.getThemeColor());
                }else {
                    holder.explicitImageView.setVisibility(View.GONE);
                }
            }
            holder.duration.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());
            holder.thumbnail.setImageBitmap(item.getThumbnail());

            if (item==queueObject.stopAfterThis){
                holder.stopAfterThisNowPlaying.setVisibility(View.VISIBLE);
            }else {
                holder.stopAfterThisNowPlaying.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
        if (index==0 || index==queueObject.QueueArray.size()-1){
            return Math.min(2,queueObject.QueueArray.size());
        }

        return Math.min(3,queueObject.QueueArray.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songName;
        TextView artist;
        TextView duration;
        ImageView thumbnail;
        ImageView explicitImageView;
        ConstraintLayout constraintLayout;
        NowPlayingBackgroundCanvas nowPlayingBackgroundCanvas;
        CardView thumbnailNowPlayingCardView;
        CardView stopAfterThisNowPlaying;
        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.songNameNowPlaying);
            artist = itemView.findViewById(R.id.artistNowPlaying);
            duration = itemView.findViewById(R.id.durationNowPlaying);
            thumbnail = itemView.findViewById(R.id.thumbnailNowPlaying);
            explicitImageView = itemView.findViewById(R.id.explicitImageViewNowPlayingCard);
            constraintLayout = itemView.findViewById(R.id.listItemNowPlayingConstraintLayout);
            nowPlayingBackgroundCanvas = itemView.findViewById(R.id.nowPlayingBackgroundCanvas);
            thumbnailNowPlayingCardView = itemView.findViewById(R.id.thumbnailNowPlayingCardView);
            stopAfterThisNowPlaying = itemView.findViewById(R.id.stopAfterThisNowPlaying);

        }
    }

    public void setPaintColor(){
        setPaintColor = true;
        notifyItemChanged(Math.min(1,queueObject.QueueArray.indexOf(queueObject.nowPlaying)),R.id.nowPlayingBackgroundCanvas);
    }

    public void updatePosition(){
        updatePosition = true;
        notifyItemChanged(Math.min(1,queueObject.QueueArray.indexOf(queueObject.nowPlaying)),R.id.nowPlayingBackgroundCanvas);
    }

    public void NotifyDataSetChanged(){
        setPaintColor = true;
        updatePosition = true;
        notifyDataSetChanged();
    }

    public CardView getNowPlayingCardCardView(){
        return nowPlayingCardCardView;
    }
}

