package com.example.testapplication2;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;

public class CreatePlaylistActivity extends Activity {

    EditText editText;
    TextView title;
    TextView warning;
    Button cancelButton;
    Button doneButton;
    Bundle bundle;
    boolean areWeRenaming;
    String playlistName = "";

    ValueAnimator warningAnimation;
    ValueAnimator warningBecomeOrange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_playlist_layout);
        editText = findViewById(R.id.createPlaylistEditText);
        title = findViewById(R.id.createPlaylistTitle);
        warning = findViewById(R.id.createPlaylistWarning);
        cancelButton = findViewById(R.id.createPlaylistCancelButton);
        doneButton = findViewById(R.id.createPlaylistDoneButton);

        bundle = getIntent().getExtras();
        areWeRenaming = "RenamePlaylist".equals(bundle.getString("Requirement"));
        if (areWeRenaming){
            title.setText("Rename Playlist");
            //editText.setHint(bundle.getString("PlaylistName"));
            playlistName = bundle.getString("PlaylistName");
            editText.setText(playlistName);
        }

        warningAnimation = ValueAnimator.ofArgb(Color.parseColor("#FFCC9900"), Color.parseColor("#FFCC0000"), Color.parseColor("#FFCC9900"));
        warningAnimation.setDuration(1000);
        warningAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                int color = (int) updatedAnimation.getAnimatedValue();
                warning.setTextColor(color);
            }
        });

        editText.requestFocus();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    return doneMethod();
                }
                return false;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("EditTextText",editText.getText().toString().trim());
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneMethod();
            }
        });

    }

    public boolean doneMethod(){
        String textInEditText = editText.getText().toString().trim();
        if ("".equals(textInEditText)) {
            warning.setText("\u26A0 Invalid Name.");
            warning.setVisibility(View.VISIBLE);
            editText.setText("");
            warningAnimation.start();
            return true;
        }else{
            if (areWeRenaming && textInEditText.equals(playlistName)){
                Intent intent = new Intent();
                intent.putExtra("EditTextText", textInEditText);
                intent.putExtra("Requirement", bundle.getString("Requirement"));
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
                return false;
            }else {
                File newPlaylist = new File(getDir("Playlists", MODE_PRIVATE), textInEditText + ".ser");
                if (newPlaylist.exists()) {
                    warning.setText("\u26A0 Playlist with same name already exists.");
                    warning.setVisibility(View.VISIBLE);
                    warningAnimation.start();
                    return true;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("EditTextText", textInEditText);
                    intent.putExtra("Requirement", bundle.getString("Requirement"));
                    if (areWeRenaming) {
                        intent.putExtra("PlaylistName", bundle.getString("PlaylistName"));
                        intent.putExtra("PlaylistPosition", bundle.getInt("PlaylistPosition"));
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return false;
                }
            }
        }
    }

}
