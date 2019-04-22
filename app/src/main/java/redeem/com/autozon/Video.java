package redeem.com.autozon;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Video extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{
    YouTubePlayerView youTubePlayerView;
    String link;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            link = bundle.getString("link");
        }
        youTubePlayerView = findViewById(R.id.video);
        try {
            youTubePlayerView.initialize("AIzaSyBabnq7etJ9ILcz7KXe2Q1T8D2wO97ItVE", this);//GIVE API HERE
        }
        catch (Throwable e)
        {
            Log.d("check", e.toString());
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        try {
            youTubePlayer.loadVideo(link);//GIVE URL HERE
        }
        catch (Throwable e)
        {
            Log.d("check", e.toString());
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
        this.finish();
    }
}
