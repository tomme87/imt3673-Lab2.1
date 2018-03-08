package no.ntnu.tomme87.imt3673.lab2_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class ItemsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "ItemsActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ItemListAdapter itemListAdapter;

    private ServiceDoneReceiver serviceDoneReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setupUI();
        setupData();
        setupReceiver();
        //Utils.setupScheduler(this);
    }

    /**
     * Add action meny when creating options meny
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    /**
     * Run settings activity when we click settings i options menu.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            final Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Remove db instance and reciever when destroyed
     */
    @Override
    protected void onDestroy() {
        ItemDatabase.destroyInstance();
        unregisterReceiver(this.serviceDoneReceiver);
        super.onDestroy();
    }

    private void setupUI() {
        // Setup laylout
        this.swipeRefreshLayout = findViewById(R.id.l_swiperefresh);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.recyclerView = findViewById(R.id.rv_items);

        // Setup adapter
        this.itemListAdapter = new ItemListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Divider between elements: https://stackoverflow.com/a/27037230
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        // Setup view
        this.recyclerView.setAdapter(itemListAdapter);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this));
    }

    private void setupData() {
        new ItemsFromDatabaseTask(ItemDatabase.getDatabase(this)).execute();
    }

    private void setupReceiver() {
        this.serviceDoneReceiver = new ServiceDoneReceiver();
        registerReceiver(this.serviceDoneReceiver, new IntentFilter(ServiceDoneReceiver.ACTION));
    }

    /**
     * Run when user swipes down from the top of view.
     *
     * Consider actually invoking the service somehow here. this just get's data from DB.
     */
    @Override
    public void onRefresh() {
        new ItemsFromDatabaseTask(ItemDatabase.getDatabase(this)).execute();
    }

    /**
     * Class for handling touch events on the recycler view.
     */
    private class RecyclerTouchListener extends RecyclerView.SimpleOnItemTouchListener {
        private final String TAG = "RecyclerTouchListener";
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context) {
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && gestureDetector.onTouchEvent(e)) {
                Log.d(TAG, "click!");
                itemListAdapter.onClick(rv.getChildAdapterPosition(child));
            }

            return false;
        }
    }

    // Consider using static class: https://stackoverflow.com/a/46166223 ??

    /**
     * Aync task for fetching data from databse
     */
    private class ItemsFromDatabaseTask extends AsyncTask<Void, Void, List<ItemEntity>> {
        private ItemDatabase db;

        ItemsFromDatabaseTask(ItemDatabase db) {
            this.db = db;
        }

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<ItemEntity> doInBackground(Void... voids) {
            return db.itemDao().getAll();
        }

        @Override
        protected void onPostExecute(List<ItemEntity> itemEntities) {
            if (itemEntities != null && itemEntities.size() > 0) {
                itemListAdapter.setData(itemEntities);
            } else {
                Toast.makeText(getApplicationContext(), "Nothing in database, make sure settings are correct", Toast.LENGTH_LONG).show();
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onCancelled() {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Update view(adapter) with new data when service is done.
     */
    class ServiceDoneReceiver extends BroadcastReceiver {
        static final String ACTION = "no.ntnu.tomme87.imt3673.lab2_1.ServiceDoneReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            setupData();
        }
    }
}
