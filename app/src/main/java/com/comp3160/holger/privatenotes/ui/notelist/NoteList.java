package com.comp3160.holger.privatenotes.ui.notelist;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.comp3160.holger.privatenotes.R;
import com.comp3160.holger.privatenotes.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

// Fragment for displaying the list of
// notes for the app instance in a RecyclerView.
public class NoteList extends Fragment {
    
    public static final String TAG = "PrivateNotes";
    
    private NoteListViewModel viewModel;
    
    private HashMap<String, Note> dataset = new HashMap<>();
    
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter recyclerAdapter;
    
    private FloatingActionButton addButton;
    
    public static NoteList newInstance() {
        return new NoteList();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_list_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NoteListViewModel.class);
        
        observeDataset();
        
        setUpUI();
    }
    
    // Sets up all UI objects.
    private void setUpUI() {
        addButton = (FloatingActionButton) Objects.requireNonNull(getActivity()).findViewById(R.id.buttonAddNote);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote(v);
            }
        });
    
        recyclerView = (RecyclerView) Objects.requireNonNull(getView()).findViewById(R.id.recyclerview);
    
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    
        recyclerAdapter = new RecyclerAdapter(viewModel, dataset);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            // Select a note -> go to NoteContents
            @SuppressWarnings("SuspiciousMethodCalls")
            @Override
            public void onItemClick(View view, int position) {
                final TextView tv = (TextView) view.findViewById(R.id.textView_name);
                Log.d(TAG, "OnClick/View.class: " + view.getClass());
                viewModel.selectNote(dataset.get(tv.getText()));
            }
            // Delete a note -> refresh the UI
            @Override
            public void onLongItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Note deleted!", Toast.LENGTH_SHORT).show();
                final TextView tv = (TextView) view.findViewById(R.id.textView_name);
                viewModel.deleteNote(tv.getText().toString());
            }
        }));
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel == null) {
            viewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);
            observeDataset();
        }
        setUpUI();
    }
    
    // Observe the dataset for filling the RecyclerView.
    private void observeDataset() {
        viewModel.getDataset().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Note>>() {
            @Override
            public void onChanged(HashMap<String, Note> notes) {
                Log.d(TAG, "onChanged: " + notes);
                dataset = notes;
            
                if (recyclerAdapter != null) {
                    recyclerAdapter.loadData(dataset);
                    recyclerView.removeAllViews();
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    // Called when the 'plus' button is pressed, for
    // adding a new note.
    private void addNote(View view) {
        viewModel.selectNote(NoteListViewModel.newNote);
    }
}
