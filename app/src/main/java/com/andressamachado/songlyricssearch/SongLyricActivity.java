package com.andressamachado.songlyricssearch;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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
    /**Instances to be used to handle the shared preferences between activities*/
    SharedPreferences sharedPreferences;
    /**Key to be used to pass user`s mode preferences to the next activity*/
    public static String user_mode_pref_key = "style";

    /**XML layout elements declarations*/
    View view;
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

        initializeActivity();
        setClickListeners(fromSongLyricsSearchActivity);

    }

    /**
     * Method created to initialize every view from the XML layout used in this activity and to set
     * initial configuration to the app functionality
     */
    private void initializeActivity() {
        //Initialization of activity`s root view
        view = (View) findViewById(R.id.song_lyric_page);

        //Setting artist + song title to the titleTextField textview
        titleTextField = findViewById(R.id.song_info_title);
        titleTextField.setText(songInfo);

        //Setting lyric passed by the intent to lyricTextField textview
        lyricTextField = findViewById(R.id.lyric_text_field);
        lyricTextField.setText(lyric);

        //Setting favorite checkbox according to what was passed by the intent
        favoriteCheckButton = findViewById(R.id.checkbox_favorite_song);
        favoriteCheckButton.setChecked(isFavorite);

        //layout configuration based in the users preference
        layoutPreferenceConfiguration();
    }

    /**
     * Method to process the user layout preference set by using SharedPreferences
     */
    private void layoutPreferenceConfiguration() {
        //Manage to hold the user`s style mode preference to be loaded next time the application is opened
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String modeOptionSaved = sharedPreferences.getString(user_mode_pref_key, "light");

        //if user checks to use the dark mode, load the dark mode style
        if (modeOptionSaved.equals("dark")){
            setModeDark();
        }
    }

    /**
     * Method to set the functionality of the "make it favorite" checkbox. Sets listener to the check.
     * */
    private void setClickListeners(Intent fromSongLyricsSearchActivity) {
        //sets song as favorite if the "make it favorite" checkbox was checked
        favoriteCheckButton.setOnClickListener(v -> {
            //Instance of the SongLyricsDBOpener class that maneges the database fot this application
            SongLyricsDBOpener dbOpener = new SongLyricsDBOpener(this);
            db = dbOpener.getWritableDatabase();

            id = fromSongLyricsSearchActivity.getLongExtra("id",0);

            //"make it favorite" checkbox reference
            CheckBox self = (CheckBox)v;
            //stores the values to be processed
            ContentValues values = new ContentValues();

            //if the make it favorite checkbox is checked, save a number 1 to the db
            if (self.isChecked()) {
                values.put(SongLyricsDBOpener.COL_IS_FAVORITE, 1);
            } else {
                //otherwise, save a 0 to the db
                values.put(SongLyricsDBOpener.COL_IS_FAVORITE, 0);
            }

            //updating the database in case the checkbox has changed
            String[] whereValue = {id+""};
            db.update(SongLyricsDBOpener.TABLE_NAME, values, "_id = ?", whereValue);
        });
    }

    /**
     * Method created to modify root layout configuration to dark mode
     * */
    private void setModeDark() {
        view.setBackgroundColor(getResources().getColor(R.color.darkModeBackgroundColor));
        titleTextField.setTextColor(getResources().getColor(R.color.colorAccent));
        lyricTextField.setTextColor(getResources().getColor(R.color.darkModeTextColor));
        favoriteCheckButton.setBackgroundColor(getResources().getColor(R.color.darkModeBackgroundColor));
        favoriteCheckButton.setTextColor(getResources().getColor(R.color.darkModeTextColor));
    }
}