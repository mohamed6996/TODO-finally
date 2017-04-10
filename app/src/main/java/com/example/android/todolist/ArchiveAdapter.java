package com.example.android.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;


public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder> {

    final private ListItemClickListner mOnClickListner;
    private Context mContext;
    private Cursor mCursor;

    public ArchiveAdapter(Context mContext, ListItemClickListner listner) {
        this.mContext = mContext;
        this.mOnClickListner = listner;

    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.archive_row_item, parent, false);

        return new ArchiveAdapter.ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(TaskContract.TaskArchiveEntry._ID);
        int titleIndex = mCursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_TITLE_ARCHIVE);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskArchiveEntry.COLUMN_DESCRIPTION_ARCHIVE);


        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String description = mCursor.getString(descriptionIndex);

        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(title );
        holder.taskDescriptionView.setTextColor(Color.WHITE);

    }



    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

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

    class ArchiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView taskDescriptionView;
        //  TextView priorityView;


        public ArchiveViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.archive_taskDescription);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            mOnClickListner.onListItemClick(getAdapterPosition(), (Integer) itemView.getTag());


        }
    }
}
