package com.android.zakaria.notesapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.adapters.RecyclerViewAdapterForTrash;
import com.android.zakaria.notesapp.models.MyNote;
import com.android.zakaria.notesapp.preferences.MyPreferences;
import com.android.zakaria.notesapp.databases.MyNoteDbManager;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TrashActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrash;
    private RecyclerViewAdapterForTrash recyclerViewAdapterForTrash;
    private RecyclerView.LayoutManager layoutManager;
    private MyPreferences myPreferences;
    private MyNoteDbManager myNoteDbManager;

    private List<MyNote> myNotesListTrash;
    private TextView noDataTrash;
    private boolean isRestored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        recyclerViewTrash = findViewById(R.id.recyclerViewTrash);
        noDataTrash = findViewById(R.id.noDataTrash);

        myPreferences = MyPreferences.getMyPreferences(this);
        myNoteDbManager = new MyNoteDbManager(this);

        displayAllNotes();
    }

    private void displayAllNotes() {
        myNotesListTrash = myNoteDbManager.getAllNotesForTrash();

        if (!myNotesListTrash.isEmpty()) {
            noDataTrash.setVisibility(View.GONE);
            recyclerViewTrash.setVisibility(View.VISIBLE);
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }

        Collections.reverse(myNotesListTrash);
        recyclerViewTrash.setHasFixedSize(true);
        recyclerViewTrash.setLayoutManager(layoutManager);
        recyclerViewAdapterForTrash = new RecyclerViewAdapterForTrash(this, myNotesListTrash);
        recyclerViewTrash.setAdapter(recyclerViewAdapterForTrash);
        recyclerViewAdapterForTrash.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!myNotesListTrash.isEmpty()) {
            getMenuInflater().inflate(R.menu.trash_menu, menu);

            menu.findItem(R.id.emptyTrashMenuAction).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    emptyTrash();
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 121: //for restore
                displaySnackBarMessage(item.getGroupId());
                return true;

            case 122: // for delete forever
                displayWarningMessage(item.getGroupId());
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void displaySnackBarMessage(int itemId) {
        final int id = myNotesListTrash.get(itemId).getId();
        final String title = myNotesListTrash.get(itemId).getTitle();
        final String note = myNotesListTrash.get(itemId).getNote();
        final String imagePath = myNotesListTrash.get(itemId).getImagePath();

        myNotesListTrash.remove(itemId);
        recyclerViewAdapterForTrash.notifyDataSetChanged();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.trashLayout), "Note restored", Snackbar.LENGTH_INDEFINITE).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAllNotes();
                isRestored = true;
            }
        }).setActionTextColor(Color.YELLOW);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRestored) {
                    MyNote myNote = new MyNote(title, note, getCurrentDateAndTime(), imagePath);
                    myNoteDbManager.addNote(myNote);
                    long deleteStatus = new MyNoteDbManager(TrashActivity.this).deleteTrashNote(id);

                    if (deleteStatus <= 0) {
                        Toast.makeText(TrashActivity.this, "Note not restored.", Toast.LENGTH_SHORT).show();
                    }
                    snackbar.dismiss();
                }
            }
        }, 3000);
        isRestored = false;
    }

    private void displayWarningMessage(final int itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete note forever?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = myNotesListTrash.get(itemId).getId();
                myNotesListTrash.remove(itemId);

                long deleteStatus = new MyNoteDbManager(TrashActivity.this).deleteTrashNote(id);
                if (deleteStatus <= 0) {
                    Toast.makeText(TrashActivity.this, "Notes not deleted", Toast.LENGTH_SHORT).show();
                }
                recyclerViewAdapterForTrash.notifyDataSetChanged();
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

    private String getCurrentDateAndTime() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private void emptyTrash() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("All notes in Trash will be permanently deleted.");

        builder.setPositiveButton("Empty trash", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (MyNote myNotesData : myNotesListTrash) {
                    String imagePath = myNotesData.getImagePath();

                    if (imagePath != null) {
                        File filePaths = new File(imagePath);

                        if (filePaths.exists()) {
                            boolean isFileDeleted = filePaths.delete();

                            if (!isFileDeleted) {
                                Toast.makeText(TrashActivity.this, "Previous image can't delete", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TrashActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                myNotesListTrash.clear();
                boolean isDeleted = myNoteDbManager.deleteAllNotesFromTrash();
                if (isDeleted) {
                    recyclerViewAdapterForTrash.notifyDataSetChanged();
                }
                dialog.dismiss();
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

    @Override
    public boolean onSupportNavigateUp() {
        isRestored = true;
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        isRestored = true;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onRestart() {
        if (!myPreferences.getSkipLockPattern().equals("skip")) {
            Intent intent = new Intent(this, InputPatternActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        super.onRestart();
    }
}
