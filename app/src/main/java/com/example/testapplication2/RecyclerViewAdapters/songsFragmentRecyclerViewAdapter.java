package com.example.testapplication2.RecyclerViewAdapters;

import static com.example.testapplication2.Enums.EnumMessages.ATTACH_ITEM_TOUCH_LISTENER;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_ITEM_INSERTED;
import static com.example.testapplication2.Enums.EnumMessages.SET_TOOLBAR_ICON;
import static com.example.testapplication2.MainActivity.SelectedSongs;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.updateQueue;
import static com.example.testapplication2.MainActivity.updateSongsRecyclerViewItemConstraintLayout;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.Fragments.SongsFragment;
import com.example.testapplication2.Interfaces.AccessToMainActivity;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.SongItemCanvas;
import com.example.testapplication2.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class songsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<songsFragmentRecyclerViewAdapter.ViewHolder> implements Filterable {

    ArrayList<SongItem> songsRecyclerViewAdapterAllSongs = new ArrayList<>();
    ArrayList<SongItem> songsRecyclerViewAdapterAllSongsParent = new ArrayList<>();
    MainActivity mainActivity;
    Context context;
    AccessToMainActivity accessToMainActivity;
    ConstraintLayout constraintLayout;
    StringBuilder stringBuilder;

   /* public native short[] helloWorrrrd(String path);

    static {
        System.loadLibrary("nativemodule");
    }*/
    public native String helloWorld(String str);
    public native short decode(short[] shorts);

    static {
        System.loadLibrary("nativemodule");
    }
    public songsFragmentRecyclerViewAdapter(Context mcontext,ConstraintLayout constraintLayout, ArrayList<SongItem> msongsRecyclerViewAdapterAllSongs, ArrayList<SongItem> msongsRecyclerViewAdapterAllSongsParent, MainActivity mmainActivity){
        this.songsRecyclerViewAdapterAllSongs = msongsRecyclerViewAdapterAllSongs;
        this.songsRecyclerViewAdapterAllSongsParent = msongsRecyclerViewAdapterAllSongsParent;
        this.mainActivity = mmainActivity;
        this.context = mcontext;
        this.constraintLayout = constraintLayout;

        accessToMainActivity = (AccessToMainActivity) mainActivity;
        stringBuilder = new StringBuilder();
    }

    @NonNull
    @Override
    public songsFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_songs, parent, false);
        songsFragmentRecyclerViewAdapter.ViewHolder viewHolder = new songsFragmentRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SelectedSongs.size()>0){
                    SongItem songItem = songsRecyclerViewAdapterAllSongs.get(viewHolder.getAdapterPosition());
                    if (SelectedSongs.contains(songItem)) {
                        boolean wasAllSelected = SelectedSongs.size()==songsRecyclerViewAdapterAllSongs.size();
                        SelectedSongs.remove(songItem.setIsSelected(false));
                        if (SelectedSongs.size()==0){
                            accessToMainActivity.IsSelectingSongs(false);
                        }
                        selectViewHolder(viewHolder,false);
                        if (wasAllSelected){
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                    }else {
                        SelectedSongs.add(songItem.setIsSelected(true));
                        selectViewHolder(viewHolder,true);
                    }
                    if (SelectedSongs.size()>0) {
                        mainActivity.toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                        if (SelectedSongs.size()==songsRecyclerViewAdapterAllSongs.size()){
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                    } else {
                        mainActivity.toolbar.setSubtitle(null);
                        weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                    }
                    updateSongsRecyclerViewItemConstraintLayout = true;
                }else {

                    if (queueObject.QueueArray.size()>0) {
                        Snackbar snackbar = Snackbar
                                .make(constraintLayout, "Replace existing queue?", Snackbar.LENGTH_LONG)
                                .setAnchorView(R.id.nowPlayingRecyclerView)
                                .setAction("Confirm", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        replaceQueue(viewHolder.getAdapterPosition());
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
                    }else {
                        replaceQueue(viewHolder.getAdapterPosition());
                    }
                }

                //Toast.makeText(context, Arrays.toString(helloWorrrrd(songsRecyclerViewAdapterAllSongs.get(viewHolder.getAdapterPosition()).getUri())),Toast.LENGTH_SHORT).show();
                //System.out.println("RETURNED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + Arrays.toString(helloWorrrrd(songsRecyclerViewAdapterAllSongs.get(viewHolder.getAdapterPosition()).getUri())));
           }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (SelectedSongs.size()>0){
                        SongItem songItem = songsRecyclerViewAdapterAllSongs.get(viewHolder.getAdapterPosition());
                        if (SelectedSongs.contains(songItem)) {
                            boolean wasAllSelected = SelectedSongs.size()==songsRecyclerViewAdapterAllSongs.size();
                            SelectedSongs.remove(songItem.setIsSelected(false));
                            if (SelectedSongs.size()==0){
                                accessToMainActivity.IsSelectingSongs(false);
                            }
                            selectViewHolder(viewHolder,false);
                            if (wasAllSelected){
                                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                            }
                        } else {
                            SelectedSongs.add(songItem.setIsSelected(true));
                            selectViewHolder(viewHolder,true);
                        }
                        if (SelectedSongs.size()==songsRecyclerViewAdapterAllSongs.size()){
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                }else {
                    accessToMainActivity.IsSelectingSongs(true);
                    SelectedSongs.add(songsRecyclerViewAdapterAllSongs.get(viewHolder.getAdapterPosition()).setIsSelected(true));
                    selectViewHolder(viewHolder,true);
                    weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                }
                if (SelectedSongs.size()>0) {
                    mainActivity.toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                    if (SelectedSongs.size()==songsRecyclerViewAdapterAllSongs.size()){
                        weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                    }
                } else {
                    mainActivity.toolbar.setSubtitle(null);
                    weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                }
                updateSongsRecyclerViewItemConstraintLayout = true;
                weakReferencesToFragments.getMessage(SongsFragment.name, ATTACH_ITEM_TOUCH_LISTENER,-1,null);
                return true;
            }
        });

        return viewHolder;
    }

    public void replaceQueue(int pos){

        queueObject.updateQueueObjectFullyExceptPreviousSong(new ArrayList<>(Collections.singletonList(songsRecyclerViewAdapterAllSongs.get(pos))),0);
        updateQueue = true;

        int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
        if (pbState == PlaybackStateCompat.STATE_PLAYING || pbState == PlaybackStateCompat.STATE_PAUSED) {
            MediaControllerCompat.getMediaController(mainActivity).sendCommand("StartPlayBackOver",null,null);
        } else {
            MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull songsFragmentRecyclerViewAdapter.ViewHolder holder, int position) {
        stringBuilder.setLength(0);
        SongItem item = songsRecyclerViewAdapterAllSongs.get(position);
        WrapperForCommonDataSongItem wrapper = item.getWrapperForCommonDataSongItem();

        if (item.isSelected()){
            holder.songItemCanvas.SetColor(item.getWrapperForCommonDataSongItem().getThemeColor(),true);
            //holder.listItemSongsConstraintLayout.setElevation(50*dpToPixels);
            //holder.listItemSongsConstraintLayout.setPadding((int) (10*dpToPixels),(int) (10*dpToPixels),0,(int) (10*dpToPixels));
            /*holder.thumbnailSongsCardView.setScaleX(1.15f);
            holder.thumbnailSongsCardView.setScaleY(1.15f);*/
            //holder.thumbnail.setScaleY(1.25f);
            //holder.listItemSongsConstraintLayout.setTranslationY(-10*dpToPixels);
            /*holder.listItemSongsConstraintLayout.setScaleX(1.04f);
            holder.listItemSongsConstraintLayout.setScaleY(1.01f);
            holder.listItemSongsConstraintLayout.setTranslationY(-40);*/
        }else {
            holder.songItemCanvas.SetColor(item.getWrapperForCommonDataSongItem().getThemeColor());
            /*holder.listItemSongsConstraintLayout.setElevation(0);
            holder.thumbnailSongsCardView.setScaleX(1f);
            holder.thumbnailSongsCardView.setScaleY(1f);*/
            //holder.thumbnail.setScaleY(1f);
            //holder.listItemSongsConstraintLayout.setTranslationY(0);
            /*holder.listItemSongsConstraintLayout.setScaleX(1f);
            holder.listItemSongsConstraintLayout.setScaleY(1f);
            holder.listItemSongsConstraintLayout.setTranslationY(0);*/
            //holder.listItemSongsConstraintLayout.setPadding((int) (20*dpToPixels),(int) (10*dpToPixels),0,(int) (10*dpToPixels));
        }

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

        /*holder.songName.setTextColor(color);
        holder.artist.setTextColor(color);
        holder.duration.setTextColor(color);*/

        holder.duration.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());
        holder.thumbnail.setImageBitmap(item.getThumbnail());

    }

    @Override
    public int getItemCount() {
        return songsRecyclerViewAdapterAllSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,  PopupMenu.OnMenuItemClickListener {

        TextView songName;
        TextView artist;
        TextView duration;
        ImageView thumbnail;
        ImageView moreOptionsSongs;
        ImageView explicitImageView;
        CardView thumbnailSongsCardView;
        SongItemCanvas songItemCanvas;
        public ConstraintLayout listItemSongsConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.songNameSongs);
            artist = itemView.findViewById(R.id.artistSongs);
            duration = itemView.findViewById(R.id.durationSongs);
            thumbnail = itemView.findViewById(R.id.thumbnailSongs);
            explicitImageView = itemView.findViewById(R.id.explicitImageView);
            thumbnailSongsCardView = itemView.findViewById(R.id.thumbnailSongsCardView);
            songItemCanvas = itemView.findViewById(R.id.songItemCanvas);
            listItemSongsConstraintLayout = itemView.findViewById(R.id.listItemSongsConstraintLayout);
            moreOptionsSongs = itemView.findViewById(R.id.moreOptionsSongs);

            moreOptionsSongs.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            PopupMenu moreOptionsPopUp = new PopupMenu(view.getContext(),view);
            moreOptionsPopUp.inflate(R.menu.more_options_songs_menu);
            moreOptionsPopUp.setOnMenuItemClickListener(this);
            moreOptionsPopUp.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            int pos = getAdapterPosition();
            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
            if (id==R.id.AddAsNextSongInQueueSongs){
                boolean bool = queueObject.QueueArray.size()==0;
                queueObject.add(index +1,songsRecyclerViewAdapterAllSongs.get(pos));
                if (bool){
                    queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                }
                weakReferencesToFragments.getMessage(QueueFragment.name, NOTIFY_ITEM_INSERTED, index +1,null);
                SaveQueue(context);
            }else if (id==R.id.AddToQueueSongs){
                boolean bool = queueObject.QueueArray.size()==0;
                queueObject.add(songsRecyclerViewAdapterAllSongs.get(pos));
                if (bool){
                    queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                }
                weakReferencesToFragments.getMessage(QueueFragment.name, NOTIFY_ITEM_INSERTED, queueObject.QueueArray.size()-1,null);
                SaveQueue(context);
            }
            return false;
        }
    }

    public static void selectViewHolder(ViewHolder viewHolder, boolean select){
        viewHolder.songItemCanvas.select(select);
        /*viewHolder.listItemSongsConstraintLayout.setBackgroundColor(Color.parseColor("#00000000"));
        viewHolder.listItemSongsConstraintLayout.setBackgroundColor(Color.parseColor("#22FFFFFF"));*/
    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }
    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SongItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(songsRecyclerViewAdapterAllSongsParent);
            }else {
                String typedText = constraint.toString().toLowerCase().trim();


                for (SongItem rowbyrow : songsRecyclerViewAdapterAllSongsParent){
                    if (rowbyrow.getSongName().toLowerCase().contains(typedText)){
                        filteredList.add(rowbyrow);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songsRecyclerViewAdapterAllSongs.clear();
            songsRecyclerViewAdapterAllSongs.addAll((List)results.values);
            weakReferencesToFragments.getMessage(MainActivity.name,SET_TOOLBAR_ICON,-1,null);
            notifyDataSetChanged();
        }
    };
}
