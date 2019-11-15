package com.comp3160.holger.privatenotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comp3160.holger.privatenotes.ui.notelist.NoteList;
import com.comp3160.holger.privatenotes.ui.notelist.Note;
import com.comp3160.holger.privatenotes.ui.notelist.NoteListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

// Just a RecyclerView adapter. Dataset is a HashMap.
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MViewHolder> {
    private HashMap<String, Note> dataset;
    
    public RecyclerAdapter(NoteListViewModel vm, HashMap<String, Note> dataset) {
        loadData(dataset);
    }
    
    @NonNull
    @Override
    public RecyclerAdapter.MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(NoteList.TAG, "onCreateViewHolder()");
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
    
        return new MViewHolder(cardView);
    }
    
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MViewHolder holder, int position) {
        Log.d(NoteList.TAG, "onBindViewHolder -> " + "");
        holder.textView.setText(Objects.requireNonNull(dataset.get(Objects.requireNonNull(dataset.keySet().toArray())[position])).getName());
    }
    
    @Override
    public int getItemCount() {
        return dataset.size();
    }
    
    @Override
    public long getItemId(int position) {
        return (long) position;
    }
    
    class MViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        FloatingActionButton deleteButton;
        
        MViewHolder(@NonNull CardView View) {
            super(View);
            cardView = View;
            textView = (TextView) cardView.findViewById(R.id.textView_name);
            deleteButton = (FloatingActionButton) cardView.findViewById(R.id.deleteButton);
        }
    }
    
    public void loadData(HashMap<String, Note> dataset) {
        this.dataset = dataset;
    }
}
