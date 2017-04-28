package com.smart.common;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartprime.R;
import com.smart.customviews.EasyVideoCallback;
import com.smart.customviews.EasyVideoPlayer;
import com.smart.framework.AlertNeutral;
import com.smart.framework.SmartUtils;

public class VideoPlayer extends AppCompatActivity implements EasyVideoCallback {

    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        player = (EasyVideoPlayer) findViewById(R.id.player);
        player.setSource(Uri.parse(getIntent().getStringExtra("IN_VIDEO_URL")));
        player.setSubmitText(getString(R.string.close));
        assert player != null;
        player.setCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        player.pause();
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onPreparing()");
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onPrepared()");
    }

    @Override
    public void onBuffering(int percent) {
        Log.d("EVP-Sample", "onBuffering(): " + percent + "%");
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        Log.d("EVP-Sample", "onError(): " + e.getMessage());
        SmartUtils.getOKDialog(VideoPlayer.this, getString(R.string.error), e.getMessage(),
                getString(R.string.ok), true, new AlertNeutral() {
                    @Override
                    public void NeutralMathod(DialogInterface dialog, int id) {

                    }
                });
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onCompletion()");
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

        supportFinishAfterTransition();
    }
}