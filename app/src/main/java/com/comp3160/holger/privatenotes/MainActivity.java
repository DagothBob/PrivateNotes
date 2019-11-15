package com.comp3160.holger.privatenotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.comp3160.holger.privatenotes.ui.notelist.Note;
import com.comp3160.holger.privatenotes.ui.notelist.NoteContents;
import com.comp3160.holger.privatenotes.ui.notelist.NoteList;
import com.comp3160.holger.privatenotes.ui.notelist.NoteListViewModel;

// MainActivity handles the fragments 'NoteList' and 'NoteContents'
// and is the main activity of the app.
public class MainActivity extends AppCompatActivity {
    
    private NoteListViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        viewModel = ViewModelProviders.of(this).get(NoteListViewModel.class);
        observeSelected();
        
        // On app startup fragment is "NoteList"
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                                       .disallowAddToBackStack()
                                       .replace(R.id.container, NoteList.newInstance())
                                       .commitNow();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        observeSelected();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle(R.string.app_name);
        getSupportFragmentManager().popBackStack();
    }
    
    // Sets observer for the currently selected note for
    // the purposes of changing fragments.
    private void observeSelected() {
        viewModel.getSelected().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                // Note contents saved -> go back to NoteList
                if (note.getName().equals(NoteListViewModel.defaultNote.getName())) {
                    setTitle(R.string.app_name);
                    getSupportFragmentManager().beginTransaction()
                                               .disallowAddToBackStack()
                                               .replace(R.id.container, NoteList.newInstance())
                                               .commit();
                }
                // New note -> go to NoteContents
                else if (note.getName().equals("")) {
                    setTitle("New note");
                    getSupportFragmentManager().beginTransaction()
                                               .addToBackStack("NoteContents")
                                               .replace(R.id.container, NoteContents.newInstance())
                                               .commit();
                }
                // Note selected -> go to NoteContents
                else {
                    setTitle(note.getName());
                    getSupportFragmentManager().beginTransaction()
                                               .addToBackStack("NoteContents")
                                               .replace(R.id.container, NoteContents.newInstance())
                                               .commit();
                }
            }
        });
    }
}
