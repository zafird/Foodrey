package ca.bcit.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private TextView mLoadingText;

    private int mProgressStatus = 0;

    private Handler mHadler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mProgressBar = findViewById(R.id.progressBar);
        mLoadingText = findViewById(R.id.tvLoadingComplete);
        mLoadingText.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mProgressStatus < 100){
                    mProgressStatus += 5;
                    android.os.SystemClock.sleep(100);

                    mHadler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress((mProgressStatus));
                        }
                    });
                    mHadler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingText.setVisibility(View.VISIBLE);

                        }
                    });
                    if(mProgressStatus > 95){
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }).start();

    }

}
