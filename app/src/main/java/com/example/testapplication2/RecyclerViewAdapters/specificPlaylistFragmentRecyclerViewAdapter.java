package com.example.testapplication2.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.Fragments.SpecificPlaylistFragment;
import com.example.testapplication2.Interfaces.DraggingItems;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.SongItemCanvas;
import com.example.testapplication2.R;
import com.google.android.material.snackbar.Snackbar;

import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_ITEM_INSERTED;
import static com.example.testapplication2.Fragments.SpecificPlaylistFragment.isArtistItem;
import static com.example.testapplication2.MainActivity.AllSongsExceptArchived;
import static com.example.testapplication2.MainActivity.archivePlaylistItem;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.rewritePlaylistData;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.updateQueue;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;

public class specificPlaylistFragmentRecyclerViewAdapter extends RecyclerView.Adapter<specificPlaylistFragmentRecyclerViewAdapter.ViewHolder> {

    MainActivity mainActivity;
    public PlaylistItem playlistItem;
    DraggingItems draggingItems;
    Context context;
    ConstraintLayout constraintLayout;
    StringBuilder stringBuilder;
    Snackbar onClickSnackBar;
    Snackbar removeUndoSnackBar;

    public specificPlaylistFragmentRecyclerViewAdapter(MainActivity mmainActivity,Context context,ConstraintLayout constraintLayout, SpecificPlaylistFragment mselfFragment, PlaylistItem mplaylistItem) {
        this.mainActivity = mmainActivity;
        this.playlistItem = mplaylistItem;
        this.context = context;
        this.constraintLayout = constraintLayout;

        draggingItems = (DraggingItems) mselfFragment;
        stringBuilder = new StringBuilder();
    }

    @NonNull
    @Override
    public specificPlaylistFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_songs, parent, false);
        specificPlaylistFragmentRecyclerViewAdapter.ViewHolder viewHolder = new specificPlaylistFragmentRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.listItemSongsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (queueObject.QueueArray.size()>0) {
                    onClickSnackBar = Snackbar
                            .make(constraintLayout, "Replace existing queue?", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.nowPlayingRecyclerView)
                            .setAction("Confirm", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    replaceQueue(viewHolder.getAdapterPosition());
                                }
                            });
                    onClickSnackBar.getView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickSnackBar.dismiss();
                        }
                    });
                    onClickSnackBar.setActionTextColor(secondaryBaseColor);
                    onClickSnackBar.show();
                }else {
                    replaceQueue(viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    public void replaceQueue(int pos){

        queueObject.updateQueueObjectFullyExceptPreviousSong(playlistItem.getSongItemsPlaylist(),pos);
        updateQueue = true;

        int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
        if (pbState == PlaybackStateCompat.STATE_PLAYING || pbState == PlaybackStateCompat.STATE_PAUSED) {
            MediaControllerCompat.getMediaController(mainActivity).sendCommand("StartPlayBackOver",null,null);
        } else {
            MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
        }
        SaveQueue(context);
    }

    @Override
    public void onBindViewHolder(@NonNull specificPlaylistFragmentRecyclerViewAdapter.ViewHolder holder, int position) {
        stringBuilder.setLength(0);
        SongItem item = playlistItem.getSongItemsPlaylist().get(position);
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

    }

    @Override
    public int getItemCount() {
        return playlistItem.getSongItemsPlaylist().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,  PopupMenu.OnMenuItemClickListener {

        TextView songName;
        TextView artist;
        TextView duration;
        ImageView thumbnail;
        ImageView moreOptionsSpecificPlaylist;
        ImageView explicitImageView;
        SongItemCanvas songItemCanvas;
        ConstraintLayout listItemSongsConstraintLayout;
        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.songNameSongs);
            artist = itemView.findViewById(R.id.artistSongs);
            duration = itemView.findViewById(R.id.durationSongs);
            listItemSongsConstraintLayout = itemView.findViewById(R.id.listItemSongsConstraintLayout);
            thumbnail = itemView.findViewById(R.id.thumbnailSongs);
            explicitImageView = itemView.findViewById(R.id.explicitImageView);
            songItemCanvas = itemView.findViewById(R.id.songItemCanvas);
            moreOptionsSpecificPlaylist = itemView.findViewById(R.id.moreOptionsSongs);

            moreOptionsSpecificPlaylist.setOnClickListener(this);

            thumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!isArtistItem && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        draggingItems.StartDrag(getAdapterPosition());
                        rewritePlaylistData = true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            PopupMenu moreOptionsPopUp = new PopupMenu(view.getContext(),view);
            moreOptionsPopUp.inflate(R.menu.more_options_specific_playlist_menu);
            if (isArtistItem) {
                moreOptionsPopUp.getMenu().findItem(R.id.RemoveFromPlaylist).setVisible(false);
            }
            moreOptionsPopUp.setOnMenuItemClickListener(this);
            moreOptionsPopUp.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            int pos = getAdapterPosition();
            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
            if (id==R.id.PlayNextSpecificPlaylist){
                boolean bool = queueObject.QueueArray.size()==0;
                queueObject.add(index +1,playlistItem.getSongItemsPlaylist().get(pos));
                if (bool){
                    queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                }
                weakReferencesToFragments.getMessage(QueueFragment.name, NOTIFY_ITEM_INSERTED, index +1,null);
                SaveQueue(context);
            }else if (id==R.id.AddToQueueSpecificPlaylist){
                boolean bool = queueObject.QueueArray.size()==0;
                queueObject.add(playlistItem.getSongItemsPlaylist().get(pos));
                if (bool){
                    queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                }
                weakReferencesToFragments.getMessage(QueueFragment.name, NOTIFY_ITEM_INSERTED, queueObject.QueueArray.size()-1,null);
                SaveQueue(context);
            }else if (id==R.id.RemoveFromPlaylist){
                removeItem(pos);
            }
            return false;
        }
    }

    public void dismissSnackBars(){
        if (onClickSnackBar!=null) {
            onClickSnackBar.dismiss();
        }
        if (removeUndoSnackBar!=null) {
            removeUndoSnackBar.dismiss();
        }
    }

    public void removeItem(int position){
        String snackBarString = "Removed from playlist";
        if (playlistItem==archivePlaylistItem){
            AllSongsExceptArchived.add(playlistItem.getSongItemsPlaylist().get(position));
            snackBarString = "Removed from archive";
        }
        SongItem songItem = playlistItem.getSongItemsPlaylist().get(position);
        playlistItem.getSongItemsPlaylist().remove(position);
        notifyItemRemoved(position);

        rewritePlaylistData = true;

        removeUndoSnackBar = Snackbar
                .make(constraintLayout, snackBarString, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.nowPlayingRecyclerView)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playlistItem.getSongItemsPlaylist().add(position,songItem);
                        notifyItemInserted(position);
                        if (playlistItem==archivePlaylistItem){
                            AllSongsExceptArchived.remove(songItem);
                        }

                        rewritePlaylistData = false;
                    }
                });
        removeUndoSnackBar.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUndoSnackBar.dismiss();
            }
        });
        removeUndoSnackBar.setActionTextColor(secondaryBaseColor);
        removeUndoSnackBar.show();
    }
}
