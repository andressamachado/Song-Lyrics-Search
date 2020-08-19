package com.andressamachado.songlyricssearch;

/****************************************************************************************
 Filename:DetailFragment.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Course: CST2335 - Mobile Graphical Interface Programming, Lab Section 013
 Assignment: Final Project && Lab 8
 Due Date: 2020/08/05
 Submission 2020/08/05
 Professor's Name: Islam Gomaa & Eric Torunski.
 Purpose: This class is to represent the details that will be loaded into the fragment that will be
 loaded to a tablet if the user is using one. Instead of going to another page, the fragment will be
 loaded at the right of the screen.
 ***************************************************************************************/

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentDetail extends Fragment{
    TextView titleTextField;
    TextView lyricTextField;
    CheckBox favoriteCheckButton;
    SQLiteDatabase db;

    boolean isFavorite;
    long id;
    String songInfo;
    String lyric;

    public FragmentDetail(){ }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_detail_layout, container, false);

        Bundle dataFromActivity = getArguments();

        lyric = dataFromActivity.getString(MainActivity.LYRIC);
        isFavorite = dataFromActivity.getBoolean(MainActivity.IS_FAVORITE);
        songInfo = dataFromActivity.getString(MainActivity.ARTIST_NAME) + " - " + dataFromActivity.getString(MainActivity.SONG_TITLE);

        titleTextField = (TextView) result.findViewById(R.id.song_info_title);
        titleTextField.setText(songInfo);

        lyricTextField = (TextView) result.findViewById(R.id.lyric_text_field);
        lyricTextField.setText(lyric);

        favoriteCheckButton = (CheckBox) result.findViewById(R.id.checkbox_favorite_song);
        favoriteCheckButton.setChecked(isFavorite);

        favoriteCheckButton.setOnClickListener(v -> {

            SongLyricsDBOpener dbOpener = new SongLyricsDBOpener(getContext());
            db = dbOpener.getWritableDatabase();

            id = dataFromActivity.getLong("id",0);

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

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }
}
