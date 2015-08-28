package com.halilibo.screenrecorddebug.View;

import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halilibo.screenrecorddebug.Model.RecordedVideo;
import com.halilibo.screenrecorddebug.R;
import com.halilibo.screenrecorddebug.Utility;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView historyRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new HistoryAdapter(this);
        historyRecyclerView.setAdapter(mAdapter);

        mAdapter.setData(Utility.getDB().getAllRecordedVideos());

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final Context context;
        private ArrayList<RecordedVideo> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View rootView;
            public TextView titleTextView;
            public TextView timeTextView;
            public ImageView thumbnailImageView;
            public ViewHolder(View v) {
                super(v);
                rootView = v;
                titleTextView = (TextView) v.findViewById(R.id.title_textview);
                timeTextView = (TextView) v.findViewById(R.id.time_textview);
                thumbnailImageView = (ImageView) v.findViewById(R.id.thumbnail_imageview);
            }
        }

        public HistoryAdapter(Context context) {
            this.context = context;
            mDataset = new ArrayList<>();
        }

        public void setData(ArrayList<RecordedVideo> data){
            mDataset.addAll(data);
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_history, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecordedVideoActivity.class);
                    intent.putExtra("id", mDataset.get(position).getId() );
                    context.startActivity(intent);
                }
            });
            holder.titleTextView.setText(mDataset.get(position).getTitle());
            holder.timeTextView.setText(mDataset.get(position).getTime().toString());
            holder.thumbnailImageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(
                    mDataset.get(position).getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
