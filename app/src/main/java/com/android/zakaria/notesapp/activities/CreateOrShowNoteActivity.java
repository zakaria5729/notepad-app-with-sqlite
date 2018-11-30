package com.android.zakaria.notesapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.models.MyNote;
import com.android.zakaria.notesapp.preferences.MyPreferences;
import com.android.zakaria.notesapp.databases.MyNoteDbManager;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateOrShowNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private MyNoteDbManager myNoteDbManager;
    private MyPreferences myPreferences;

    private EditText editTitle, editNote;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView editedDateText;
    private ImageView noteImage;
    private LinearLayout linearLayoutET, bottomSheetLinearLayout, deleteNoteLL, makeACopyLL, sendNoteLL, takePhotoLL, chooseImageLL;
    private ImageButton moreHorizButton;

    private final static int REQUEST_CAMERA_CODE = 121;
    private final static int REQUEST_CHOOSE_IMAGE_CODE = 122;

    private boolean isUndoClicked, isImageChanged;
    private String showNoteKey, title, note, imagePath = null;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_show_note);

        //default status bar and action bar changed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorCreateStatusBar));
        }
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCreateActionBar)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_black_24dp);

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        editedDateText = findViewById(R.id.editedDate);
        noteImage = findViewById(R.id.noteImage);
        //moreHorizButton = findViewById(R.id.moreHoriz);
        linearLayoutET = findViewById(R.id.linearLayoutEt);

        deleteNoteLL = findViewById(R.id.deleteNoteLL);
        makeACopyLL = findViewById(R.id.makeACopyLL);
        sendNoteLL = findViewById(R.id.sendNoteLL);
        takePhotoLL = findViewById(R.id.takePhotoLL);
        chooseImageLL = findViewById(R.id.chooseImageLL);

        myNoteDbManager = new MyNoteDbManager(this);
        myPreferences = MyPreferences.getMyPreferences(this);
        setEditTextFontSize(myPreferences.getFontSize());

        deleteNoteLL.setOnClickListener(this);
        makeACopyLL.setOnClickListener(this);
        sendNoteLL.setOnClickListener(this);
        takePhotoLL.setOnClickListener(this);
        chooseImageLL.setOnClickListener(this);

        createOrDisplaySavedData();

        if (savedInstanceState != null) {
            String imageSavedInstance = savedInstanceState.getString("imageSavedInstance");

            if (imageSavedInstance != null) {
                noteImage.setVisibility(View.VISIBLE);
                //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Glide.with(this)
                        .load(imageSavedInstance)
                        .into(noteImage);

                imagePath = imageSavedInstance;
                isImageChanged = true;
            }
        }
    }

    private void createOrDisplaySavedData() {
        LinearLayout bottomSheetMoreLayout = findViewById(R.id.bottom_sheet_more_layout);
        LinearLayout bottomSheetPlusLayout = findViewById(R.id.bottom_sheet_plus_layout);

        MyPreferences myPreferences = MyPreferences.getMyPreferences(this);
        int backgroundColor = myPreferences.getBackgroundColor();

        editTitle.setBackgroundColor(backgroundColor);
        editNote.setBackgroundColor(backgroundColor);
        bottomSheetPlusLayout.setBackgroundColor(backgroundColor);
        bottomSheetMoreLayout.setBackgroundColor(backgroundColor);

        showNoteKey = getIntent().getStringExtra("our_note_key");
        String myDate;
        if (showNoteKey.equals("show_note_here")) {
            deleteNoteLL.setVisibility(View.VISIBLE);

            MyNote myNote = myNoteDbManager.getSingleNote(getIntent().getIntExtra("id", 0));
            id = myNote.getId();
            title = myNote.getTitle();
            note = myNote.getNote();
            myDate = myNote.getDate();
            imagePath = myNote.getImagePath();

            editTitle.setText(title);
            editNote.setText(note);
            editedDateText.setText(myDate);

            if (imagePath != null) {
                noteImage.setVisibility(View.VISIBLE);
                //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Glide.with(this)
                        .load(imagePath)
                        .into(noteImage);
            }

            editTitle.setSelection(editTitle.getText().length());
            editNote.setSelection(editNote.getText().length());

            editTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            editTitle.setRawInputType(InputType.TYPE_CLASS_TEXT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                editTitle.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                editNote.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }

            linearLayoutET.setFocusable(true);
            linearLayoutET.setFocusableInTouchMode(true);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        } else if (showNoteKey.equals("create_note_here")) {
            makeACopyLL.setVisibility(View.GONE);
            editTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            editTitle.setRawInputType(InputType.TYPE_CLASS_TEXT);

            myDate = "Edited: " + getCurrentDateAndTime();
            editedDateText.setText(myDate);
            editTitle.requestFocus();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeMoreBottomSheetAndKeyboard();
                    closePlusBottomSheetAndKeyboard();
                }
            }
        });

        editNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeMoreBottomSheetAndKeyboard();
                    closePlusBottomSheetAndKeyboard();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        savedOrUpdateNote();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        savedOrUpdateNote();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        super.onBackPressed();
    }

    private void savedOrUpdateNote() {
        String currentTitle = editTitle.getText().toString();
        String currentNote = editNote.getText().toString();

        if (id > 0) {
            if (!title.equals(currentTitle) || !note.equals(currentNote) || isImageChanged) {
                MyNote myNote = new MyNote(id, currentTitle, currentNote, getCurrentDateAndTime(), imagePath);
                long isUpdate = myNoteDbManager.updateNote(myNote);

                if (isUpdate <= 0) {
                    Toast.makeText(this, "Note not updated", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (!currentTitle.trim().isEmpty() || !currentNote.trim().isEmpty() || isImageChanged) {
                MyNote myNote = new MyNote(currentTitle, currentNote, getCurrentDateAndTime(), imagePath);
                long isInsert = myNoteDbManager.addNote(myNote);

                if (isInsert <= 0) {
                    Toast.makeText(this, "Note not created", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pin_archive_menu, menu);
        if (showNoteKey.equals("show_note_here")) {
            menu.findItem(R.id.archiveMenu).setVisible(true);
            menu.findItem(R.id.pinMenu).setVisible(true);
            menu.findItem(R.id.pinMenu).setVisible(true);
            menu.findItem(R.id.archiveMenu).setVisible(true);
            menu.findItem(R.id.resetMenu).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resetMenu:
                resetNote();
                break;
            case R.id.pinMenu:
                break;

            case R.id.archiveMenu:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetNote() {
        editTitle.setText("");
        editNote.setText("");
    }

    private void deleteNote() {
        title = editTitle.getText().toString();
        note = editNote.getText().toString();

        editTitle.setText("");
        editNote.setText("");
        noteImage.setVisibility(View.GONE);

        Snackbar snackbar = Snackbar.make(linearLayoutET, "Note moves to trash", Snackbar.LENGTH_INDEFINITE).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUndoClicked = true;
                editTitle.setText(title);
                editNote.setText(note);
                noteImage.setVisibility(View.VISIBLE);

                editTitle.setSelection(editTitle.getText().length());
                editNote.setSelection(editNote.getText().length());
            }
        }).setActionTextColor(Color.YELLOW);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUndoClicked) {
                    if (id > 0) {
                        MyNote myNote = new MyNote(title, note, getCurrentDateAndTime(), imagePath);
                        long isInsert = myNoteDbManager.addNoteToTrash(myNote);

                        if (isInsert > 0) {
                            long isDelete = myNoteDbManager.deleteNote(id);

                            if (isDelete > 0) {
                                Intent intent = new Intent(CreateOrShowNoteActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                            } else {
                                Toast.makeText(CreateOrShowNoteActivity.this, "Note not delete.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }, 3000);
    }

    private String getCurrentDateAndTime() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public void moreHorizontalView(View view) {
        //close previous bottom sheet and soft key board
        closePlusBottomSheetAndKeyboard();

        //start the current/desire bottom sheet
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_more_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void plusHorizontalView(View view) {
        //close previous bottom sheet and soft key board
        closeMoreBottomSheetAndKeyboard();

        //start the current/desire bottom sheet
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_plus_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setEditTextFontSize(int fontSize) {
        if (fontSize == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Small);
                editNote.setTextAppearance(android.R.style.TextAppearance_Small);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Small);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Small);
            }
        } else if (fontSize == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
                editNote.setTextAppearance(android.R.style.TextAppearance_Medium);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            }
        } else if (fontSize == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Large);
                editNote.setTextAppearance(android.R.style.TextAppearance_Large);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Large);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Large);
            }
        }
        editTitle.setTypeface(editTitle.getTypeface(), Typeface.BOLD);
    }

    private void closeMoreBottomSheetAndKeyboard() {
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_more_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void closePlusBottomSheetAndKeyboard() {
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_plus_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteNoteLL:
                deleteNote();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.makeACopyLL:
                makeACopyOfNote();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.sendNoteLL:
                shareNoteToOtherClientApp();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.chooseImageLL:
                chooseImagePermission();
                closePlusBottomSheetAndKeyboard();
                break;

            case R.id.takePhotoLL:
                takePhotoPermission();
                closePlusBottomSheetAndKeyboard();
                break;
        }
    }

    private void shareNoteToOtherClientApp() {
        if (myPreferences.getDisableShareNote().equals("disable")) {
            Toast.makeText(this, "Please, enable your note sharing option from settings", Toast.LENGTH_LONG).show();

        } else {
            if (!editTitle.getText().toString().trim().isEmpty() || !editNote.getText().toString().trim().isEmpty()) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, editTitle.getText().toString());
                intent.putExtra(android.content.Intent.EXTRA_TEXT, editNote.getText().toString());
                startActivity(android.content.Intent.createChooser(intent, "Send note"));
            } else {
                Toast.makeText(this, "Empty notes can not be shared. Insert title or body", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeACopyOfNote() {
        if (!editTitle.getText().toString().trim().isEmpty() || !editNote.getText().toString().trim().isEmpty() || imagePath != null) {
            MyNote myNote = new MyNote(editTitle.getText().toString(), editNote.getText().toString(), getCurrentDateAndTime(), imagePath);
            long insert = myNoteDbManager.addNote(myNote);

            if (insert > 0) {
                savedOrUpdateNote();

                Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
                intent.putExtra("our_note_key", "show_note_here");
                intent.putExtra("id", id);
                myNote.setDate(getCurrentDateAndTime());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        } else {
            Toast.makeText(this, "Copy of empty note can not be made", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImagePermission() {
        isImageChanged = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CHOOSE_IMAGE_CODE);
        } else {
            chooseImageForNote();
        }
    }

    private void takePhotoPermission() {
        isImageChanged = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        } else {
            takePhotoForNote();
        }
    }

    private void chooseImageForNote() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK);
        chooseImageIntent.setType("image/*");
        startActivityForResult(chooseImageIntent, REQUEST_CHOOSE_IMAGE_CODE);
    }

    private void takePhotoForNote() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, REQUEST_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CHOOSE_IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageForNote();
            } else {
                Toast.makeText(this, "Need read permission of external storage to select an image.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoForNote();
            } else {
                Toast.makeText(this, "Need camera permission to take a photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_IMAGE_CODE && data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        isImageChanged = true;
                        imagePath = saveToInternalStorage(bitmap);

                        noteImage.setVisibility(View.VISIBLE);
                        Glide.with(CreateOrShowNoteActivity.this)
                                .asBitmap()
                                .load(bitmap)
                                .into(noteImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                Bundle bundleData = data.getExtras();

                if (bundleData != null) {
                    Bitmap bitmapData = (Bitmap) bundleData.get("data");

                    if (bitmapData != null) {
                        isImageChanged = true;
                        imagePath = saveToInternalStorage(bitmapData);

                        noteImage.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .asBitmap()
                                .load(bitmapData)
                                .into(noteImage);
                    }
                }
            }
        }
        closePlusBottomSheetAndKeyboard();
    }

    private String saveToInternalStorage(Bitmap bitmapData) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        if (id > 0) {
            if (imagePath != null) {
                File currentFilePath = new File(imagePath);

                if (currentFilePath.exists()) {
                    boolean isDeleted = currentFilePath.delete();
                    if (!isDeleted) {
                        Toast.makeText(this, "Previous image can't delete", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        File directory = contextWrapper.getDir("note_image", Context.MODE_PRIVATE);
        File filePath = new File(directory, getImageName());
        String fullImagePath = filePath.toString();

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(fullImagePath);
            bitmapData.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(contextWrapper, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fullImagePath;
    }

    private String getImageName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String imageTimeStrap = simpleDateFormat.format(new Date());
        return "image_" + imageTimeStrap + ".jpg";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (imagePath != null) {
            outState.putString("imageSavedInstance", imagePath);
        }
    }

    @Override
    protected void onRestart() {
        if (!myPreferences.getSkipLockPattern().equals("skip")) {
            Intent intent = new Intent(this, InputPatternActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
        }
        super.onRestart();
    }
}
