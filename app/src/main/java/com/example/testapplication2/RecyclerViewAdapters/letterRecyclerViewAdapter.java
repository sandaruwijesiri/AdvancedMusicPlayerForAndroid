package com.example.testapplication2.RecyclerViewAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.Interfaces.LetterScrollInterface;
import com.example.testapplication2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.sortSongsByMode;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;

public class letterRecyclerViewAdapter extends RecyclerView.Adapter<letterRecyclerViewAdapter.ViewHolder>{
    ArrayList<String> letters = new ArrayList<String>(Arrays.asList("#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"));
    ArrayList<String> durations = new ArrayList<>(Arrays.asList("0-1 min","1-2 min","2-3 min","3-4 min","4-5 min","5+ min"));
    ArrayList<String> lastAdded = new ArrayList<>(Arrays.asList("Last two weeks","Last 30 days","Six months","This year","Older than a year"));
    ArrayList<String> favourites = new ArrayList<>(Arrays.asList("75%-100%","50%-75%","25%-50%","0%-50%"));
    ArrayList<String> nowShowingList = new ArrayList<>();
    LetterScrollInterface letterScrollInterface;

    public letterRecyclerViewAdapter(Activity activity) {
        letterScrollInterface = (LetterScrollInterface) activity;

        nowShowingList.clear();
        if (sortSongsByWhat==0) {
            nowShowingList.addAll(letters);
        }else if (sortSongsByWhat==1) {
            nowShowingList.addAll(durations);
        }else if (sortSongsByWhat==2){
            nowShowingList.addAll(lastAdded);
        }else if (sortSongsByWhat==3){
            nowShowingList.addAll(favourites);
        }

        if (sortSongsByMode==1){
            Collections.reverse(nowShowingList);
        }
    }

    @NonNull
    @Override
    public letterRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (sortSongsByWhat==0) {
            listItem = layoutInflater.inflate(R.layout.list_item_letter_recyclerview, parent, false);
        }else {
            listItem = layoutInflater.inflate(R.layout.list_item_letter_recyclerview_2, parent, false);
        }
        letterRecyclerViewAdapter.ViewHolder viewHolder = new letterRecyclerViewAdapter.ViewHolder(listItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortSongsByWhat==0) {
                    letterScrollInterface.scrollToLetter(nowShowingList.get(viewHolder.getAdapterPosition()));
                }else if (sortSongsByWhat==1){
                    letterScrollInterface.scrollToDuration(nowShowingList.get(viewHolder.getAdapterPosition()));
                }else if (sortSongsByWhat==2){
                    letterScrollInterface.scrollToLastAdded(nowShowingList.get(viewHolder.getAdapterPosition()));
                }else if (sortSongsByWhat==3){
                    letterScrollInterface.scrollToFavourite(nowShowingList.get(viewHolder.getAdapterPosition()));
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull letterRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.letterTextView.setText(nowShowingList.get(position));
    }

    @Override
    public int getItemCount() {
        return nowShowingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView letterTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            letterTextView = itemView.findViewById(R.id.letterTextView);
            letterTextView.setTextColor(baseColor);
        }
    }
}
