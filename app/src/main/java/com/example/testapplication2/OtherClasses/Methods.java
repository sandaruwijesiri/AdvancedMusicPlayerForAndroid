package com.example.testapplication2.OtherClasses;

import android.content.Context;
import android.media.MediaMetadata;
import android.support.v4.media.MediaMetadataCompat;

import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.QueueObject;
import com.example.testapplication2.DataClasses.SongItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.Fragments.SpecificPlaylistFragment.previousPlaylist;
import static com.example.testapplication2.MainActivity.AllSongsExceptArchived;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MediaPlaybackService.mediaSession;

public class Methods {
    static ArrayList<String> UriArray = new ArrayList<>();
    static ArrayList<String> SongNameArray = new ArrayList<>();
    static ArrayList<Integer> SongDurationArray = new ArrayList<>();

    public static void resetUriArray(ArrayList<String> uriArrayList){
        uriArrayList.clear();
        for (SongItem item: AllSongsExceptArchived){
            uriArrayList.add(item.getUri());
        }
    }

    public static void resetSongNameArray(ArrayList<String> songNameArrayList){
        songNameArrayList.clear();
        for (SongItem item: AllSongsExceptArchived){
            songNameArrayList.add(item.getSongName());
        }
    }

    public static void resetSongDurationArray(ArrayList<Integer> songDurationArrayList){
        songDurationArrayList.clear();
        for (SongItem item: AllSongsExceptArchived){
            songDurationArrayList.add(item.getDuration());
        }
    }

    public static void setCurrentPlaylistItem(PlaylistItem playlistItem){
        previousPlaylist = currentPlaylist;
        currentPlaylist = playlistItem;
    }

    public static SongItem doesSongExist(String name,String uri,String duration){
        // Archive is not considered.

        resetUriArray(UriArray);
        SongItem songItem=null;
        int index = UriArray.indexOf(uri);
        if (index!=-1) {
            songItem = AllSongsExceptArchived.get(index);
        } else {
            if (SongNameArray.size() == 0) {
                resetSongNameArray(SongNameArray);
            }

            int index2 = SongNameArray.indexOf(name);
            if (index2!=-1) {
                songItem = AllSongsExceptArchived.get(index2);
            } else {
                if (SongDurationArray.size() == 0) {
                    resetSongDurationArray(SongDurationArray);
                }

                int index3 = SongDurationArray.indexOf(Integer.valueOf(duration));
                if (index3!=-1) {
                    songItem = AllSongsExceptArchived.get(index3);
                }
            }

        }

        return songItem;
    }

    public static SongItem doesSongExist(String name,String uri,int duration){
        // Archive is not considered.

        resetUriArray(UriArray);
        SongItem songItem=null;
        int index = UriArray.indexOf(uri);
        if (index!=-1) {
            songItem = AllSongsExceptArchived.get(index);
        } else {
            if (SongNameArray.size() == 0) {
                resetSongNameArray(SongNameArray);
            }

            int index2 = SongNameArray.indexOf(name);
            if (index2!=-1) {
                songItem = AllSongsExceptArchived.get(index2);
            } else {
                if (SongDurationArray.size() == 0) {
                    resetSongDurationArray(SongDurationArray);
                }

                int index3 = SongDurationArray.indexOf(duration);
                if (index3!=-1) {
                    songItem = AllSongsExceptArchived.get(index3);
                }
            }

        }

        return songItem;
    }

    public static void SaveQueue(Context context){

        File fileparent = new File(context.getDir("Queue", MODE_PRIVATE),"QueueObject.ser");
        try {
            fileparent.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(fileparent);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(queueObject);
            out.close();
            fileOut.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            Methods.addToErrorLog(ioException.getMessage(),context);
        }
    }

    public static void ReadQueue(Context context){

        File fileparent = new File(context.getDir("Queue", MODE_PRIVATE),"QueueObject.ser");
        try {
            fileparent.createNewFile();
            FileInputStream fileIn = new FileInputStream(fileparent);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            QueueObject tempQueueObject = (QueueObject) in.readObject();

            in.close();
            fileIn.close();

            if (tempQueueObject.QueueArray.size()>0) {
                if (tempQueueObject.nowPlaying == null && tempQueueObject.previousSong == null) {
                    tempQueueObject.nowPlaying = tempQueueObject.QueueArray.get(0);
                    tempQueueObject.playbackPosition=0;
                } else if (tempQueueObject.nowPlaying == null) {
                    int i = (tempQueueObject.QueueArray.indexOf(tempQueueObject.previousSong)+1)%tempQueueObject.QueueArray.size();
                    tempQueueObject.nowPlaying = tempQueueObject.QueueArray.get(i);
                    tempQueueObject.playbackPosition=0;
                }
                SongItem song;
                SongItem returned;
                for (int i = 0; i < tempQueueObject.QueueArray.size(); ++i) {
                    song = tempQueueObject.QueueArray.get(i);
                    returned = doesSongExist(song.getSongName(), song.getUri(), String.valueOf(song.getDuration()));
                    if (returned == null) {
                        tempQueueObject.remove(i);
                        if (tempQueueObject.nowPlaying==song){
                            if (tempQueueObject.QueueArray.size()>0) {
                                tempQueueObject.nowPlaying = tempQueueObject.QueueArray.get(i % tempQueueObject.QueueArray.size());
                            }else {
                                tempQueueObject.nowPlaying=null;
                            }
                            tempQueueObject.playbackPosition=0;
                        }
                        --i;
                    } else {
                        tempQueueObject.set(i, returned);
                        if (tempQueueObject.nowPlaying==song){tempQueueObject.nowPlaying=tempQueueObject.QueueArray.get(i);}
                    }
                    if (tempQueueObject.previousSong==song){tempQueueObject.previousSong=returned;}
                }
            }else {
                tempQueueObject.nowPlaying = null;
                tempQueueObject.previousSong = null;
                tempQueueObject.playbackPosition=0;
            }
            tempQueueObject.stopAfterThis=null;
            tempQueueObject.previousStop=null;
            queueObject=tempQueueObject;
        } catch (IOException | ClassNotFoundException ioException) {
            queueObject=new QueueObject();
            ioException.printStackTrace();

            addToErrorLog(ioException.getMessage(), context);
        }
    }

    public static void updateMetadata(){
        if(mediaSession!=null) {
            if (queueObject.nowPlaying == null) {
                mediaSession.setMetadata(null);
            } else {
                mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, queueObject.nowPlaying.getSongName())
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, queueObject.nowPlaying.getArtist())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, queueObject.nowPlaying.getThumbnail())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, queueObject.nowPlaying.getDuration())
                        .build()
                );
            }
        }
    }

    public static void addToErrorLog(String errorMessage, Context context){
        File errorLog = new File(context.getDir("ErrorLogDirectory", MODE_PRIVATE), "ErrorLog.txt");
        try {
            errorLog.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(errorLog, true);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy ");
            LocalDateTime now = LocalDateTime.now();
            String toWrite = "\n" + "\n" + "----------------------" + dtf.format(now) + "\n" + "\n" + errorMessage;
            fileOutputStream.write(toWrite.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SongItem> arrangeForAutoPlaylist2(ArrayList<SongItem> songs, String mode){
        ArrayList<SongItem> bassSorted = new ArrayList<>(songs);
        ArrayList<SongItem> trebleSorted = new ArrayList<>(songs);

        bassSorted.sort(new Comparator<SongItem>() {
            @Override
            public int compare(SongItem o1, SongItem o2) {
                return (int) ((o2.getSubBass()+o2.getBass())-(o1.getSubBass()+o1.getBass()));
            }
        });
        trebleSorted.sort(new Comparator<SongItem>() {
            @Override
            public int compare(SongItem o1, SongItem o2) {
                return (int) ((o2.getHigherMidrange()+o2.getPresence()+o2.getBrilliance())-(o1.getHigherMidrange()+o1.getPresence()+o1.getBrilliance()));
            }
        });

        ArrayList<SongItem> retThis = new ArrayList<>();
        SongItem temp;

        int i=0;
        Random random = new Random();
        boolean randomized = "2".equals(mode); // mode can be null.
        boolean flag= random.nextInt(2)==0;       // bass or treble first?
        while (bassSorted.size()>0 && trebleSorted.size()>0){
            if (randomized) {
                i = random.nextInt(Math.min(Math.min(bassSorted.size(), trebleSorted.size()), 5));
            }
            if (flag) {
                temp = bassSorted.get(i);
                retThis.add(temp);
                bassSorted.remove(i);
                trebleSorted.remove(temp);
            }else {
                temp = trebleSorted.get(i);
                retThis.add(temp);
                trebleSorted.remove(i);
                bassSorted.remove(temp);
            }
            flag=!flag;
        }
        return retThis;
    }

    public static ArrayList<SongItem> arrangeForAutoPlaylist(ArrayList<SongItem> songs, String mode){
        ArrayList<SongItem> bassSorted = new ArrayList<>(songs);

        bassSorted.sort(new Comparator<SongItem>() {
            @Override
            public int compare(SongItem o1, SongItem o2) {
                float bassO1 = o1.getSubBass()+o1.getBass();
                float trebleO1 = o1.getHigherMidrange()+o1.getPresence()+o1.getBrilliance();

                float bassO2 = o2.getSubBass()+o2.getBass();
                float trebleO2 = o2.getHigherMidrange()+o2.getPresence()+o2.getBrilliance();

                if (bassO1<=0 && bassO2<=0){
                    return Float.compare(trebleO1, trebleO2);
                }else if (bassO1<=0){
                    return 1;
                }else if (bassO2<=0){
                    return -1;
                }else {
                    return Float.compare(trebleO1/bassO1,trebleO2/bassO2);
                }
            }
        });

        ArrayList<SongItem> trebleSorted = new ArrayList<>(bassSorted);
        Collections.reverse(trebleSorted);

        ArrayList<SongItem> retThis = new ArrayList<>();
        SongItem temp;
        int i=0;
        Random random = new Random();
        boolean randomized = "2".equals(mode); // mode can be null.
        boolean bassFirst= random.nextInt(2)==0;       // bass or treble first?
        while (bassSorted.size()>0 && trebleSorted.size()>0){
            if (randomized) {
                i = random.nextInt(Math.min(Math.min(bassSorted.size(), trebleSorted.size()), 5));
            }
            if (bassFirst) {
                temp = bassSorted.get(i);
                retThis.add(temp);
                bassSorted.remove(i);
                trebleSorted.remove(temp);
            }else {
                temp = trebleSorted.get(i);
                retThis.add(temp);
                trebleSorted.remove(i);
                bassSorted.remove(temp);
            }
            bassFirst=!bassFirst;
        }
        return retThis;
    }

    public static ArrayList<SongItem> getPlaylist(PlaylistItem playlistItem){
        String playlistName = playlistItem.getPlaylistName();
        /*if (playlistName.startsWith("auto")) {
            String mode = null;
            if (playlistName.length() > 4) {
                mode = playlistName.substring(4, 5);
            }
            return Methods.arrangeForAutoPlaylist(playlistItem.getSongItemsPlaylist(), mode);
        } else {
            return playlistItem.getSongItemsPlaylist();
        }*/
        if (playlistItem.getSongItemsPlaylist().size()>0 && playlistName.startsWith("auto")){
            //Pattern pattern = Pattern.compile("b[0-9]{2}m[0-9]{2}h[0-9]{2}");
            Pattern pattern = Pattern.compile("code[0-9]{14}");
            Matcher matcher = pattern.matcher(playlistName);
            if (matcher.find()){
                int patternMatchIndex = matcher.start();
                ObjectForGeneratingPlaylist start = new ObjectForGeneratingPlaylist(playlistItem.getSongItemsPlaylist().get(0));
                start.subBass = Float.parseFloat(playlistName.substring(patternMatchIndex+4,patternMatchIndex+6));
                start.bass = Float.parseFloat(playlistName.substring(patternMatchIndex+6,patternMatchIndex+8));
                start.lowerMidrange = Float.parseFloat(playlistName.substring(patternMatchIndex+8,patternMatchIndex+10));
                start.midrange = Float.parseFloat(playlistName.substring(patternMatchIndex+10,patternMatchIndex+12));
                start.higherMidrange = Float.parseFloat(playlistName.substring(patternMatchIndex+12,patternMatchIndex+14));
                start.presence = Float.parseFloat(playlistName.substring(patternMatchIndex+14,patternMatchIndex+16));
                start.brilliance = Float.parseFloat(playlistName.substring(patternMatchIndex+16,patternMatchIndex+18));

                return generatePlaylist(playlistItem,start);
            }else {
                Random random = new Random();
                ObjectForGeneratingPlaylist start = new ObjectForGeneratingPlaylist(playlistItem.getSongItemsPlaylist().get(0));
                start.subBass = random.nextFloat()*100;
                start.bass = random.nextFloat()*100;
                start.lowerMidrange = random.nextFloat()*100;
                start.midrange = random.nextFloat()*100;
                start.higherMidrange =random.nextFloat()*100;
                start.presence = random.nextFloat()*100;
                start.brilliance = random.nextFloat()*100;

                return generatePlaylist(playlistItem,start);
            }
        }else {
            return playlistItem.getSongItemsPlaylist();
        }
    }

    public static ArrayList<SongItem> generatePlaylist(PlaylistItem playlistItem, ObjectForGeneratingPlaylist object){
        ArrayList<SongItem> toReturn = new ArrayList<>();
        ArrayList<ObjectForGeneratingPlaylist> arrayList = new ArrayList<>();
        for (SongItem songItem:playlistItem.getSongItemsPlaylist()){
            arrayList.add(new ObjectForGeneratingPlaylist(songItem));
        }

        if (arrayList.size()==0){
            return toReturn;
        }

        while (arrayList.size()>0){
            object = findClosest(object,arrayList);
            toReturn.add(object.songItem);
            arrayList.remove(object);
        }

        return toReturn;
    }

    public static ObjectForGeneratingPlaylist findClosest(ObjectForGeneratingPlaylist object, ArrayList<ObjectForGeneratingPlaylist> space){
        float radius = Float.MAX_VALUE;
        ObjectForGeneratingPlaylist closest = null;
        for (ObjectForGeneratingPlaylist objectForGeneratingPlaylist:space){
            if (object!=objectForGeneratingPlaylist){
                float radHere = (float) object.getDistance(objectForGeneratingPlaylist);
                if (radHere<radius){
                    closest=objectForGeneratingPlaylist;
                    radius=radHere;
                }
            }
        }
        return closest;
    }

    static class ObjectForGeneratingPlaylist{


        public float subBass;
        public float bass;
        public float lowerMidrange;
        public float midrange;
        public float higherMidrange;;
        public float presence;
        public float brilliance;
        SongItem songItem;

        public ObjectForGeneratingPlaylist(SongItem songItem) {
            this.subBass = songItem.getSubBass();
            this.bass = songItem.getBass();
            this.lowerMidrange = songItem.getLowerMidrange();
            this.midrange = songItem.getMidrange();
            this.higherMidrange = songItem.getHigherMidrange();
            this.presence = songItem.getPresence();
            this.brilliance = songItem.getBrilliance();
            this.songItem = songItem;
        }

        public double getDistance(ObjectForGeneratingPlaylist ogp){
            return Math.sqrt(Math.pow(subBass-ogp.songItem.getSubBass(),2) + Math.pow(bass-ogp.songItem.getBass(),2)
            + Math.pow(lowerMidrange-ogp.songItem.getLowerMidrange(),2) + Math.pow(midrange-ogp.songItem.getMidrange(),2) + Math.pow(higherMidrange-ogp.songItem.getHigherMidrange(),2)
            + Math.pow(presence-ogp.songItem.getPresence(),2) + Math.pow(brilliance-ogp.songItem.getBrilliance(),2));
        }
    }
}
