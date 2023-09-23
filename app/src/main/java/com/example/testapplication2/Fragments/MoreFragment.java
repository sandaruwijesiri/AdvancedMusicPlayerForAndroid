package com.example.testapplication2.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.Enums.EnumMessages.LOAD_SPECIFIC_PLAYLIST_FRAGMENT;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.archivePlaylistItem;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.setCurrentPlaylistItem;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MoreFragment extends MyFragmentSuperClass {


    /*TextView noteText;
    TextView pitchText;*/

    public static String name = "MoreFragment";
    static MainActivity mainActivity;

    /*static int totalPitchCount=0;
    static int accurateHits=0;
    static int[] notes = new int[12];*/

    TextView archiveTextView;

    TextView ShowErrorLogTextView;
    TextView HideErrorLogTextView;
    TextView ClearErrorLogTextView;
    TextView ErrorContentTextview;

    public MoreFragment() {
        super("MoreFragment");
        weakReferencesToFragments.saveFragment(5,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.more_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());

        archiveTextView = rootView.findViewById(R.id.archiveTextView);
        archiveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currentPlaylist = archivePlaylistItem;
                setCurrentPlaylistItem(archivePlaylistItem);
                weakReferencesToFragments.getMessage(MainActivity.name, LOAD_SPECIFIC_PLAYLIST_FRAGMENT,-1,null);
            }
        });


        ShowErrorLogTextView = rootView.findViewById(R.id.ShowErrorLogTextView);
        HideErrorLogTextView = rootView.findViewById(R.id.HideErrorLogTextView);
        ClearErrorLogTextView = rootView.findViewById(R.id.ClearErrorLogTextView);
        ErrorContentTextview = rootView.findViewById(R.id.ErrorContentTextview);

        ShowErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File errorLog = new File(getContext().getDir("ErrorLogDirectory", MODE_PRIVATE), "ErrorLog.txt");
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    FileInputStream fileInputStream = new FileInputStream(errorLog);
                    int data = fileInputStream.read();
                    for (;data!=-1;data=fileInputStream.read()){
                        stringBuilder.append((char) data);
                    }
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(), getContext());
                    e.printStackTrace();
                }

                ErrorContentTextview.setText(stringBuilder.toString());
            }
        });

        HideErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorContentTextview.setText("");
            }
        });

        ClearErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorContentTextview.setText("");
                File errorLog = new File(getContext().getDir("ErrorLogDirectory", MODE_PRIVATE), "ErrorLog.txt");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(errorLog);
                    fileOutputStream.write("".getBytes());
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(), getContext());
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
        }
    }

    public void processPitch(float pitchInHz) {    //Uncomment recordAudio permission in manifest to use

        /*pitchText.setText("" + pitchInHz);

        String note = "";
        float n = 0;

        n = (float) (12*(Math.log10(pitchInHz/440.00)/Math.log10(2)));
        float v = n%12;
        if (v<0){v=v+12;}

        float octave = (float) ((n-2.5)/12) + 1;

        if (v<0.5 || v>=11.5){note = "A";}
        else if (v<1.5 && v>=0.5){note = "Bb";}
        else if (v<2.5 && v>=1.5){note = "B";}
        else if (v<3.5 && v>=2.5){note = "C";}
        else if (v<4.5 && v>=3.5){note = "C#";}
        else if (v<5.5 && v>=4.5){note = "D";}
        else if (v<6.5 && v>=5.5){note = "Eb";}
        else if (v<7.5 && v>=6.5){note = "E";}
        else if (v<8.5 && v>=7.5){note = "F";}
        else if (v<9.5 && v>=8.5){note = "F#";}
        else if (v<10.5 && v>=9.5){note = "G";}
        else if (v<11.5 && v>=10.5){note = "G#";}

        noteText.setText(note + (int)(octave + 4));*/
    }

    public static void getScale(String path, Context context){
        new AndroidFFMPEGLocator(context); // https://github.com/JorenSix/TarsosDSP/blob/master/src/android/be/tarsos/dsp/io/android/AndroidFFMPEGLocator.java
        PipedAudioStream f = new PipedAudioStream(path);
        TarsosDSPAudioInputStream audioStream = f.getMonoStream(44100,0);
        AudioDispatcher dispatcher =  new AudioDispatcher(audioStream, 1024, 0);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch2(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    public static void processPitch2(float pitchInHz) {
        /*float n = (float) (12*(Math.log10(pitchInHz/440.00)/Math.log10(2)));
        float v = n%12;
        if (v<0){v=v+12;}
        if (v%1<0.2 || v%1>0.8){
            ++accurateHits;
        }
        ++totalPitchCount;

        if (v<3.5 && v>=2.5){++notes[0];}
        else if (v<4.5 && v>=3.5){++notes[1];}
        else if (v<5.5 && v>=4.5){++notes[2];}
        else if (v<6.5 && v>=5.5){++notes[3];}
        else if (v<7.5 && v>=6.5){++notes[4];}
        else if (v<8.5 && v>=7.5){++notes[5];}
        else if (v<9.5 && v>=8.5){++notes[6];}
        else if (v<10.5 && v>=9.5){++notes[7];}
        else if (v<11.5 && v>=10.5){++notes[8];}
        else if (v<0.5 || v>=11.5){++notes[9];}
        else if (v<1.5 && v>=0.5){++notes[10];}
        else if (v<2.5 && v>=1.5){++notes[11];}


        System.out.println("Notes!!!!!!!!!!!!!!!!!!!!!!!!!!!!! : " + Arrays.toString(notes));
        System.out.println("Accuracy!!!!!!!!!!!!!!!!!!!!!!!!!!!!! : " + (float)accurateHits/totalPitchCount +" : " + accurateHits + " : " + totalPitchCount);*/
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){

    }

    public void thumbnailUpdated(SongItem songItem){

    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("More");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }
}
