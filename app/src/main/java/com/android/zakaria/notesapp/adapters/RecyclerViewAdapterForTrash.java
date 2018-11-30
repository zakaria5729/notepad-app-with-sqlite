package com.android.zakaria.notesapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zakaria.notesapp.R;
import com.android.zakaria.notesapp.models.MyNote;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapterForTrash extends RecyclerView.Adapter<RecyclerViewAdapterForTrash.MyViewHolderForTrash> {

    private List<MyNote> myNotesListTrash;
    private Context context;

    public RecyclerViewAdapterForTrash(Context context, List<MyNote> myNotesListTrash) {
        this.context = context;
        this.myNotesListTrash = myNotesListTrash;
    }

    @NonNull
    @Override
    public MyViewHolderForTrash onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_grid_view_layout, parent, false);
        return new MyViewHolderForTrash(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderForTrash holder, int position) {
        MyNote myNote = myNotesListTrash.get(position);

        holder.headLine.setVisibility(View.VISIBLE);
        holder.details.setVisibility(View.VISIBLE);

        holder.headLine.setText(myNote.getTitle());
        holder.details.setText(myNote.getNote());
        holder.dateShow.setText(myNote.getDate());

        if (myNote.getImagePath() != null) {
            holder.userImageView.setVisibility(View.VISIBLE);
            //holder.userImageView.getLayoutParams().height = 450;

            Glide.with(context)
                    .load(myNote.getImagePath())
                    .into(holder.userImageView);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Long press on notes to restore or delete.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myNotesListTrash.size();
    }

    class MyViewHolderForTrash extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView headLine, details, dateShow;
        private RelativeLayout relativeLayout;
        private ImageView userImageView;

        MyViewHolderForTrash(View itemView) {
            super(itemView);

            headLine = itemView.findViewById(R.id.headLine);
            details = itemView.findViewById(R.id.details);
            dateShow = itemView.findViewById(R.id.dateShow);
            userImageView = itemView.findViewById(R.id.userImg);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutId);

            relativeLayout.setOnCreateContextMenuListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                headLine.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                details.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
            setNoteTexViewFontSize();
        }

        private void setNoteTexViewFontSize() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                headLine.setTextAppearance(android.R.style.TextAppearance_Medium);
                details.setTextAppearance(android.R.style.TextAppearance_Medium);
            } else {
                headLine.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                details.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            }
            headLine.setTypeface(headLine.getTypeface(), Typeface.BOLD);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Choose an option");
            menu.add(this.getAdapterPosition(), 121, 1, "Restore note"); //121 for restore
            menu.add(this.getAdapterPosition(), 122, 2, "Delete forever"); //122 for delete forever
        }
    }
}
