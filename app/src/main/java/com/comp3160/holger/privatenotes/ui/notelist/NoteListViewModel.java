package com.comp3160.holger.privatenotes.ui.notelist;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

// All data-driven logic is handled in this class.
// MainActivity and the UI fragments observe the
// LiveDatas in this class to respond to changes.
public class NoteListViewModel extends AndroidViewModel {
    
    // defaultNote is the template that observers look for to know
    // when the ViewModel is in a state where it isn't modifying
    // a note.
    //
    // newNote is checked for in observed Notes to know when it
    // is time to initiate opening of a new note.
    public static final Note defaultNote = new Note(" ", "");
    static final Note newNote = new Note("", "");
    
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference globalReference;
    private DatabaseReference myReference;
    
    private String iid;
    private MutableLiveData<HashMap<String, Note>> dataset;
    private MutableLiveData<Note> selected;
    private MutableLiveData<String> name;
    private MutableLiveData<String> contents;
    
    private Toast deleteToast;
    
    public NoteListViewModel(@NonNull final Application application) {
        super(application);
        iid = FirebaseInstanceId.getInstance().getId();
        firebaseDatabase = FirebaseDatabase.getInstance();
        globalReference = firebaseDatabase.getReference();
        myReference = globalReference.child(iid);
        
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(NoteList.TAG, "onDataChange(" + dataSnapshot + ")");
                
                // If the data for this app instance is empty.
                if (myReference.getKey() == null) {
                    dataset.setValue(new HashMap<String, Note>());
                }
                else {
                    HashMap<String, Note> tempdataset = new HashMap<>();
    
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(NoteList.TAG, "Snapshot: " + snapshot.getKey());
                        if (snapshot.exists()) {
                            Note put = new Note(snapshot.getKey(),
                                    snapshot.child("value").child("contents").getValue().toString());
                            tempdataset.put(put.getName(), put);
                            Log.d(NoteList.TAG, "" + tempdataset.get(put.getName()));
                        }
                    }
                    Log.d(NoteList.TAG, "Dataset changed");
                    dataset.setValue(tempdataset);
                }
            }
    
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
    }
    
    MutableLiveData<HashMap<String, Note>> getDataset() {
        if (dataset == null) {
            dataset = new MutableLiveData<>();
        }
        return dataset;
    }
    
    // All fragments/activities observing 'selected'
    // will be notified that the currently selected
    // note has changed.
    void selectNote(Note note) {
        selected.setValue(note);
    }
    
    public MutableLiveData<Note> getSelected() {
        if (selected == null) {
            Log.d(NoteList.TAG, "Selected note was null.");
            selected = new MutableLiveData<>();
            selected.setValue(defaultNote);
        }
        return selected;
    }
    
    // Deletes a note from the database and displays a toast.
    void deleteNote(String name) {
        if (deleteToast == null) {
            deleteToast = new Toast(getApplication());
        }
        myReference.child(name).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (deleteToast.getView() == null) {
                    deleteToast = Toast.makeText(getApplication(), "Note deleted!", Toast.LENGTH_SHORT);
                    deleteToast.show();
                }
            }
        });
    }
    
    // Called when the checkmark button is pressed in
    // NoteContents for saving a note.
    void writeContents(final String name, final String content) {
        if (name == null || name.equals("")) {
            Toast.makeText(getApplication(), "You must give your note a name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.name == null) {
            this.name = new MutableLiveData<>();
        }
        if (contents == null) {
            contents = new MutableLiveData<>();
        }
        contents.setValue(content);
        
        // New note -> add new note
        if (selected.getValue() == newNote) {
            Log.d(NoteList.TAG, "Add: " + name + " | " + content);
            myReference.child(name).child("value").setValue(new Note(name, content));
            Toast.makeText(getApplication(), "Note added!", Toast.LENGTH_SHORT).show();
            selected.setValue(defaultNote);
        }
        // Overwrite note
        else {
            Log.d(NoteList.TAG, "Overwrite: " + name + " | " + content);
            myReference.child(selected.getValue().getName()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference mdatabaseReference) {
                    if (databaseError == null) {
                        myReference.child(name).child("value").setValue(new Note(name, content));
                        Toast.makeText(getApplication(), "Note updated!", Toast.LENGTH_SHORT).show();
                        selected.setValue(defaultNote);
                    }
                    else {
                        databaseError.toException().printStackTrace(System.err);
                        Toast.makeText(getApplication(), "DatabaseError. Please report this to the developer!", Toast.LENGTH_LONG).show();
                        selected.setValue(defaultNote);
                    }
                }
            });
        }
    }
}
