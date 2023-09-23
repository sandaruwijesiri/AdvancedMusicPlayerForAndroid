package com.example.testapplication2.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.Interfaces.DraggingItems;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.SongItemCanvas;
import com.example.testapplication2.R;
import com.google.android.material.snackbar.Snackbar;

import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.rewritePlaylistData;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;

public class queueFragmentRecyclerViewAdapter  extends RecyclerView.Adapter<queueFragmentRecyclerViewAdapter.ViewHolder> {
    MainActivity mainActivity;
    DraggingItems draggingItems;
    Context context;
    MotionLayout motionLayout;
    StringBuilder stringBuilder;
    int songNameColor;
    int artistAndDurationColor;

    public queueFragmentRecyclerViewAdapter(MainActivity mainActivity, Context context, MotionLayout motionLayout, QueueFragment mselfFragment) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.motionLayout = motionLayout;
        draggingItems = (DraggingItems) mselfFragment;
        stringBuilder = new StringBuilder();
        songNameColor = context.getColor(R.color.primary_text_color);
        artistAndDurationColor = context.getColor(R.color.secondary_text_color);
    }

    @NonNull
    @Override
    public queueFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_songs, parent, false);
        queueFragmentRecyclerViewAdapter.ViewHolder viewHolder = new queueFragmentRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if (queueObject.QueueArray.get(viewHolder.getAdapterPosition())==queueObject.nowPlaying) {
                    message = "Start over?";
                }else {
                    message = "Switch to this song?";
                }

                Snackbar snackbar = Snackbar
                        .make(motionLayout,  message, Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.nowPlayingRecyclerView)
                        .setAction("Confirm", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                queueObject.setNowPlaying(queueObject.QueueArray.get(viewHolder.getAdapterPosition()));
                                queueObject.playbackPosition = 0;

                                int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
                                if (pbState == PlaybackStateCompat.STATE_PLAYING || pbState == PlaybackStateCompat.STATE_PAUSED) {
                                    MediaControllerCompat.getMediaController(mainActivity).sendCommand("StartPlayBackOver", null, null);
                                } else {
                                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
                                }
                            }
                        });
                snackbar.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(secondaryBaseColor);
                snackbar.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull queueFragmentRecyclerViewAdapter.ViewHolder holder, int position) {
        stringBuilder.setLength(0);
        SongItem item = queueObject.QueueArray.get(position);
        WrapperForCommonDataSongItem wrapper = item.getWrapperForCommonDataSongItem();

        int durationInSeconds = item.getDuration()/1000;


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
        holder.songItemCanvas.SetColor(item.getWrapperForCommonDataSongItem().getThemeColor());

        if (item==queueObject.nowPlaying){
            /*holder.songName.setTextColor(Color.parseColor("#CCCC00"));
            holder.artist.setTextColor(Color.parseColor("#CCCC00"));
            holder.duration.setTextColor(Color.parseColor("#CCCC00"));*/

            /*holder.songName.setTextColor(contrastColor);
            holder.artist.setTextColor(contrastColor);
            holder.duration.setTextColor(contrastColor);*/

            holder.songName.setTextColor(secondaryBaseColor);
            holder.artist.setTextColor(secondaryBaseColor);
            holder.duration.setTextColor(secondaryBaseColor);
        }else {
            holder.songName.setTextColor(songNameColor);
            holder.artist.setTextColor(artistAndDurationColor);
            holder.duration.setTextColor(artistAndDurationColor);

            /*holder.songName.setTextColor(Color.parseColor("#552211"));
            holder.artist.setTextColor(Color.parseColor("#552211"));
            holder.duration.setTextColor(Color.parseColor("#552211"));*/
        }

        if (item==queueObject.stopAfterThis){
            holder.stopAfterThisView.setVisibility(View.VISIBLE);
        }else {
            holder.stopAfterThisView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return queueObject.QueueArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,  PopupMenu.OnMenuItemClickListener {

        TextView songName;
        TextView artist;
        TextView duration;
        ImageView thumbnail;
        ImageView moreOptionsQueue;
        ImageView explicitImageView;
        CardView stopAfterThisView;
        SongItemCanvas songItemCanvas;
        ConstraintLayout constraintLayout;
        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.songNameSongs);
            artist = itemView.findViewById(R.id.artistSongs);
            duration = itemView.findViewById(R.id.durationSongs);
            thumbnail = itemView.findViewById(R.id.thumbnailSongs);
            explicitImageView = itemView.findViewById(R.id.explicitImageView);
            songItemCanvas = itemView.findViewById(R.id.songItemCanvas);
            constraintLayout = itemView.findViewById(R.id.listItemSongsConstraintLayout);
            moreOptionsQueue = itemView.findViewById(R.id.moreOptionsSongs);
            stopAfterThisView = itemView.findViewById(R.id.stopAfterThisView);

            moreOptionsQueue.setOnClickListener(this);

            thumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        draggingItems.StartDrag(getAdapterPosition());
                        //rewritePlaylistData = true;               // lead to a bug where if this is called and then songs of an artist were viewed
                                                                    //  through the artist adapter, and then went back, a playlist would be created for that artist.
                                                                    // I have no idea why this line is here in the first place, but only commented it out just in case.
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            PopupMenu moreOptionsPopUp = new PopupMenu(view.getContext(),view);
            moreOptionsPopUp.inflate(R.menu.more_options_queue_menu);
            moreOptionsPopUp.setOnMenuItemClickListener(this);
            moreOptionsPopUp.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            int pos = getAdapterPosition();
            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);

            if (id==R.id.PlayNextQueue){
                queueObject.add(index +1,queueObject.QueueArray.get(pos));
                notifyItemInserted(index +1);
                SaveQueue(context);
            }else if (id==R.id.AddToQueueQueue){
                queueObject.add(queueObject.QueueArray.get(pos));
                notifyItemInserted(queueObject.QueueArray.size()-1);
                SaveQueue(context);
            }else if (id==R.id.StopAfterThisQueue){
                queueObject.setStopAfterThis(queueObject.QueueArray.get(pos));
            }else if (id==R.id.RemoveFromQueue){
                SongItem itemToRemove = queueObject.QueueArray.get(pos);
                queueObject.remove(pos);
                notifyItemRemoved(pos);
                SaveQueue(context);

                if (pos == index){
                    Bundle bundle = new Bundle();
                    bundle.putInt("indexToPlay", pos);
                    MediaControllerCompat.getMediaController(mainActivity).sendCommand("GoToThisSong", bundle, null);
                }

                Snackbar snackbar = Snackbar
                        .make(motionLayout, "Song removed from queue.", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.nowPlayingRecyclerView)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                queueObject.add(pos,itemToRemove);
                                if (queueObject.QueueArray.size()==1){
                                    queueObject.setNowPlaying(itemToRemove);
                                }
                                notifyItemInserted(pos);
                                SaveQueue(context);
                            }
                        });
                snackbar.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(secondaryBaseColor);
                snackbar.show();
            }
            return false;
        }
    }
}
