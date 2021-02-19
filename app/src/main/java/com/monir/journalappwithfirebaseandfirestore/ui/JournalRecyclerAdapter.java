package com.monir.journalappwithfirebaseandfirestore.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monir.journalappwithfirebaseandfirestore.R;
import com.monir.journalappwithfirebaseandfirestore.model.Journal;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.PrimitiveIterator;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.journal_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Journal journal = journalList.get(position);
        String imageUrl;
        holder.textViewTitle.setText(journal.getTitle());
        holder.textViewThoughts.setText(journal.getThough());
        holder.textViewName.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();

        String timeAgo = (String) DateUtils
                .getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        holder.textViewTime.setText(timeAgo);

        // now we going to use picasso and load on Recycler view

        Picasso.with(context).load(imageUrl).placeholder(R.drawable.images2).fit().into(holder.list_image_view);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView list_image_view;

        private TextView textViewTitle;
        private TextView textViewThoughts;
        private TextView textViewTime;
        private TextView textViewName;
        private ImageButton shareButton;

        private String userId;
        private String username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            textViewTitle = itemView.findViewById(R.id.list_item_title_text_view);
            textViewThoughts = itemView.findViewById(R.id.list__item_thoughts_text_view);
            textViewTime = itemView.findViewById(R.id.list_item_time_text_view);
            list_image_view = itemView.findViewById(R.id.list_item_image_view);
            textViewName = itemView.findViewById(R.id.list_item_username);

            shareButton = itemView.findViewById(R.id.list_item_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
