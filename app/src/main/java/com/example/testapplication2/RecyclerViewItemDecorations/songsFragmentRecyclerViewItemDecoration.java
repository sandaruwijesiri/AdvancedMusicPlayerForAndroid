package com.example.testapplication2.RecyclerViewItemDecorations;

import static com.example.testapplication2.MainActivity.dpToPixels;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;

import java.util.ArrayList;

public class songsFragmentRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    static Paint paint;
    LinearLayoutManager linearLayoutManager;
    ArrayList<SongItem> songsRecyclerViewAdapterItemDecorationAllSongs;
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    String alphabet3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    TextView Letter;

    public songsFragmentRecyclerViewItemDecoration(RecyclerView recyclerView, ArrayList<SongItem> msongsRecyclerViewAdapterItemDecorationAllSongs,TextView Letter) {

        this.songsRecyclerViewAdapterItemDecorationAllSongs = msongsRecyclerViewAdapterItemDecorationAllSongs;
        this.Letter = Letter;

        paint = new Paint();
        paint.setColor(secondaryBaseColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

    }

    public static void setPaintColor(){
        paint.setColor(secondaryBaseColor);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos==0){
            outRect.top = (int) (10*dpToPixels);
        }else if (pos>0){
            if (sortSongsByWhat==0) {
                if (shouldDrawSeparator(pos,0)){
                    outRect.top = (int) (20 * dpToPixels);
                }
            }else if (sortSongsByWhat==1){
                if (shouldDrawSeparator(pos,1)){
                    outRect.top = (int) (20 * dpToPixels);
                }
            }else if (sortSongsByWhat==2){
                if (shouldDrawSeparator(pos,2)){
                    outRect.top = (int) (20 * dpToPixels);
                }
            }else if (sortSongsByWhat==3){
                if (shouldDrawSeparator(pos,3)){
                    outRect.top = (int) (20 * dpToPixels);
                }
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int childCount = parent.getChildCount();
        View child;
        for (int i=0;i<childCount-1;++i){
            child = parent.getChildAt(i+1);
            int pos = parent.getChildAdapterPosition(child);

            if (sortSongsByWhat==0) {
                if (pos > 0) {
                    if (shouldDrawSeparator(pos,0)) {
                        c.drawLine((float) (0.25 * parent.getRight()), child.getY() - 25, (float) (0.75 * parent.getRight()), child.getY() - 25, paint);
                    }
                }
            }else if (sortSongsByWhat==1){
                if (shouldDrawSeparator(pos,1)){
                    c.drawLine((float) (0.25 * parent.getRight()), child.getY() - 25, (float) (0.75 * parent.getRight()), child.getY() - 25, paint);
                }
            }else if (sortSongsByWhat==2){
                if (pos>0 && shouldDrawSeparator(pos,2)){
                    c.drawLine((float) (0.25 * parent.getRight()), child.getY() - 25, (float) (0.75 * parent.getRight()), child.getY() - 25, paint);
                }
            }else if (sortSongsByWhat==3){
                if (pos>0 && shouldDrawSeparator(pos,3)){
                    c.drawLine((float) (0.25 * parent.getRight()), child.getY() - 25, (float) (0.75 * parent.getRight()), child.getY() - 25, paint);
                }
            }

        }

        if (sortSongsByWhat==0) {
            String l = "#";
            if (childCount > 0) {
                l = songsRecyclerViewAdapterItemDecorationAllSongs.get(parent.getChildAdapterPosition(parent.getChildAt(0))).getSongName().substring(0, 1).toUpperCase();
                if (!alphabet3.contains(l)) {
                    l = "#";
                }
            }
            Letter.setText(l);
        }else if (sortSongsByWhat==1){
            if (childCount > 0) {
                int l = songsRecyclerViewAdapterItemDecorationAllSongs.get(parent.getChildAdapterPosition(parent.getChildAt(0))).getDuration()/60000;
                if (l<5) {
                    Letter.setText(l + "-" + (l + 1));
                }else {
                    Letter.setText("5+");
                }
            }
        }else if (sortSongsByWhat==2){
            if (childCount > 0) {
                long timeSinceLastAdded = System.currentTimeMillis() - songsRecyclerViewAdapterItemDecorationAllSongs.get(parent.getChildAdapterPosition(parent.getChildAt(0))).getCreationTimeSinceEpochInMillis();
                if (timeSinceLastAdded<=1000*60*60*24*7*2L) {
                    Letter.setText("2 w");
                }else if (timeSinceLastAdded<=1000*60*60*24* 30L) {
                    Letter.setText("1 M");
                }else if (timeSinceLastAdded<=1000*60*60*24* 180L) {
                    Letter.setText("6 M");
                }else if (timeSinceLastAdded<=1000*60*60*24* 365L) {
                    Letter.setText("1 y");
                }else{
                    Letter.setText("1+ y");
                }
            }
        }else if (sortSongsByWhat==3){
            if (childCount > 0) {
                int position = parent.getChildAdapterPosition(parent.getChildAt(0));
                int size = songsRecyclerViewAdapterItemDecorationAllSongs.size();
                if (position<size/4) {
                    Letter.setText("Q1");
                }else if (position<size/2) {
                    Letter.setText("Q2");
                }else if (position<size*3/4) {
                    Letter.setText("Q3");
                }else{
                    Letter.setText("Q4");
                }
            }
        }
    }

    public boolean shouldDrawSeparator(int pos, int sortBy){
        if (sortBy==0){
            String firstLetterOfPosMinusOne = songsRecyclerViewAdapterItemDecorationAllSongs.get(pos - 1).getSongName().substring(0, 1).toUpperCase();
            String firstLetterOfPos = songsRecyclerViewAdapterItemDecorationAllSongs.get(pos).getSongName().substring(0, 1).toUpperCase();
            if (alphabet.contains(firstLetterOfPos) && alphabet.contains(firstLetterOfPosMinusOne)) {
                if (!firstLetterOfPosMinusOne.equals(firstLetterOfPos)) {
                    return true;
                }
            } else if ((alphabet.contains(firstLetterOfPos) && !alphabet.contains(firstLetterOfPosMinusOne)) || (!alphabet.contains(firstLetterOfPos) && alphabet.contains(firstLetterOfPosMinusOne))) {
                return true;
            }
        }else if (sortBy==1){
            int durationOfPosMinusOne = songsRecyclerViewAdapterItemDecorationAllSongs.get(pos-1).getDuration();
            int durationOfPos = songsRecyclerViewAdapterItemDecorationAllSongs.get(pos).getDuration();
            if (
                    ((durationOfPosMinusOne<60000 && durationOfPos>=60000)||(durationOfPosMinusOne>=60000 && durationOfPos<60000)) ||                      // 1 minute
                            ((durationOfPosMinusOne<120000 && durationOfPos>=120000)||(durationOfPosMinusOne>=120000 && durationOfPos<120000)) ||          // 2 minutes
                            ((durationOfPosMinusOne<180000 && durationOfPos>=180000)||(durationOfPosMinusOne>=180000 && durationOfPos<180000)) ||          // 3 minutes
                            ((durationOfPosMinusOne<240000 && durationOfPos>=240000)||(durationOfPosMinusOne>=240000 && durationOfPos<240000)) ||          // 4 minutes
                            ((durationOfPosMinusOne<300000 && durationOfPos>=300000)||(durationOfPosMinusOne>=300000 && durationOfPos<300000))             // 5 minutes
            ){
                return true;
            }
        }else if(sortBy==2){
            long now=System.currentTimeMillis();
            long timeSinceLastAddedOfPosMinusOne = now - songsRecyclerViewAdapterItemDecorationAllSongs.get(pos-1).getCreationTimeSinceEpochInMillis();
            long timeSinceLastAddedOfPos = now - songsRecyclerViewAdapterItemDecorationAllSongs.get(pos).getCreationTimeSinceEpochInMillis();
            if (
                    ((timeSinceLastAddedOfPosMinusOne<1000*60*60*24*7*2L && timeSinceLastAddedOfPos>=1000*60*60*24*7*2L)||(timeSinceLastAddedOfPosMinusOne>=1000*60*60*24*7*2L && timeSinceLastAddedOfPos<1000*60*60*24*7*2L)) ||               // two weeks
                            ((timeSinceLastAddedOfPosMinusOne<1000*60*60*24* 30L && timeSinceLastAddedOfPos>=1000*60*60*24* 30L)||(timeSinceLastAddedOfPosMinusOne>=1000*60*60*24* 30L && timeSinceLastAddedOfPos<1000*60*60*24* 30L)) ||       // 1 month
                            ((timeSinceLastAddedOfPosMinusOne<1000*60*60*24* 180L && timeSinceLastAddedOfPos>=1000*60*60*24* 180L)||(timeSinceLastAddedOfPosMinusOne>=1000*60*60*24* 180L && timeSinceLastAddedOfPos<1000*60*60*24* 180L)) ||   // six months
                            ((timeSinceLastAddedOfPosMinusOne<1000*60*60*24* 365L && timeSinceLastAddedOfPos>=1000*60*60*24* 365L)||(timeSinceLastAddedOfPosMinusOne>=1000*60*60*24* 365L && timeSinceLastAddedOfPos<1000*60*60*24* 365L))      // year
            ){
                return true;
            }
        }else if(sortBy==3){
            int size=songsRecyclerViewAdapterItemDecorationAllSongs.size();
            if (pos==size/4 || pos==size/2 || pos==size*3/4){
                return true;
            }
        }
        return false;
    }
}
