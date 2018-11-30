package com.android.zakaria.notesapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.preferences.MyPreferences;

public class PatternAndOtherSettingActivity extends AppCompatActivity {

    private SwitchCompat enablePattern, hidePatternDraw, vibratingPatternDraw, disableNoteSharing;
    private MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_and_other_setting);

        enablePattern = findViewById(R.id.enablePattern);
        hidePatternDraw = findViewById(R.id.hidePatternDraw);
        vibratingPatternDraw = findViewById(R.id.vibratePatternDraw);
        disableNoteSharing = findViewById(R.id.enableNoteSharing);

        myPreferences = MyPreferences.getMyPreferences(this);

        getPatternSwitchCurrentState();
        getHidePatternSwitchCurrentState();
        getVibratePatternSwitchCurrentState();
        getNoteSharingSwitchCurrentState();

        enablePattern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myPreferences.setLockPattern("");
                    myPreferences.setSkipLockPattern("");
                    myPreferences.setEmailForLockPattern("");
                } else {
                    myPreferences.setSkipLockPattern("skip");
                }
            }
        });

        hidePatternDraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!myPreferences.getSkipLockPattern().equals("skip")) {
                    if (isChecked) {
                        myPreferences.setHidePatternDraw("hide");
                    } else {
                        myPreferences.setHidePatternDraw("");
                    }
                } else {
                    Toast.makeText(PatternAndOtherSettingActivity.this, "Pattern switch enable first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vibratingPatternDraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!myPreferences.getSkipLockPattern().equals("skip")) {
                    if (isChecked) {
                        myPreferences.setVibratePatternDraw("vibrate");
                    } else {
                        myPreferences.setVibratePatternDraw("");
                    }
                } else {
                    Toast.makeText(PatternAndOtherSettingActivity.this, "Pattern switch enable first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disableNoteSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myPreferences.setDisableShareNote("disable");
                } else {
                    myPreferences.setDisableShareNote("");
                }
            }
        });
    }

    private void getPatternSwitchCurrentState() {
        if (!myPreferences.getSkipLockPattern().equals("skip")) {
            enablePattern.setChecked(true);
        } else {
            enablePattern.setChecked(false);
        }
    }

    private void getHidePatternSwitchCurrentState() {
        if (myPreferences.getHidePatternDraw().equals("hide")) {
            hidePatternDraw.setChecked(true);
        } else {
            hidePatternDraw.setChecked(false);
        }
    }

    private void getVibratePatternSwitchCurrentState() {
        if (myPreferences.getVibratePatternDraw().equals("vibrate")) {
            vibratingPatternDraw.setChecked(true);
        } else {
            vibratingPatternDraw.setChecked(false);
        }
    }

    private void getNoteSharingSwitchCurrentState() {
        if (myPreferences.getDisableShareNote().equals("disable")) {
            disableNoteSharing.setChecked(true);
        } else {
            disableNoteSharing.setChecked(false);
        }
    }

    @Override
    public boolean onNavigateUp() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onRestart() {
        if (!MyPreferences.getMyPreferences(this).getSkipLockPattern().equals("skip")) {
            Intent intent = new Intent(this, InputPatternActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        super.onRestart();
    }
}
