package com.android.zakaria.notesapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.adapters.RecyclerViewAdapter;
import com.android.zakaria.notesapp.models.MyNote;
import com.android.zakaria.notesapp.preferences.MyPreferences;
import com.android.zakaria.notesapp.databases.MyNoteDbManager;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private MyPreferences myPreferences;
    private MyNoteDbManager myNoteDbManager;

    private List<MyNote> myNotesList;
    private int fontSize, backgroundColor;
    private boolean isUndoClicked;

    private TextView noDataTextView;
    private Button bgButtonCng11, bgButtonCng12, bgButtonCng13, bgButtonCng21, bgButtonCng22, bgButtonCng23;
    private MenuItem listMenuItem, gridMenuItem, listOrGridViewIcon;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Notes");
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noDataText);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        myPreferences = MyPreferences.getMyPreferences(this);
        myNoteDbManager = new MyNoteDbManager(this);

        fontSize = myPreferences.getFontSize();  //by default medium = 1
        backgroundColor = myPreferences.getBackgroundColor();

        if (backgroundColor != -1) {
            coordinatorLayout.setBackgroundColor(backgroundColor);
        }

        displayAllNotes();
        floatingActionButtonHideOrShow();
    }

    private void displayAllNotes() {
        MyNoteDbManager myNoteDbManager = new MyNoteDbManager(this);
        myNotesList = myNoteDbManager.getAllNotes();

        if (!myNotesList.isEmpty()) {
            noDataTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (myPreferences.isViewChange()) {
                int screenRotation = this.getResources().getConfiguration().orientation;

                if (screenRotation == 1) { //1 for portrait mode
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                } else if (screenRotation == 2) { // 2 for landscape mode
                    layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                }
            } else {
                //true means list view mode
                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }

            Collections.reverse(myNotesList);
            recyclerView.setHasFixedSize(true);
            recyclerViewAdapter = new RecyclerViewAdapter(this, myNotesList, fontSize, backgroundColor);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void floatingActionButtonHideOrShow() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**dy is increasing when scrolling down (+ve)
             dy is decreasing when scrolling up (-ve)*/
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if ((dy > 0 || dy < 0) && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        listOrGridViewIcon = menu.findItem(R.id.listOrGridViewIcon);

        if (myPreferences.isViewChange()) {
            listOrGridViewIcon.setIcon(R.drawable.list_view_ivon);
        } else {
            listOrGridViewIcon.setIcon(R.drawable.grid_view_icon);
        }

        if (!myNotesList.isEmpty()) {
            menu.findItem(R.id.deleteAllNoteIcon).setVisible(true);
        } else {
            menu.findItem(R.id.deleteAllNoteIcon).setVisible(false);
        }

        MenuItem searchMenuItem = menu.findItem(R.id.searchIcon);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Type note title or body");

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    displayAllNotes();
                } else if (!newText.trim().isEmpty() && (myNotesList.size() > 0 && !myNotesList.isEmpty())){
                    recyclerViewAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.listOrGridViewIcon:
                if (myPreferences.isViewChange()) {
                    listOrGridViewIcon.setIcon(R.drawable.list_view_ivon);
                    myPreferences.setViewChange(false);
                } else {
                    listOrGridViewIcon.setIcon(R.drawable.grid_view_icon);
                    myPreferences.setViewChange(true);
                }
                displayAllNotes();
                break;

            case R.id.trashIcon:
                startActivity(new Intent(this, TrashActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.deleteAllNoteIcon:
                deleteAllNotes();
                break;

            case R.id.fontChangeIcon:
                fontChange();
                break;

            case R.id.changeBgIcon:
                backgroundColorChange();
                break;

            case R.id.about:
                aboutDialog();
                break;

            case R.id.patternAndOthers:
                startActivity(new Intent(this, PatternAndOtherSettingActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
        return true;
    }

    public void gotToCreateNewNoteActivity(View view) {
        Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
        intent.putExtra("our_note_key", "create_note_here");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void deleteAllNotes() {
        floatingActionButton.hide();
        myNotesList.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "All notes moves to trash", Snackbar.LENGTH_INDEFINITE).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUndoClicked = true;
                floatingActionButton.show();
                displayAllNotes();
            }
        }).setActionTextColor(Color.YELLOW);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUndoClicked) {
                    myNotesList = myNoteDbManager.getAllNotes();

                    for (MyNote myNotesData : myNotesList) {
                        myNoteDbManager.addNoteToTrash(myNotesData);
                    }

                    boolean deleteStatus = myNoteDbManager.deleteAllNotes();
                    if (deleteStatus) {
                        snackbar.dismiss();
                        myNotesList.clear();
                        recyclerViewAdapter.notifyDataSetChanged();
                        noDataTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        }, 5000);
        isUndoClicked = false;
        myNotesList = myNoteDbManager.getAllNotes();
    }

    private void fontChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.font_icon);
        builder.setTitle("Choose a font size");

        builder.setSingleChoiceItems(R.array.font_size, fontSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fontSize = which;
                myPreferences.setFontSize(fontSize);
                dialog.dismiss();

                //after 1 second the activity is recreated
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 500);
            }
        });
        builder.create().show();
    }

    private void backgroundColorChange() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a color");
        builder.setIcon(R.drawable.bg_change_icon);

        View view = getLayoutInflater().inflate(R.layout.background_change_layout, null);
        bgButtonCng11 = view.findViewById(R.id.bgButtonCng11);
        bgButtonCng12 = view.findViewById(R.id.bgButtonCng12);
        bgButtonCng13 = view.findViewById(R.id.bgButtonCng13);
        bgButtonCng21 = view.findViewById(R.id.bgButtonCng21);
        bgButtonCng22 = view.findViewById(R.id.bgButtonCng22);
        bgButtonCng23 = view.findViewById(R.id.bgButtonCng23);

        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        bgButtonCng11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundColor = getBackgroundColor(v);
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng11");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        bgButtonCng12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) bgButtonCng12.getBackground();
                backgroundColor = buttonColor.getColor();
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng12");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        bgButtonCng13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) bgButtonCng13.getBackground();
                backgroundColor = buttonColor.getColor();
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng13");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        bgButtonCng21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) bgButtonCng21.getBackground();
                backgroundColor = buttonColor.getColor();
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng21");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        bgButtonCng22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) bgButtonCng22.getBackground();
                backgroundColor = buttonColor.getColor();
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng22");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        bgButtonCng23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) bgButtonCng23.getBackground();
                backgroundColor = buttonColor.getColor();
                coordinatorLayout.setBackgroundColor(backgroundColor);

                myPreferences.setBackgroundColor(backgroundColor);
                myPreferences.setCurrentlySelectedColorBtn("bgButtonCng23");
                alertDialog.dismiss();

                activityRecreated();
            }
        });

        String currentBackgroundColorBtn = myPreferences.getCurrentlySelectedColorBtn();
        switch (currentBackgroundColorBtn) {
            case "bgButtonCng11":
                bgButtonCng11.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;

            case "bgButtonCng12":
                bgButtonCng12.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;

            case "bgButtonCng13":
                bgButtonCng13.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;

            case "bgButtonCng21":
                bgButtonCng21.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;

            case "bgButtonCng22":
                bgButtonCng22.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;

            case "bgButtonCng23":
                bgButtonCng23.setCompoundDrawablesWithIntrinsicBounds(R.drawable.done_icon, 0, 0, 0);
                break;
        }
    }

    public static int getBackgroundColor(View view) {
        Drawable drawable = view.getBackground();
        if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            return colorDrawable.getColor();
        }
        return 0;
    }

    private void activityRecreated() {
        //after 1 second the activity is recreated
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 200);
    }

    @SuppressLint("InflateParams")
    private void aboutDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.about_me, null);
        }
        dialogBuilder.setView(view);
        dialogBuilder.create().show();
    }

    @Override
    public void onBackPressed() {
        isUndoClicked = true;  // if clicked on all delete button and close the app before the snack-bar is hiding
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        if (!myPreferences.getSkipLockPattern().equals("skip")) {
            Intent intent = new Intent(this, InputPatternActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        super.onRestart();
    }
}
