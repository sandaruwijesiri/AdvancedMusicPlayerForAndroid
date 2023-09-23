package com.example.testapplication2.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Interfaces.AccessToMainActivity;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.DeletePlaylistDialog;
import com.example.testapplication2.Miscellaneous.PlaylistItemCanvas;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.Enums.EnumMessages.LOAD_SPECIFIC_PLAYLIST_FRAGMENT;
import static com.example.testapplication2.MainActivity.allPlaylists;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.updateQueue;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;
import static com.example.testapplication2.OtherClasses.Methods.setCurrentPlaylistItem;

public class playlistsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<playlistsFragmentRecyclerViewAdapter.ViewHolder> implements DeletePlaylistDialog.DeletePlaylistDialogListener {

    MainActivity mainActivity;
    AccessToMainActivity accessToMainActivity;
    Context context;
    ConstraintLayout constraintLayout;
    StringBuilder stringBuilder;
    int defaultThemeColorForItem = Color.parseColor("#E34949");

    public playlistsFragmentRecyclerViewAdapter(MainActivity mainActivity, Context context,ConstraintLayout constraintLayout) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.constraintLayout = constraintLayout;

        accessToMainActivity = mainActivity;
        stringBuilder = new StringBuilder();
    }

    @NonNull
    @Override
    public playlistsFragmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_playlists, parent, false);
        playlistsFragmentRecyclerViewAdapter.ViewHolder viewHolder = new playlistsFragmentRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentPlaylist = allPlaylists.get(viewHolder.getAdapterPosition());
                setCurrentPlaylistItem(allPlaylists.get(viewHolder.getAdapterPosition()));
                weakReferencesToFragments.getMessage(MainActivity.name, LOAD_SPECIFIC_PLAYLIST_FRAGMENT,-1,null);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull playlistsFragmentRecyclerViewAdapter.ViewHolder holder, int position) {

        PlaylistItem playlistItem = allPlaylists.get(position);

        holder.playlistName.setText(playlistItem.getPlaylistName());
        stringBuilder.setLength(0);
        String tracks;
        if (playlistItem.getSongItemsPlaylist().size()==1){
            tracks = stringBuilder.append("    ").append(1).append(" Track").toString();
        }else {
            tracks = stringBuilder.append("    ").append(playlistItem.getSongItemsPlaylist().size()).append(" Tracks").toString();
        }
        holder.noOfTracks.setText(tracks);
        stringBuilder.setLength(0);
        holder.duration.setText(stringBuilder.append("    ").append(playlistItem.getDurationAsAString()).toString());

        if (playlistItem.getSongItemsPlaylist().size()>0) {
            SongItem item = playlistItem.getSongItemsPlaylist().get(0);
            holder.playlistsImage.setImageBitmap(item.getThumbnail());
            holder.playlistItemCanvas.SetColor(item.getWrapperForCommonDataSongItem().getThemeColor());
        }else {
            holder.playlistsImage.setImageResource(R.drawable.ic_launcher_foreground_custom);
            holder.playlistItemCanvas.SetColor(defaultThemeColorForItem);
        }
    }

    @Override
    public int getItemCount() {
        return allPlaylists.size();
    }

    @Override
    public void onDeletePlaylistDialogPositiveClick(DialogFragment dialog, int AdapterPosition) {
        File deletePlaylistFile = new File(mainActivity.getDir("Playlists", MODE_PRIVATE), allPlaylists.get(AdapterPosition).getPlaylistName() + ".ser");
        if (deletePlaylistFile.delete()){
            allPlaylists.remove(AdapterPosition);
            //notifyItemRemoved(AdapterPosition); // Buggy behaviour when there are other playlists below the playlist being deleted.
            notifyDataSetChanged();

            File file = new File(context.getDir("Preferences", MODE_PRIVATE),"PlaylistsOrder");
            StringBuilder writeThis = new StringBuilder();

            for (PlaylistItem playlistItem:allPlaylists){
                writeThis.append(playlistItem.getPlaylistName()).append("\n");
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file,false);
                fileOutputStream.write(writeThis.toString().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                Methods.addToErrorLog(e.getMessage(),context);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeletePlaylistDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(mainActivity.getApplicationContext(),"Delete Aborted",Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView playlistName;
        TextView noOfTracks;
        TextView duration;
        ImageView playlistsImage;
        ImageView playlistMoreOptions;
        CardView playlistsImageCard;
        PlaylistItemCanvas playlistItemCanvas;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.playlistName);
            noOfTracks = itemView.findViewById(R.id.playlistNoOfTracks);
            duration = itemView.findViewById(R.id.playlistDuration);
            playlistsImage = itemView.findViewById(R.id.playlistImageView);
            playlistMoreOptions = itemView.findViewById(R.id.playlistMoreOptions);
            playlistsImageCard = itemView.findViewById(R.id.playlistImageCardView);
            playlistItemCanvas = itemView.findViewById(R.id.playlistItemCanvas);

            playlistMoreOptions.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            PopupMenu moreOptionsPopUp = new PopupMenu(view.getContext(),view);
            moreOptionsPopUp.inflate(R.menu.more_options_playlists_menu);
            moreOptionsPopUp.setOnMenuItemClickListener(this);
            moreOptionsPopUp.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id==R.id.PlayPlaylist){

                if (queueObject.QueueArray.size()>0) {
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "Replace existing queue?", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.nowPlayingRecyclerView)
                            .setAction("Confirm", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    replaceQueue(getAdapterPosition());
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
                    replaceQueue(getAdapterPosition());
                }


            }else if (id==R.id.AddToQueuePlaylist){

                PlaylistItem playlistItem = allPlaylists.get(getAdapterPosition());

                if (playlistItem.getSongItemsPlaylist().size()==0){
                    Toast.makeText(context,"Playlist is empty",Toast.LENGTH_SHORT).show();
                }else {
                    boolean bool = queueObject.QueueArray.size()==0;
                    queueObject.addAll(Methods.getPlaylist(playlistItem));
                    if (bool){
                        queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                    }
                    updateQueue = true;
                    SaveQueue(context);
                }
            }else if (id==R.id.RenamePlaylist){
                ((AccessToMainActivity) mainActivity).RenamingPlaylist(allPlaylists.get(getAdapterPosition()).getPlaylistName(),getAdapterPosition());
            }else if (id==R.id.DeletePlaylist){
                DeletePlaylistDialog allowPermissionDialogFragment = new DeletePlaylistDialog(playlistsFragmentRecyclerViewAdapter.this, getAdapterPosition());
                allowPermissionDialogFragment.show(mainActivity.getSupportFragmentManager(),"DeletePlaylistDialogFragment");
            }
            return false;
        }

    }

    public void replaceQueue(int pos){
        PlaylistItem playlistItem = allPlaylists.get(pos);

        if (playlistItem.getSongItemsPlaylist().size()==0){
            Toast.makeText(context,"Playlist is empty",Toast.LENGTH_SHORT).show();
        }else {
            queueObject.updateQueueObjectFullyExceptPreviousSong(Methods.getPlaylist(playlistItem),0);
            updateQueue = true;

            int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
            if (pbState == PlaybackStateCompat.STATE_PLAYING || pbState == PlaybackStateCompat.STATE_PAUSED) {
                MediaControllerCompat.getMediaController(mainActivity).sendCommand("StartPlayBackOver", null, null);
            } else {
                MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
            }
            SaveQueue(context);
        }
    }
}
