package com.andressamachado.songlyricssearch;

/****************************************************************************************
 Filename:SongLyricActivity.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Date: 2020/08/17
 Purpose: This activity epresents the page where the lyric will be displayed. It will show the title
 of the song, the artist name, the lyric and a check box that makes possible to the user add that
 song to their list of favorites
 ***************************************************************************************/

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SongLyricActivity extends AppCompatActivity {
    String lyric;
    String songInfo;
    long id;
    boolean isFavorite;
    TextView titleTextField;
    TextView lyricTextField;
    CheckBox favoriteCheckButton;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_page_activity);

        Intent fromSongLyricsSearchActivity = getIntent();
        lyric = fromSongLyricsSearchActivity.getStringExtra("lyric");
        isFavorite = fromSongLyricsSearchActivity.getBooleanExtra("isFavorite", false);
        songInfo = fromSongLyricsSearchActivity.getStringExtra("song info");

        titleTextField = findViewById(R.id.song_info_title);
        titleTextField.setText(songInfo);
        lyricTextField = findViewById(R.id.lyric_text_field);
        lyricTextField.setText(lyric);
        favoriteCheckButton = findViewById(R.id.checkbox_favorite_song);
        favoriteCheckButton.setChecked(isFavorite);

        favoriteCheckButton.setOnClickListener(v -> {

        });
    }
}
