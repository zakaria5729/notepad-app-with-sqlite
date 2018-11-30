package com.android.zakaria.notesapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    private static MyPreferences myPreferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private MyPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(ConfigPref.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public static MyPreferences getMyPreferences(Context context) {
        if (myPreferences == null) {
            myPreferences = new MyPreferences(context);
        }
        return myPreferences;
    }

    public void setViewChange(boolean isViewChange) {
        editor.putBoolean(ConfigPref.VIEW_CHANGE, isViewChange);
        editor.apply();
    }

    public boolean isViewChange() {
        return sharedPreferences.getBoolean(ConfigPref.VIEW_CHANGE, false);
    }

    public void setFontSize(int fontSize) {
        editor.putInt(ConfigPref.FONT_SIZE_CHANGE, fontSize);
        editor.apply();
    }

    public int getFontSize() {
        return sharedPreferences.getInt(ConfigPref.FONT_SIZE_CHANGE, 1); //by default font size medium = 1
    }

    public void setBackgroundColor(int backgroundColor) {
        editor.putInt(ConfigPref.BACKGROUND_COLOR_CHANGE, backgroundColor);
        editor.apply();
    }

    public int getBackgroundColor() {
        return sharedPreferences.getInt(ConfigPref.BACKGROUND_COLOR_CHANGE, -1);
    }

    public void setCurrentlySelectedColorBtn(String currentlySelectedColorBtn) {
        editor.putString(ConfigPref.CURRENTLY_SELECTED_COLOR_BTN, currentlySelectedColorBtn);
        editor.apply();
    }

    public String getCurrentlySelectedColorBtn() {
        return sharedPreferences.getString(ConfigPref.CURRENTLY_SELECTED_COLOR_BTN, "bgButtonCng11");
    }

    public void setLockPattern(String lockPattern) {
        editor.putString(ConfigPref.PATTERN_LOCK, lockPattern);
        editor.apply();
    }

    public String getLockPattern() {
        return sharedPreferences.getString(ConfigPref.PATTERN_LOCK, "");
    }

    public void setEmailForLockPattern(String emailForLockPattern) {
        editor.putString(ConfigPref.EMAIL_OR_TEXT_FOR_PATTERN_LOCK, emailForLockPattern);
        editor.apply();
    }

    public String getEmailForLockPattern() {
        return sharedPreferences.getString(ConfigPref.EMAIL_OR_TEXT_FOR_PATTERN_LOCK, "");
    }

    public void setSkipLockPattern(String skipLockPattern) {
        editor.putString(ConfigPref.SKIP_PATTERN_LOCK, skipLockPattern);
        editor.apply();
    }

    public String getSkipLockPattern() {
        return sharedPreferences.getString(ConfigPref.SKIP_PATTERN_LOCK, "");
    }

    public void setHidePatternDraw(String hidePatternDraw) {
        editor.putString(ConfigPref.HIDE_PATTERN_DRAW, hidePatternDraw);
        editor.apply();
    }

    public String getHidePatternDraw() {
        return sharedPreferences.getString(ConfigPref.HIDE_PATTERN_DRAW, "");
    }

    public void setVibratePatternDraw(String vibratePatternDraw) {
        editor.putString(ConfigPref.VIBRATE_PATTERN_DRAW, vibratePatternDraw);
        editor.apply();
    }

    public String getVibratePatternDraw() {
        return sharedPreferences.getString(ConfigPref.VIBRATE_PATTERN_DRAW, "");
    }

    public void setDisableShareNote(String shareNote) {
        editor.putString(ConfigPref.SHARE_NOTE_DISABLE, shareNote);
        editor.apply();
    }

    public String getDisableShareNote() {
        return sharedPreferences.getString(ConfigPref.SHARE_NOTE_DISABLE, "");
    }
}
