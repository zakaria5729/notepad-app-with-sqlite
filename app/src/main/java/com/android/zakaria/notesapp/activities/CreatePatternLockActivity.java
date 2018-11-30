package com.android.zakaria.notesapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.preferences.MyPreferences;

import java.util.List;

public class CreatePatternLockActivity extends AppCompatActivity {

    private MyPreferences myPreferences;
    private PatternLockView patternLockView;
    private Button createPatternBtn, skipPatternBtn;

    private EditText emailEditText;
    private String forgotPattern, patternLock = "";
    private TextView patternTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pattern_lock);

        myPreferences = MyPreferences.getMyPreferences(this);
        if (myPreferences.getSkipLockPattern().equals("skip")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        emailEditText = findViewById(R.id.emailEdt);
        patternTextView = findViewById(R.id.patternTextView);
        createPatternBtn = findViewById(R.id.createPatternBtn);
        skipPatternBtn = findViewById(R.id.skipPatternBtn);
        patternLockView = findViewById(R.id.pattern_lock_view);

        getCreatePatternOrForgotPattern();
    }

    private void getCreatePatternOrForgotPattern() {
        forgotPattern = getIntent().getStringExtra("forgot_pattern_key");

        if (forgotPattern.equals("forgot_pattern")) {
            emailEditText.addTextChangedListener(myTextWatcher);

        } else if (forgotPattern.equals("create_pattern")) {
            patternTextView.setVisibility(View.VISIBLE);
            patternLockView.setVisibility(View.VISIBLE);
            skipPatternBtn.setVisibility(View.VISIBLE);

            patternLockView.addPatternLockListener(patternLockViewListener);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
    }

    public void createLockPattern(View view) {
        if (forgotPattern.equals("forgot_pattern")) {
            if (emailEditText.getText().toString().trim().equals(myPreferences.getEmailForLockPattern()) && patternLock.length() > 3) {
                myPreferences.setEmailForLockPattern(emailEditText.getText().toString().trim());
                myPreferences.setLockPattern(patternLock);

                jumpToInputPatternActivity();
            } else {
                Toast.makeText(this, "Invalid email text or pattern.", Toast.LENGTH_SHORT).show();
            }

        } else if (forgotPattern.equals("create_pattern")) {
            if (!emailEditText.getText().toString().trim().isEmpty() && patternLock.length() > 3) {
                myPreferences.setEmailForLockPattern(emailEditText.getText().toString().trim());
                myPreferences.setLockPattern(patternLock);

                jumpToInputPatternActivity();
            } else {
                Toast.makeText(this, "Invalid email text or pattern.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private PatternLockViewListener patternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            if (pattern.size() >= 4) {
                patternTextView.setText(R.string.correct_pattern);
                patternLockView.setCorrectStateColor(ResourceUtils.getColor(CreatePatternLockActivity.this, R.color.colorCorrectPattern));
                createPatternBtn.setVisibility(View.VISIBLE);
                patternLock = PatternLockUtils.patternToString(patternLockView, pattern);

            } else {
                createPatternBtn.setVisibility(View.INVISIBLE);
                patternLockView.setWrongStateColor(ResourceUtils.getColor(CreatePatternLockActivity.this, R.color.colorAccent));
                patternTextView.setText(R.string.wrong_pattern);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        patternTextView.setText(R.string.create_new_pattern);
                    }
                }, 1000);
                pattern.clear();
            }
        }

        @Override
        public void onCleared() {
        }
    };

    public void skipCreateLockPattern(View view) {
        if (forgotPattern.equals("forgot_pattern")) {
            myPreferences.setSkipLockPattern("skip");
        } else if (forgotPattern.equals("create_pattern")) {
            myPreferences.setSkipLockPattern("skip");
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private final TextWatcher myTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (myPreferences.getEmailForLockPattern().equals(emailEditText.getText().toString())) {
                patternTextView.setVisibility(View.VISIBLE);
                patternLockView.setVisibility(View.VISIBLE);
                skipPatternBtn.setVisibility(View.VISIBLE);
                createPatternBtn.setVisibility(View.VISIBLE);
                patternLockView.addPatternLockListener(patternLockViewListener);

                patternTextView.setText(R.string.create_new_pattern);
            } else {
                patternTextView.setVisibility(View.INVISIBLE);
                patternLockView.setVisibility(View.INVISIBLE);
                skipPatternBtn.setVisibility(View.INVISIBLE);
                createPatternBtn.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void jumpToInputPatternActivity() {
        Intent intent = new Intent(this, InputPatternActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
