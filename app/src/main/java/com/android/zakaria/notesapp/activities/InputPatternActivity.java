package com.android.zakaria.notesapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.preferences.MyPreferences;

import java.util.List;

public class InputPatternActivity extends AppCompatActivity {

    private MyPreferences myPreferences;
    private PatternLockView patternLockView;
    private TextView patternTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pattern_lock);

        myPreferences = MyPreferences.getMyPreferences(this);

        if (myPreferences.getSkipLockPattern().equals("skip")) {
            Intent intent = new Intent(InputPatternActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else if (myPreferences.getLockPattern().equals("")) {
            Intent intent = new Intent(InputPatternActivity.this, CreatePatternLockActivity.class);
            intent.putExtra("forgot_pattern_key", "create_pattern");
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        patternTextView = findViewById(R.id.patternTextView);
        patternLockView = findViewById(R.id.pattern_lock_view);

        if (myPreferences.getHidePatternDraw().equals("hide")) {
            patternLockView.setInStealthMode(true); //enable hide pattern drawing
        } else {
            patternLockView.setInStealthMode(false); //disable hide pattern drawing
        }

        if (myPreferences.getVibratePatternDraw().equals("vibrate")) {
            patternLockView.setTactileFeedbackEnabled(true); //enable vibration pattern drawing
        } else {
            patternLockView.setTactileFeedbackEnabled(false); // disable vibration pattern drawing
        }

        patternLockView.addPatternLockListener(patternLockViewListener);
    }

    private PatternLockViewListener patternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
        }
        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            String patternLock = PatternLockUtils.patternToString(patternLockView, pattern);

            if (patternLock.equals(myPreferences.getLockPattern())) {
                patternTextView.setText(R.string.correct_pattern);
                patternLockView.setCorrectStateColor(ResourceUtils.getColor(InputPatternActivity.this, R.color.colorCorrectPattern));

                Intent intent = new Intent(InputPatternActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            } else {
                patternLockView.setWrongStateColor(ResourceUtils.getColor(InputPatternActivity.this, R.color.colorAccent));
                patternTextView.setText(R.string.wrong_pattern);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        patternTextView.setText(R.string.input_pattern);
                    }
                }, 1000);
            }
            pattern.clear();
        }

        @Override
        public void onCleared() {
        }
    };

    public void forgotPatternPopUp(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you forgot your pattern?");
        builder.setIcon(R.drawable.pattern_lock_icon);
        builder.setPositiveButton("Forgot", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(InputPatternActivity.this, CreatePatternLockActivity.class);
                intent.putExtra("forgot_pattern_key", "forgot_pattern");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
