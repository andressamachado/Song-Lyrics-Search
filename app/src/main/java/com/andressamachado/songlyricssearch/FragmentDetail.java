package com.andressamachado.songlyricssearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/******************************************************************************************
 *  This class is to represent the details that will be loaded into the fragment that will be
 *  loaded to a tablet if the user is using one. Instead of going to another page, the fragment will be
 *  loaded at the right of the screen.
 *
 * @author Andressa Machado
 * @version 1.0
 * @see androidx.fragment.app.Fragment
 * @since 2020/08/17
 ******************************************************************************************/
public class FragmentDetail extends Fragment{
    /**Declaring elements from fragment_detail_layout*/
    TextView titleTextField;
    TextView lyricTextField;
    CheckBox favoriteCheckButton;

    /**Instance to manage the SQLite database*/
    SQLiteDatabase db;

    /**Variables to receive information from the MainActivity and display here in the fragment*/
    boolean isFavorite;
    long id;
    String songInfo;
    String lyric;

    public FragmentDetail(){ }

    /**
     * Method to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater to inflate the view in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState bundle to reconstruct the fragment from a previous saved state
     * @return a View created to populate
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View containing the song lyric searched
        View result = inflater.inflate(R.layout.fragment_detail_layout, container, false);

        //Bundle instance to get data from MainActivity class
        Bundle dataFromActivity = getArguments();

        //Getting values from MainActivity and saving them to display here
        lyric = dataFromActivity.getString(MainActivity.LYRIC);
        isFavorite = dataFromActivity.getBoolean(MainActivity.IS_FAVORITE);
        songInfo = dataFromActivity.getString(MainActivity.ARTIST_NAME) + " - " + dataFromActivity.getString(MainActivity.SONG_TITLE);

        //Setting song title and artist name to be displayed
        titleTextField = (TextView) result.findViewById(R.id.song_info_title);
        titleTextField.setText(songInfo);

        //Setting lyric to be displayed
        lyricTextField = (TextView) result.findViewById(R.id.lyric_text_field);
        lyricTextField.setText(lyric);

        //Setting isFavorite option
        favoriteCheckButton = (CheckBox) result.findViewById(R.id.checkbox_favorite_song);
        favoriteCheckButton.setChecked(isFavorite);

        //Setting song to be listed as a favorite song
        favoriteCheckButton.setOnClickListener(v -> {
            //Manages updates in the database
            SongLyricsDBOpener dbOpener = new SongLyricsDBOpener(getContext());
            db = dbOpener.getWritableDatabase();

            id = dataFromActivity.getLong("id",0);

            //make it favorite checkbox reference
            CheckBox self = (CheckBox) v;
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

        return result;
    }

    /**
     * Method called when a fragment is first attached to its context.
     *
     * @param context the current state of the application
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentDetail for a tablet, or SongLyricActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }
}
