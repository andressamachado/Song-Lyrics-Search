package com.andressamachado.songlyricssearch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/******************************************************************************************
 * This class is responsible for the splash screen functionality of the application. It extends
 * AppCompatActivity to adjusts newer platform features on older devices.
 *
 * @author Andressa Machado
 * @version 1.0
 * @see AppCompatActivity
 * @since 2020/08/19
 ******************************************************************************************/
public class SplashScreen extends AppCompatActivity {
    //Constant holding the period of time that the splash screen will be visible before going to the MainActivity
    //4 seconds in this case
    private static final int SPLASH_SCREEN = 4000;

    /**XML layout elements declarations*/
    Animation topAnimation, bottomAnimation;
    TextView title, subtitle;
    ImageView image;

    /**
     * OnCreate method is a bundle where the activity is initialized. It is called when the
     * application is not loaded yet. Here is performed basic application startup logic that
     * should happen only once for the entire life of the activity
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the splash screen to be full screen, not using the action bar that is placed by default by android
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        //Initializing animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Initializing every view used in this activity
        image = findViewById(R.id.splash_scree_img);
        title = findViewById(R.id.splash_scree_title);
        subtitle = findViewById(R.id.splash_scree_subtitle);

        //Setting animation
        image.setAnimation(bottomAnimation);
        title.setAnimation(topAnimation);
        subtitle.setAnimation(topAnimation);

        //4seconds past, run next application
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goToApplication = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(goToApplication);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}
