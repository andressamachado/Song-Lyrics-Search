package com.andressamachado.songlyricssearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**XML layout elements declarations*/
    Toolbar toolbar;
    DrawerLayout drawer;
    View view;
    TextView appTitle;
    Switch modeSwitch;
    EditText artistFieldInput;
    EditText songFieldInput;
    Button searchButton;
    ImageButton googleButton;
    TextView needHelp;
    ListView searchedSongList;

    /**Builds the alert dialog*/
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search_page_activity);

        initializeActivity();
        setClickListeners();
    }

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

        //builder fot the alert dialog
        dialogBuilder = new AlertDialog.Builder(this);
    }

    private void setClickListeners(){
        //Listener to the text view need help
        needHelp.setOnClickListener(v -> {
            //displays a alert dialog with information about the application
            buildAndDisplayAlertDialog();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        //Inflate the menu with a layout created for it
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
}
