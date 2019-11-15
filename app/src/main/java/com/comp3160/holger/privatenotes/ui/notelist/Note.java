package com.comp3160.holger.privatenotes.ui.notelist;

public class Note {
    private String name, contents;
    
    Note(String name, String contents) {
        this.name = name;
        this.contents = contents;
    }
    
    public String getName() {
        return name;
    }
    
    String getContents() {
        return contents;
    }
    
    public String toString() {
        return "Name: " + name + "\nContents: " + contents + "\n";
    }
}
