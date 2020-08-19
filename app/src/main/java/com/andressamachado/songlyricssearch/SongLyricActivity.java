package com.andressamachado.songlyricssearch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/******************************************************************************************
 *  This activity represents the page where the lyric will be displayed. It will show the title
 *  of the song, the artist name, the lyric and a check box that makes possible to the user add that
 *  song to their list of favorites
 *
 * @author Andressa Machado
 * @version 1.0
 * @see AppCompatActivity
 * @since 2020/08/17
 ******************************************************************************************/
public class SongLyricActivity extends AppCompatActivity {
    /**XML layout elements declarations*/
    TextView titleTextField;
    TextView lyricTextField;
    CheckBox favoriteCheckButton;

    /**Instance to create, delete and execute SQL commands to handle lyrics previously searched*/
    SQLiteDatabase db;

    /**Used to hold if the song is listed as a favorite song */
    boolean isFavorite;
    /**Used to hold the song`s id in the db*/
    long id;
    /**Used to hold the name of the artist or group searched + song title*/
    String songInfo;
    /**Used to hold the lyric found from web searched*/
    String lyric;

    /**
     * OnCreate method is a bundle where the activity is initialized. It is called when the
     * application is not loaded yet. Here is performed basic application startup logic that
     * should happen only once for the entire life of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_page_activity);

        //Getting values passed by an intent from MainActivity
        Intent fromSongLyricsSearchActivity = getIntent();
        lyric = fromSongLyricsSearchActivity.getStringExtra("lyric");
        isFavorite = fromSongLyricsSearchActivity.getBooleanExtra("isFavorite", false);
        songInfo = fromSongLyricsSearchActivity.getStringExtra("song info");

        //Setting artist + song title to the titleTextField textview
        titleTextField = findViewById(R.id.song_info_title);
        titleTextField.setText(songInfo);

        //Setting lyric passed by the intent to lyricTextField textview
        lyricTextField = findViewById(R.id.lyric_text_field);
        lyricTextField.setText(lyric);

        //Setting favorite checkbox according to what was passed by the intent
        favoriteCheckButton = findViewById(R.id.checkbox_favorite_song);
        favoriteCheckButton.setChecked(isFavorite);

        //sets song as favorite if the "make it favorite" checkbox was checked
        favoriteCheckButton.setOnClickListener(v -> {

            SongLyricsDBOpener dbOpener = new SongLyricsDBOpener(this);
            db = dbOpener.getWritableDatabase();

            id = fromSongLyricsSearchActivity.getLongExtra("id",0);

            CheckBox self = (CheckBox)v;
            ContentValues values = new ContentValues();

            if (self.isChecked()) {
                values.put(SongLyricsDBOpener.COL_IS_FAVORITE, 1);
            } else {
                values.put(SongLyricsDBOpener.COL_IS_FAVORITE, 0);
            }

            String[] whereValue = {id+""};
            db.update(SongLyricsDBOpener.TABLE_NAME, values, "_id = ?", whereValue);

        });
    }
}