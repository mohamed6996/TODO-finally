package com.example.android.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.android.todolist.data.TaskContract;

import java.util.HashMap;

/**
 * Created by lenovo on 3/9/2017.
 */

/*
public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    final private ListItemClickListner mOnClickListner;

    private Cursor mCursor;
    private Context mContext;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    static HashMap<Integer, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    int id;

    public RecyclerViewAdapter(Context mContext, ListItemClickListner listner) {
        this.mContext = mContext;
        this.mOnClickListner = listner;


    }


    @Override
    public RecyclerViewAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.swipe, parent, false);

        return new RecyclerViewAdapter.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.SimpleViewHolder viewHolder, int position) {

        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TITLE);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        //   int timeIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TIME);


        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String description = mCursor.getString(descriptionIndex);


        //Set values
        viewHolder.itemView.setTag(id);
        viewHolder.taskDescriptionView.setText(title + description);


        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                //   YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                pendingRemoval();
            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle onclick here
                //   listener.onItemClick(item);
          //      mOnClickListner.onListItemClick(viewHolder.getAdapterPosition(), (Integer) viewHolder.itemView.getTag());


            }
        });

    }
       */
/* viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

    }*//*


    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void pendingRemoval() {

        notifyItemChanged(id);

        Runnable pendingRemovalRunnable = new Runnable() {
            @Override
            public void run() {
                remove();

            }
        };
        handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT); // remove item after 3 seconds
        pendingRunnables.put(id, pendingRemovalRunnable);


    }

    public void remove() {

        String stringId = Integer.toString(id);
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        mContext.getContentResolver().delete(uri, null, null);


    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SwipeLayout swipeLayout;
        TextView taskDescriptionView;
        TextView undo;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            undo = (TextView) itemView.findViewById(R.id.undo);

            itemView.setOnClickListener(this);


            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
                  */
/*  Runnable pendingRemovalRunnable = pendingRunnables.get(id);
                    pendingRunnables.remove(id);
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }*//*



                }
            });


        }

        @Override
        public void onClick(View view) {
            mOnClickListner.onListItemClick(getAdapterPosition(), (Integer) itemView.getTag());

        }


    }
}
*/
