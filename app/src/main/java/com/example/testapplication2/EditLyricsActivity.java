package com.example.testapplication2;

import static com.example.testapplication2.Enums.EnumMessages.UPDATE_LYRICS;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.canvasColor;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testapplication2.Miscellaneous.MyCanvas;

public class EditLyricsActivity extends Activity {

    Bundle bundle;
    MyCanvas canvas;
    Toolbar editLyricsToolbar;
    TextView editLyricsSongName;
    Button cancel;
    Button save;
    EditText editLyricsEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_lyrics_layout);

        /*WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(wlp);*/

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        canvas = findViewById(R.id.editLyricsMyCanvas);
        editLyricsToolbar = findViewById(R.id.editLyricsToolbar);
        editLyricsSongName = findViewById(R.id.editLyricsSongName);
        editLyricsEditText = findViewById(R.id.editLyricsEditText);
        cancel = findViewById(R.id.editLyricsCancelButton);
        save = findViewById(R.id.editLyricsSaveButton);

        editLyricsToolbar.setTitle("Edit Lyrics");
        bundle = getIntent().getExtras();

        canvas.SetColor(canvasColor);

        bundle = getIntent().getExtras();
        editLyricsEditText.setText(bundle.getString("Lyrics"));
        editLyricsSongName.setText(bundle.getString("SongName"));


        Drawable defaultIcon = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_baseline_image_24);
        editLyricsToolbar.setNavigationIcon(defaultIcon);
        DrawableCompat.setTint(defaultIcon,baseColor);

        ViewCompat.setOnApplyWindowInsetsListener(editLyricsToolbar, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("SongUri",bundle.getString("SongUri"));
                b.putString("NewLyrics", editLyricsEditText.getText().toString().trim());
                weakReferencesToFragments.getMessage(MainActivity.name,UPDATE_LYRICS,-1,null,b);
                finish();
            }
        });
    }
}
