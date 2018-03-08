package no.ntnu.tomme87.imt3673.lab2_1;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Created by Tomme on 01.03.2018.
 *
 * This class is a service that starts at given interval.
 * it fetches RSS from url and saves it to database.
 */
public class RssFetchService extends JobService {
    private static final String TAG = "RssFetchService";

    RssFetchStoreTask rssFetchStoreTask;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "Job started");

        this.rssFetchStoreTask = new RssFetchStoreTask(ItemDatabase.getDatabase(this), params);
        this.rssFetchStoreTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        this.rssFetchStoreTask.cancel(true);
        ItemDatabase.destroyInstance();
        return true;
    }

    /**
     * Async task to fetch RSS from url and store in databse.
     */
    class RssFetchStoreTask extends AsyncTask<Void, Void, List<ItemEntity>> {

        private String url;
        private int maxItems;
        private ItemDatabase db;
        private JobParameters params;

        RssFetchStoreTask(ItemDatabase db, final JobParameters params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RssFetchService.this);
            this.url = sharedPreferences.getString(SettingsFragment.RSS_URL, null);
            this.maxItems = Integer.parseInt(sharedPreferences.getString(SettingsFragment.MAX_ITEMS, getString(R.string.list_result_default)));

            this.db = db;
            this.params = params;
        }

        @Override
        protected List<ItemEntity> doInBackground(Void... params) {
            try {
                this.db.itemDao().deleteAll();

                if (this.url == null) {
                    Log.d(TAG, "Url is null");
                    return null;
                }

                Log.d(TAG, "Max items: " + this.maxItems);

                InputStream inputStream = new URL(this.url).openConnection().getInputStream();
                Feed feed = EarlParser.parseOrThrow(inputStream, this.maxItems);

                List<ItemEntity> items = new ArrayList<>();
                for (Item item : feed.getItems()) {
                    items.add(new ItemEntity(item));
                }

                Log.d(TAG, "num items: " + items.size());

                this.db.itemDao().insert(items);

                return items;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Send a broadast when we are done.
         *
         * @param itemEntities
         */
        @Override
        protected void onPostExecute(List<ItemEntity> itemEntities) {
            Log.d(TAG, "Job finish");
            sendBroadcast(new Intent(ItemsActivity.ServiceDoneReceiver.ACTION));
            ItemDatabase.destroyInstance();
            jobFinished(this.params, itemEntities == null);
        }

    }
}
