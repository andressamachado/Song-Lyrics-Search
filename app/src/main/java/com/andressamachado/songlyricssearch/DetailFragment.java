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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment{
    ImageView artistIMG;
    TextView lyric;

    public DetailFragment(){ }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle dataFromActivity = getArguments();

        String artistName = dataFromActivity.getString(MainActivity.ARTIST_NAME);
        String songTitle = dataFromActivity.getString(MainActivity.SONG_TITLE);
        String lyrics = dataFromActivity.getString(MainActivity.LYRIC);

        View result = inflater.inflate(R.layout.fragment_detail_layout, container, false);

        artistIMG = result.findViewById(R.id.artist_photo);
        lyric = result.findViewById(R.id.lyric_text_field);
        lyric.setText(lyrics);

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }
}
