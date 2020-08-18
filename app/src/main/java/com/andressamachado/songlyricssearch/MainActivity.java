package com.andressamachado.songlyricssearch;

/******************************************************************************************
 * This class is responsible for the main functionality of the application. It extends
 * AppCompatActivity to adjusts newer platform features on older devices. It implements
 * NavigationView.OnNavigationItemSelectedListener to handle events on navigation items.
 *
 * @author Andressa Machado
 * @version 1.0
 * @see AppCompatActivity and NavigationView.OnNavigationItemSelectedListener;
 * @since 2020/08/17
 ******************************************************************************************/

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /** Holds the activity name for the purpose of debugging with log.i*/
    private static final String TAG = "SongLyricSearchActivity";
    /**Key to be used to pass the song title data to the next activity*/
    public static final String SONG_TITLE = "SONG";
    /**Key to be used to pass the artist name data to the next activity*/
    public static final String ARTIST_NAME = "ARTIST";
    /**Key to be used to pass the lyric found data to the next activity*/
    public static final String LYRIC = "LYRIC";
    /**Key to be used to pass user`s mode preferences to the next activity*/
    public static String user_mode_pref_key = "style";

    /**Instances to be used to handle the shared preferences between activities*/
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;

    /**XML layout elements declarations*/
    Toolbar toolbar;
    DrawerLayout drawer;
    View view;
    TextView appTitle;
    EditText artistFieldInput;
    EditText songFieldInput;
    Button searchButton;
    ImageButton googleButton;
    Switch modeSwitch;
    ListView searchedSongList;
    TextView needHelp;

    /**Used to hold the name of the artist or group to be searched. Data from the user input*/
    String artist = null;
    /**Used to hold the title of the song to be searched. Data from the user input*/
    String song = null;
    /**Used to hold the lyric found from web searched*/
    String lyrics;
    /**Instance to create, delete and execute SQL commands to handle lyrics previously searched*/
    SQLiteDatabase db;
    /**Holds every song searched before to avoid add repeated songs to the database*/
    ArrayList<LyricSearch> searchHistory = new ArrayList<>();

    /**Builds the alert dialog*/
    AlertDialog.Builder dialogBuilder;
    /**Instance of an inner class created to build a dialog with a progress bar*/
    LoadingDialog loadingDialog;

    /**Handles the items in the list view*/
    ListAdapter listViewSearchesAdapter;
    /**Used to identify if the device is a tablet. If it is, display a fragment.*/
    boolean isTablet;
    /**Holds the position of the element in the list view*/
    int currentSearchPosition = -1;

    /**
     * OnCreate method is a bundle where the activity is initialized. It is called when the
     * application is not loaded yet. Here is performed basic application startup logic that
     * should happen only once for the entire life of the activity
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search_page_activity);

        initializeActivity();
        setClickListeners();
    }

    /**
     * Method created to initialize every view from the XML layouts used in this activity
     */
    private void initializeActivity() {
        //Initialization of every view used in this activity
        toolbar = (Toolbar) findViewById(R.id.application_toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (View) findViewById(R.id.song_lyrics_main);
        appTitle = (TextView) findViewById(R.id.application_name);
        artistFieldInput = (EditText) findViewById(R.id.artist_field_input);
        songFieldInput = (EditText) findViewById(R.id.song_title_field_input);
        searchButton = (Button) findViewById(R.id.search_button);
        googleButton = (ImageButton) findViewById(R.id.google_button);
        modeSwitch = (Switch) findViewById(R.id.switch_mode);
        searchedSongList = (ListView) findViewById(R.id.searched_song_list);
        needHelp = (TextView) findViewById(R.id.help_button);
        isTablet = findViewById(R.id.fragmentLocation) != null;

        //Sets the toolbar title to empty string to not display the name of the project there as it
        //does not have space to display everything we have to display.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Instance to handle the functionality of the drawer layout and framework action bar to
        // implement the recommended design for navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Standard navigation menu for the application. It is what pops out from the side
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Sets initial background color to white
        view.setBackgroundColor(getResources().getColor(android.R.color.white));

        //Load content from database and puts it into the array list with the songs previously searched
        loadFromDatabase();

        //Manage to hold the user`s style mode preference to be loaded next time the application is opened
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String modeOptionSaved = sharedPreferences.getString(user_mode_pref_key, "light");

        if (modeOptionSaved.equals("light")){
            //mode switch not checked
            modeSwitch.setChecked(false);
            //if user checks to use the light mode, load the light mode style
            setModeLight();
        } else {
            modeSwitch.setChecked(true);
            //if user checks to use the dark mode, load the dark mode style
            setModeDark();
        }

        //builder fot the alert dialog
        dialogBuilder = new AlertDialog.Builder(this);

        //Adapter to handle the items in the list view
        listViewSearchesAdapter = new ListAdapter(searchHistory, getApplicationContext());
        searchedSongList.setAdapter(listViewSearchesAdapter);

        //Instance of an inner class to display an alert dialog with a progress bar inside
        loadingDialog = new LoadingDialog(this);
    }

    /**
     * Method to set the functionality of the buttons and similar. Sets listeners to the click.
     * */
    private void setClickListeners() {
        //Listener to the items within the list view
        searchedSongList.setOnItemClickListener((parent, view, position, id) -> {
            //Starts progress bar
            loadingDialog.startLoadingDialog();

            //Sets fields to be used in the song search
            artistFieldInput.setText(searchHistory.get(position).getArtist().toString());
            songFieldInput.setText(searchHistory.get(position).getSongTitle().toString());

            //get the position in the list
            currentSearchPosition = position;

            //Performs song search
            performSearch();
        });

        //Listener to the search button
        searchButton.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            performSearch();
        });

        //Listener to the google image button
        googleButton.setOnClickListener(v -> {
            //Performs a google search with the data passed by the user
            performGoogleSearch();
        });

        //Listener to the switch on the top right that selects the user`s mode preference
        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Editor to manage the mode change made by the user
            mEditor = sharedPreferences.edit();

            //Performs changes in the layout of the activity
            if (isChecked) {
                mEditor.putString(user_mode_pref_key, "dark");
                setModeDark();
            } else {
                mEditor.putString(user_mode_pref_key, "light");
                setModeLight();
            }

            mEditor.apply();
        });

        //Listener to the text view need help
        needHelp.setOnClickListener(v -> {
            //displays a alert dialog with information about the application
            buildAndDisplayAlertDialog();
        });

        //Listener to a long click on an item in the list view
        searchedSongList.setOnItemLongClickListener((parent, view, position, id) -> {
            //get the id of the song in the array list
            long idToDelete = searchHistory.get(position).getId();

            //Alert dialog asking if the user wants to delete the record
            dialogBuilder.setTitle(R.string.delete_dialog);
            dialogBuilder.setNegativeButton(R.string.delete_no, null);
            dialogBuilder.setPositiveButton(R.string.delete_yes, (dialog, which) -> {
                //Deletes the record from the database
                db.delete(SongLyricsDBOpener.TABLE_NAME, SongLyricsDBOpener.COL_SEARCH_ID + "= ?", new String[]{Long.toString(idToDelete)});
                //Deletes the record from the array list
                searchHistory.remove(position);
                //Notify the adapter about the change
                listViewSearchesAdapter.notifyDataSetChanged();
            });
            dialogBuilder.show();

            return true;
        });
    }

    /**
     * Method called when the current application is being re-displayed to the user after onStop()
     * has being called
     * */
    @Override
    protected void onRestart() {
        super.onRestart();

        //List to be loaded with the database content
        searchHistory = new ArrayList<>();
        //load the song list previously searched from the database and put it into the array list
        loadFromDatabase();
        //starts the adapter to handle the list view items again
        listViewSearchesAdapter = new ListAdapter(searchHistory, getApplicationContext());
        //sets the adapter to the list view
        searchedSongList.setAdapter(listViewSearchesAdapter);
        listViewSearchesAdapter.notifyDataSetChanged();
    }

    /**
     * Method to specify the options menu icons for the toolbar. It will inflate in the toolbar
     * the menu layout resource created.
     *
     * @param menu provided in the callback
     * @return true in order to show the menu inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        //Inflate the menu with a layout created for it
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    /**
     * Method to handle the functionality of the options(icons) in the toolbar
     *
     *  @param item icons in the toolbar
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Instance to go to the next activity
        Intent browserIntent;
        //String holds the message to be displayed
        String message;

        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId()) {
            case R.id.toolbar_linkedin:
                //Sets the intent to be passed as argument to the startActivity method
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/andressa-machado-59705792/"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
            case R.id.toolbar_github:
                //Sets the intent to be passed as argument to the startActivity method
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andressamachado"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
            case R.id.toolbar_about:
                message = "This is the Song Lyrics Search activity written by Andressa Machado";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    /**
     * Method needed for the OnNavigationItemSelected interface. It is going to handle every option
     * listed at the navigation drawer. It is called when an item in the navigation menu is selected.
     *
     * @param item selected item in the menu
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId()) {
            case R.id.drawer_instructions:
                buildAndDisplayAlertDialog();
                break;
            case R.id.drawer_about_api:
                //Sets the intent to be passed as argument to the startActivity method
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lyricsovh.docs.apiary.io/#"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
            case R.id.drawer_github:
                //Sets the intent to be passed as argument to the startActivity method
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andressamachado"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
            case R.id.drawer_linkedin:
                //Sets the intent to be passed as argument to the startActivity method
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/andressa-machado-59705792/"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
        }

        //Close the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    /**
     * Changes the layout configuration of the MainActivity to the dark mode
     * */
    private void setModeDark() {
        //Black background
        view.setBackgroundColor(Color.rgb(1,0,0));
        //new image to the list view
        searchedSongList.setBackgroundResource(R.drawable.darkmode);
        //title color to purple
        appTitle.setTextColor(Color.rgb(154, 98, 121));
        //black background for the Google image button
        googleButton.setBackgroundColor(Color.rgb(1,0,0));
    }

    /**
     * Changes the layout configuration of the MainActivity to the light mode
     * */
    private void setModeLight() {
        //White background
        view.setBackgroundColor(Color.WHITE);
        //Standard image for the list view background
        searchedSongList.setBackgroundResource(R.drawable.background);
        //App title to black
        appTitle.setTextColor(Color.BLACK);
        //Google image button background to white to matches the app background
        googleButton.setBackgroundColor(Color.WHITE);
    }

    /**
     * Method to perform the song search from the API
     *
     * @see "lyricsovh.docs.apiary.io/#"
     * */
    private void performSearch() {
        //Checks if the input from the user is correct
        if (!validateInput()) {
            return;
        }

        //encode the string entered to match the search pattern
        encodeString();
        //String with the url to be used in the search
        String requestURL = "https://api.lyrics.ovh/v1/" + artist + "/" + song;
        //Checks if the song exists in the API
        requestFromAPI(requestURL);
    }

    /**
     * Method to perform a search using the google platform with the data entered by the user
     *
     * @see "https://www.google.com/"
     * */
    private void performGoogleSearch() {
        //Checks if the input is valid
        if (!validateInput()) {
            return;
        }

        //Performs search in the web
        searchWeb(artistFieldInput.getText().toString().trim() + " " + songFieldInput.getText().toString().trim());
    }

    /**
     * Method to encode input from the user to perform a successful search.
     * Uses the URLEncoder java class to perform the operation
     *
     * @see java.net.URLEncoder
     * */
    private void encodeString() {
        //Try/catch to handle exceptions
        try {
            //put encoded values into variables
            artist = URLEncoder.encode(artistFieldInput.getText().toString(), "UTF-8");
            song = URLEncoder.encode(songFieldInput.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to secure that the user entered a string and not just pressed enter
     *
     * @return true when user input is valid
     */
    private boolean validateInput() {
        //array to hold which field is not valid. Can be just artist, just song, or both fields
        ArrayList<String> message = new ArrayList<>();
        boolean valid = true;

        //If user pressed enter before write the name of the artist, is not a valid input
        if (artistFieldInput.getText().toString().trim().equals("")) {
            message.add("Artist");
            valid = false;
        }

        //If user pressed enter before write the title of the song, is not a valid input
        if (songFieldInput.getText().toString().trim().equals("")) {
            message.add("Song");
            valid = false;
        }

        //if the input is not valid, display snack bar saying that the artist field, the song field
        // or both fields cannot be empty
        if (!valid) {
            minimizeKeyboard();
            loadingDialog.dialogDismiss();
            displaySnackbar(message);
        }

        return valid;
    }

    /**
     * Method to minimize keyboard when it is not necessary
     *
     * @see android.view.inputmethod.InputMethodManager
     */
    private void minimizeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Method to display a snack bar with a button
     *
     * @param message containing the error message to be displayed
     */
    private void displaySnackbar(ArrayList<String> message) {
        //Instance of snack bar with a message error saying that the user entered an invalid input. Fields cannot be
        //empty. It receives an array of strings with the error message.
        Snackbar snackbar = Snackbar.make(searchButton, TextUtils.join(" and ", message) + " field cannot be empty", Snackbar.LENGTH_INDEFINITE)
                .setAction("oops", v -> {
                }).setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * Method to display a toast displaying an error message if the song is not found
     * */
    private void displayNotFoundToast() {
        minimizeKeyboard();
        Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_error_message), Toast.LENGTH_LONG).show();
    }

    /**
     * Method to request the song from the API
     *
     * @param requestURL string containing an URL to be used in the search
     */
    private void requestFromAPI(String requestURL) {
        //Instance of the Inner class SongQuery to perform the request
        SongQuery req = new SongQuery();
        req.execute(requestURL);
    }

    /**
     * Method to build and display a custom alert dialog with the instructions about using the
     * application
     * */
    private void buildAndDisplayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Inflate the custom dialog to the view
        View customAlertView = getLayoutInflater().inflate(R.layout.info_alert_dialog_layout, null, false);

        //sets title
        TextView title = customAlertView.findViewById(R.id.alert_title);
        title.setText(R.string.info_alert_dialog_title);

        //sets message
        TextView instructions = customAlertView.findViewById(R.id.alert_instructions);
        instructions.setText(R.string.info_alert_dialog_message);

        //sets positive button to close the dialog
        builder.setPositiveButton(R.string.alert_dialog_positive_btn, (click, arg) -> {
        }).setView(customAlertView).show();
    }

    /**
     * Method to pass the lyric data to the next activity to be displayed
     *
     * @param lyrics containing song lyric gathered from the JSON object
     * @param id song id
     * @param songTitle song title used in the search
     * @param artistName artist name used in the search
     */
    private void sendLyricToNextActivity(String lyrics, long id, String songTitle, String artistName) {
        //Intent from this to the next activity
        Intent goToSongPage = new Intent(MainActivity.this, SongLyricActivity.class);
        //Puts data into the intent instance
        goToSongPage.putExtra("lyric", lyrics);
        goToSongPage.putExtra("id", id);
        goToSongPage.putExtra("song info", songTitle + " - " + artistName);

        //Holds if the song was set to be favorite
        boolean isFavorite = currentSearchPosition != -1 && searchHistory.get(currentSearchPosition).isFavorite();
        goToSongPage.putExtra("isFavorite", isFavorite );
        //starts the new activity
        startActivity(goToSongPage);
    }

    /**
     * Method to perform a web search for the song entered.
     *
     * @see android.content.Intent and android.app.SearchManager
     * @param query url to be used in the search
     */
    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        //sends url query to be used in the search.
        //If the query starts with http or https the site will be open, but if it is a plain text
        // (which is the case) a google search will be applied
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Method to go throw the database collecting data and saving the values into the array that controls
     * the song entries inside the activity
     *
     * @see android.database.Cursor
     * */
    private void loadFromDatabase() {
        //Instance of the SongLyricsDBOpener class that maneges the database fot this application
        SongLyricsDBOpener dbOpener = new SongLyricsDBOpener(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {SongLyricsDBOpener.COL_SEARCH_ID, SongLyricsDBOpener.COL_ARTISTS, SongLyricsDBOpener.COL_SONGS, SongLyricsDBOpener.COL_IS_FAVORITE};
        //This instance of the Cursor interface provides random read-write access to the result set
        // returned by a database query.
        Cursor results = db.query(false, SongLyricsDBOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //method created to control the results of the cursor
        printCursor(results, db.getVersion());

        //Hold the column indexes
        int colSearchID = results.getColumnIndex(SongLyricsDBOpener.COL_SEARCH_ID);
        int colArtist = results.getColumnIndex(SongLyricsDBOpener.COL_ARTISTS);
        int colSong = results.getColumnIndex(SongLyricsDBOpener.COL_SONGS);
        int colIsFavorite = results.getColumnIndex(SongLyricsDBOpener.COL_IS_FAVORITE);

        if (results.moveToFirst()){
            //while it is not at the last row of the database, continue performing actions
            while(!results.isAfterLast()){
                //get values from the entries in the database
                long searchIndex = results.getLong(colSearchID);
                String artistName = results.getString(colArtist);
                String songTitle = results.getString(colSong);
                Boolean isFavorite = results.getInt(colIsFavorite) == 1;

                //add to the array list
                searchHistory.add(new LyricSearch(searchIndex, artistName, songTitle, isFavorite));
                //moves one row forward
                results.moveToNext();
            }
        }
    }

    /**
     * Method created to keep track of the cursor results. Just for debugging purpose
     *
     * @see android.database.Cursor
     * */
    private void printCursor(Cursor results, int version) {
        Log.i(TAG, "Database Version Number: " +  db.getVersion());
        Log.i(TAG, "Number of Columns in the cursor: " + results.getColumnCount());

        int colMessageID = results.getColumnIndex(SongLyricsDBOpener.COL_SEARCH_ID);
        int colMessageText = results.getColumnIndex(SongLyricsDBOpener.COL_ARTISTS);
        int colMessageSend = results.getColumnIndex(SongLyricsDBOpener.COL_SEARCH_ID);

        Log.i(TAG, "Name of the Columns in the cursor: " + results.getColumnName(colMessageID) +
                ", " + results.getColumnName(colMessageText) + ", " + results.getColumnName(colMessageSend));

        Log.i(TAG, "Number of rows in the cursor: " + results.getCount());

        long messageIndex;
        String messageText;
        boolean messageSend;

        if (results.moveToFirst()){
            while(!results.isAfterLast()){
                messageIndex = results.getLong(colMessageID);
                messageText = results.getString(colMessageText);
                messageSend = results.getInt(colMessageSend) > 0;

                Log.i(TAG, "Row of results in the cursor: " + messageIndex +
                        ", " + messageText + ", " + messageSend);
                results.moveToNext();
            }
        }
        results.moveToFirst();
    }

    /**
     * This inner class inherits from BaseAdapter and is responsible for the adapter functionality
     * in the SongLyricsSearchActivity. It basically performs actions in an array list
     *
     * @author Andressa Machado
     * @version 1.0
     * @see android.widget.Adapter
     * @since 2020/08/17
     */
    protected class ListAdapter extends BaseAdapter {
        /**Array containing previously song search by the user */
        private ArrayList<LyricSearch> searchHistory;

        /**Constructor setting the array and the context of this adapter*/
        public ListAdapter(ArrayList<LyricSearch> searchHistory, Context context) {
            this.searchHistory = searchHistory;
        }

        /**
         * Method to return the size of the list
         *
         * @return size of the array list
         */
        @Override
        public int getCount() {
            return searchHistory.size();
        }

        /**
         * Method to return an specific item in the list
         *
         * @param position array list element index
         * @return the element in the given position
         */
        @Override
        public Object getItem(int position) {
            return searchHistory.get(position);
        }

        /**
         * Method to get the id of an element in the array
         *
         * @param position array list element index
         * @return position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Method to handle every item in the list view. It displays song info
         * and also if the song was listed as favorite by the user by displaying an
         * image of a full yellow star
         *
         * @param position array list element index
         * @param convertView view to be used in the process
         * @param parent view to be inflated with the desired view
         * @return view relater to the element passed as argument
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView = null;

            //If the view is not null, inflate the XML layout for the item in the list view
            if (convertView == null) {
                newView = getLayoutInflater().inflate(R.layout.song_lyrics_listview_item, parent, false);
            } else {
                return convertView;
            }

            //Get the image view where will be displayed the star indicating if the song
            //was listed as favorite
            ImageView imgIconFavorite = (ImageView) newView.findViewById(R.id.favorite_icon);

            //get the song to be displayed at that location
            LyricSearch thisRow = (LyricSearch) getItem(position);

            //If the song is favorite, put a full star
            if (thisRow.isFavorite()){
                imgIconFavorite.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_full_star));
            }

            //Get the text view where the song info will be displayed
            TextView rowSearch = newView.findViewById(R.id.song_searched);

            //Strings to hold the song info
            String songPrint = thisRow.getSongTitle().substring(0, 1).toUpperCase() + thisRow.getSongTitle().substring(1).toLowerCase();
            String concatenation = getString(R.string.concatenation_song_searched_info);
            String artistPrint = thisRow.getArtist().substring(0, 1).toUpperCase() + thisRow.getArtist().substring(1).toLowerCase();
            //String made by concatenating the previous ones to display in the text view
            String songInfoToBeDisplayed = songPrint + " " + concatenation + " " + artistPrint;

            rowSearch.setText(songInfoToBeDisplayed);

            return newView;
        }
    }

    /**
     * This inner class inherits from AsyncTask and it is responsible for the tasks that runs on a
     * background thread and whose result is published on the UI thread. An asynchronous task is
     * defined by 3 generic types, called Params, Progress and Result, and 4 steps,
     * called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
     *
     * @author Andressa Machado
     * @version 1.0
     * @see "android.os.AsyncTask<Params, Progress, Result>"
     * @since 2020/08/17
     */
    protected class SongQuery extends AsyncTask<String, Integer, String> {

        /**
         * Method invoked on the background thread immediately after onPreExecute() finishes executing.
         *
         * @param strings variable arguments
         * @return a string object representing the results of the processing
         * @see java.io.InputStream and org.json.JSONObject
         */
        @Override
        protected String doInBackground(String... strings) {
            InputStream response;

            //Try to get connected with the API and get a new Json object from this connection
            try {
                response = connectToAPI(strings[0]);

                String result = convertResponseToString(response);
                JSONObject lyricsObject = new JSONObject(result);

                //save the lyric got from the api to the lyric string
                lyrics = getLyricsFromJSON(lyricsObject);
                //Sets the progress bar update progress
                onProgressUpdate(50);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Method to be invoked on the UI thread after a call to publishProgress(Progress...).
         * The timing of the execution is undefined. This method is used to display any form of
         * progress in the user interface while the background computation is still executing.
         *
         * @param args integers representing the progress of your processing
         */
        public void onProgressUpdate(Integer ... args){
            loadingDialog.setProgress(args[0]);
        }

        /**
         *  Method to be invoked on the UI thread after the background computation finishes. The result of the
         *  background computation is passed to this step as a parameter.
         *
         * @param fromDoInBackground object returned by doInBackground
         */
        public void onPostExecute(String fromDoInBackground) {
            if (lyrics == null) {
                //at this point the song was not found
                displayNotFoundToast();
                //progress bar concludes
                onProgressUpdate(100);
                //close the dialog with the progress bar
                loadingDialog.dialogDismiss();
            } else {
                //Song was found
                String currentArtist = artistFieldInput.getText().toString().toLowerCase();
                String currentSong = songFieldInput.getText().toString().toLowerCase();

                LyricSearch current;
                //boolean to control if the song was already searched before, so it is not added to the database nor the list
                boolean exists = false;

                for (int i = 0; i < searchHistory.size(); i++){
                    current = searchHistory.get(i);

                    if(current.getArtist().equalsIgnoreCase(currentArtist) && current.getSongTitle().equalsIgnoreCase(currentSong)){
                        exists = true;
                        break;
                    }
                }

                long newId = -1;

                //if the song was not searched before, it is added to the database and the array list
                if(!exists) {
                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(SongLyricsDBOpener.COL_ARTISTS, currentArtist);
                    newRowValues.put(SongLyricsDBOpener.COL_SONGS, currentSong);
                    newRowValues.put(SongLyricsDBOpener.COL_IS_FAVORITE, 0);

                    //Inserts in the database
                    newId = db.insert(SongLyricsDBOpener.TABLE_NAME, null, newRowValues);

                    //Creates a new search object and add it to the array list
                    LyricSearch currentSearch = new LyricSearch(newId, currentArtist, currentSong, false);
                    searchHistory.add(currentSearch);
                } else{
                    //if song was already searched before, search for it in the data base and performs the search again
                    //do not save to the database nor the array list that controls the list os songs searched in this
                    //activity
                    String[] args = new String[] {currentArtist, currentSong};
                    Cursor result =  db.rawQuery("SELECT _id FROM " + SongLyricsDBOpener.TABLE_NAME + " WHERE " + SongLyricsDBOpener.COL_ARTISTS + " = ?  AND " + SongLyricsDBOpener.COL_SONGS + " = ?",args);

                    int colSearchID = result.getColumnIndex(SongLyricsDBOpener.COL_SEARCH_ID);

                    if (result.moveToFirst()){
                        while(!result.isAfterLast()){
                            newId =  result.getLong(colSearchID);
                            result.moveToNext();
                        }
                    }
                }
                if(newId == -1) {
                    Log.i("ERROR", "select problem: ");
                    return;
                }

                listViewSearchesAdapter.notifyDataSetChanged();
                Bundle dataToPass = new Bundle();

                dataToPass.putString(ARTIST_NAME, artist);
                dataToPass.putString(SONG_TITLE, song);
                dataToPass.putString(LYRIC, lyrics);
                dataToPass.putLong("id", newId);

                //if the device is a tablet, open the lyric page as a fragment instead of opening a new page
                if (isTablet){
                    DetailFragment dFragment = new DetailFragment();

                    dFragment.setArguments(dataToPass);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation, dFragment)
                            .commit();
                } else {
                    //Device is a smartphone
                    sendLyricToNextActivity(lyrics, newId, currentSong, currentArtist);
                }

                //reset the input fields
                artistFieldInput.setText("");
                songFieldInput.setText("");

                //concludes the progress bar
                onProgressUpdate(100);

                currentSearchPosition = -1;
                loadingDialog.dialogDismiss();
            }
        }

        /**
         * Method to read the result from the API and save it, returning the lyric as a string
         *
         * @param response InputStream
         * @return string containing the content inside the lyric tag inside the JSON object
         * @throws IOException
         * @see java.io.InputStream
         */
        private String convertResponseToString(InputStream response) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
            StringBuilder sb = new StringBuilder();

            //While it is a line, continue reading and appending to the string
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            //return a string containing the lyric complete
            return sb.toString();
        }

        /**
         * * Method to connect the application with the API, using the URL of the API
         *
         * @param string url to be used in the connection
         * @return an input stream
         * @throws IOException
         * @see java.io.InputStream, java.net.HttpURLConnection, and  java.net.URL
         */
        private InputStream connectToAPI(String string) throws IOException {
            InputStream response;

            URL url = new URL(string);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                response = urlConnection.getErrorStream();
            } else {
                response = urlConnection.getInputStream();
            }

            return response;
        }

        /**
         * Method to return the lyric tag found in the JSON object
         *
         * @param lyricsObject JSON object from the successful connection
         * @return string containing tag lyric if tag was found or error if not
         * @throws JSONException
         * @see "javax.json"
         */
        private String getLyricsFromJSON(JSONObject lyricsObject) throws JSONException {
            if (lyricsObject.has("lyrics")) {
                //debug purpose
                Log.i("SongLyricsSearch", "lyric found");

                return lyricsObject.getString("lyrics");
            } else {
                String error = lyricsObject.getString("error");
                Log.i("SongLyricsSearch", error);

                return null;
            }
        }
    }

    /**
     * This inner class was made with the purpose of create an alert dialog with a progress bar
     * in it. As the application has already a loot of information in its interface, that was the
     * best way I could find to add this feature to the application without making a mess
     *
     * @author Andressa Machado
     * @version 1.0
     * @since 2020/08/05
     */
    protected class LoadingDialog {
        /**Where the alert dialog will appear*/
        Activity activity;
        /**Alert dialog to be shown*/
        AlertDialog dialog;
        /**Progress bar to be inserted inside the dialog*/
        ProgressBar progressBar;

        LoadingDialog(Activity activity) {
            this.activity = activity;
        }

        /**
         * Method to build, inflate with a custom layout and display the alert dialog
         * */
        void startLoadingDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View currentView = inflater.inflate(R.layout.progress_dialog_layout, null, false);

            builder.setView(currentView);
            progressBar = currentView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            dialog = builder.create();
            dialog.show();
        }

        /**
         * Method to close the dialog
         * */
        void dialogDismiss() {
            Handler handler = new Handler();
            handler.postDelayed(() -> dialog.dismiss(),500);
        }

        /**Sets the progress*/
        void setProgress(int arg) {
            progressBar.setProgress(arg);
        }
    }
}