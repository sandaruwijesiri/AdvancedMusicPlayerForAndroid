package com.example.testapplication2.RecyclerViewAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.ArtistItem;
import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Interfaces.LetterScrollInterface;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.testapplication2.Enums.EnumMessages.LOAD_SPECIFIC_PLAYLIST_FRAGMENT;
import static com.example.testapplication2.MainActivity.allPlaylists;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.sortSongsByMode;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.Fragments.SpecificPlaylistFragment.isArtistItem;
import static com.example.testapplication2.OtherClasses.Methods.setCurrentPlaylistItem;

public class artistAdapter extends RecyclerView.Adapter<artistAdapter.ViewHolder>{

    ArrayList<ArtistItem> artists;
    StringBuilder stringBuilder;

    public artistAdapter(Activity activity, ArrayList<ArtistItem> artists) {
        this.artists=artists;
        stringBuilder = new StringBuilder();
    }

    @NonNull
    @Override
    public artistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_artist, parent, false);
        artistAdapter.ViewHolder viewHolder = new artistAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArtistItem item = artists.get(viewHolder.getAdapterPosition());
                isArtistItem = true;
                //currentPlaylist = new PlaylistItem(item.getArtistName(),item.getArtistSongs(),item.getDurationInMillis(),item.getDurationString());
                setCurrentPlaylistItem(new PlaylistItem(item.getArtistName(),item.getArtistSongs(),item.getDurationInMillis(),item.getDurationString()));
                weakReferencesToFragments.getMessage(MainActivity.name, LOAD_SPECIFIC_PLAYLIST_FRAGMENT,-1,null);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull artistAdapter.ViewHolder holder, int position) {
        ArtistItem artist = artists.get(position);
        holder.artistImage.setImageBitmap(artist.getArtistBitmap());
        holder.artistName.setText(artist.getArtistName());
        String tracks;
        if (artist.getArtistSongs().size()==1){
            tracks = stringBuilder.append(1).append(" Track").toString();
        }else {
            tracks = stringBuilder.append(artist.getArtistSongs().size()).append(" Tracks").toString();
        }
        holder.noOfTracks.setText(tracks);
        stringBuilder.setLength(0);
        String songs = null;
        /*for (SongItem songItem:artist.getArtistSongs()){
            stringBuilder.append(songItem.getSongName()).append(", ");
        }
        if (artist.getArtistSongs().size()>0) {
            stringBuilder.substring(0, stringBuilder.length() - 2);
        }
        songs = stringBuilder.toString();*/
        holder.songsInArtists.setText(songs);
        holder.duration.setText(artist.getDurationString());
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImage;
        TextView artistName;
        TextView noOfTracks;
        TextView songsInArtists;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            artistImage = itemView.findViewById(R.id.artistImageViewInArtists);
            artistName = itemView.findViewById(R.id.artistNameInArtists);
            noOfTracks = itemView.findViewById(R.id.noOfTracksInArtists);
            songsInArtists = itemView.findViewById(R.id.songsInArtists);
            duration = itemView.findViewById(R.id.durationInArtists);
        }
    }
}
