package com.android.zakaria.notesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.activities.CreateOrShowNoteActivity;
import com.android.zakaria.notesapp.models.MyNote;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable {

    private List<MyNote> myNotesList;
    private List<MyNote> myNotesFilterList;
    private Context context;
    private int fontSize, backgroundColor;

    public RecyclerViewAdapter(Context context, List<MyNote> myNotesList, int fontSize, int backgroundColor) {
        this.context = context;
        this.fontSize = fontSize;
        this.backgroundColor = backgroundColor;
        this.myNotesList = myNotesList;

        myNotesFilterList = new ArrayList<>(myNotesList); //make a copy of myNotesList with ArrayList object
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_grid_view_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MyNote myNote = myNotesList.get(position);

        if (!myNote.getTitle().trim().isEmpty()) {
            holder.headLine.setVisibility(View.VISIBLE);
            holder.headLine.setText(myNote.getTitle());
        }  else {
            holder.headLine.setVisibility(View.GONE); //Gone for filterable or searchable
        }

        if (!myNote.getNote().trim().isEmpty()) {
            holder.noteBody.setVisibility(View.VISIBLE);
            holder.noteBody.setText(myNote.getNote());
        } else {
            holder.noteBody.setVisibility(View.GONE); //Gone for filterable or searchable
        }

        holder.dateShow.setText(myNote.getDate());
        holder.relativeLayout.setBackgroundColor(backgroundColor);

        if (myNote.getImagePath() != null) {
            holder.userImageView.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(myNote.getImagePath())
                    .into(holder.userImageView);
        } else {
            holder.userImageView.setVisibility(View.GONE); //Gone for filterable or searchable
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateOrShowNoteActivity.class);
                intent.putExtra("our_note_key", "show_note_here");
                intent.putExtra("id", myNote.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myNotesList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<MyNote> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(Collections.<MyNote>emptyList());
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (MyNote note : myNotesFilterList) {
                        if (note.getTitle().toLowerCase().contains(filterPattern) || note.getNote().toLowerCase().contains(filterPattern)) {
                            filteredList.add(note);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                myNotesList.clear();
                myNotesList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView headLine, noteBody, dateShow;
        private RelativeLayout relativeLayout;
        private ImageView userImageView;

        MyViewHolder(View itemView) {
            super(itemView);

            headLine = itemView.findViewById(R.id.headLine);
            noteBody = itemView.findViewById(R.id.details);
            dateShow = itemView.findViewById(R.id.dateShow);
            userImageView = itemView.findViewById(R.id.userImg);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                headLine.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                noteBody.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
            setNoteTexViewFontSize();
        }

        private void setNoteTexViewFontSize() {
            if (fontSize == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    headLine.setTextAppearance(android.R.style.TextAppearance_Small);
                    noteBody.setTextAppearance(android.R.style.TextAppearance_Small);
                } else {
                    headLine.setTextAppearance(context, android.R.style.TextAppearance_Small);
                    noteBody.setTextAppearance(context, android.R.style.TextAppearance_Small);
                }
            } else if (fontSize == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    headLine.setTextAppearance(android.R.style.TextAppearance_Medium);
                    noteBody.setTextAppearance(android.R.style.TextAppearance_Medium);
                } else {
                    headLine.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                    noteBody.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                }
            } else if (fontSize == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    headLine.setTextAppearance(android.R.style.TextAppearance_Large);
                    noteBody.setTextAppearance(android.R.style.TextAppearance_Large);
                } else {
                    headLine.setTextAppearance(context, android.R.style.TextAppearance_Large);
                    noteBody.setTextAppearance(context, android.R.style.TextAppearance_Large);
                }
            }
            headLine.setTypeface(headLine.getTypeface(), Typeface.BOLD);
        }
    }
}

