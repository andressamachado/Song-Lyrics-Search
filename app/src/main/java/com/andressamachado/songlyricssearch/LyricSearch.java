package com.andressamachado.songlyricssearch;
/****************************************************************************************
 Filename:SongLyricsSearch.java
 Author: Andressa Pessoa de Araujo Machado [040923007]
 Date: 2020/08/17
 Purpose: A song lyrics search object to represent the search made by the user. It holds the
 artist or band name, the song title, and if it is a favorite song or not
 ***************************************************************************************/

public class LyricSearch {
    private long id;
    private String artist;
    private String songTitle;
    private boolean isFavorite;

    public LyricSearch() {}

    public LyricSearch(long id, String artist, String songTitle, Boolean isFavorite) {
        this.id = id;
        this.artist = artist;
        this.songTitle = songTitle;
        this.isFavorite = isFavorite;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String toString() {
        return this.artist + " - " + this.songTitle +". Favorite: " + isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}
