package com.comp3160.holger.privatenotes.ui.notelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.comp3160.holger.privatenotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

// Fragment for when viewing the Note's contents.
public class NoteContents extends Fragment {
    
    private NoteListViewModel viewModel;
    
    private Note selectedNote;
    
    private EditText contentText;
    private EditText nameText;
    private FloatingActionButton saveButton;
    private FloatingActionButton viewButton;
    
    private InputMethodManager imm;
    
    public static NoteContents newInstance() {
        return new NoteContents();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_contents_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NoteListViewModel.class);
        observeSelected();
        setUpUI();
    }
    
    // Sets up all UI elements for the fragment.
    @SuppressLint("ClickableViewAccessibility")
    private void setUpUI() {
        imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        
        nameText = (EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.nameText);
        
        contentText = (EditText) getActivity().findViewById(R.id.contentText);
        contentText.setTextColor(Color.WHITE);
        contentText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    contentText.setTextColor(Color.BLACK);
                }
                else {
                    contentText.setTextColor(Color.WHITE);
                }
            }
        });
        
        saveButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        viewButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton2);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContents(v);
            }
        });
        viewButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        contentText.setTextColor(Color.BLACK);
                        imm.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
                        contentText.clearFocus();
                        return true;
                    case MotionEvent.ACTION_UP:
                        contentText.setTextColor(Color.WHITE);
                        imm.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
                        contentText.clearFocus();
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        contentText.setTextColor(Color.WHITE);
                        imm.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
                        contentText.clearFocus();
                        return true;
                }
                return false;
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel == null) {
            viewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);
        }
        setUpUI();
    }
    
    // Observe the currently selected note to know
    // how to set the UI up.
    private void observeSelected() {
        viewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                selectedNote = note;
                
                // New note
                if (selectedNote.getName().equals("")) {
                    nameText.setText("");
                    contentText.setText("");
                }
                // Existing note
                else {
                    nameText.setText(selectedNote.getName());
                    contentText.setText(selectedNote.getContents());
                }
            }
        });
    }
    
    // Tell the ViewModel to save the note contents to
    // the database.
    private void saveContents(View view) {
        viewModel.writeContents(nameText.getText().toString(), contentText.getText().toString());
    }
}
