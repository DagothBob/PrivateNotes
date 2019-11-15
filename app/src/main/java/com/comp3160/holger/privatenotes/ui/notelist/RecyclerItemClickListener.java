package com.comp3160.holger.privatenotes.ui.notelist;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

// I need this ungodly crap just to handle selecting RecyclerView items.
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        
        void onLongItemClick(View view, int position);
    }
    
    private GestureDetector mGestureDetector;
    
    RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
            
            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    Log.d(NoteList.TAG, "onLongPress/view: " + child.getClass());
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }
    
    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            Log.d(NoteList.TAG, "Click pass to child view: " + childView.getClass());
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }
    
    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
    
    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}