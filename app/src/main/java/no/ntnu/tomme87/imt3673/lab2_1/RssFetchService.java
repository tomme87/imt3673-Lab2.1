package no.ntnu.tomme87.imt3673.lab2_1;

import android.app.job.JobParameters;
import android.app.job.JobService;
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

    class RssFetchStoreTask extends AsyncTask<Void, Void, List<ItemEntity>> {

        private String url;
        private int maxItems;
        private ItemDatabase db;
        private JobParameters params;

        RssFetchStoreTask(ItemDatabase db, final JobParameters params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RssFetchService.this);
            url = sharedPreferences.getString(SettingsFragment.RSS_URL, null);
            maxItems = Integer.parseInt(sharedPreferences.getString(SettingsFragment.MAX_ITEMS, getString(R.string.list_result_default)));

            this.db = db;
            this.params = params;
        }

        @Override
        protected List<ItemEntity> doInBackground(Void... params) {
            try {
                db.itemDao().deleteAll();

                if (url == null) {
                    Log.d(TAG, "Url is null");
                    return null;
                }

                Log.d(TAG, "Max items: " + maxItems);

                InputStream inputStream = new URL(url).openConnection().getInputStream();
                Feed feed = EarlParser.parseOrThrow(inputStream, maxItems);

                List<ItemEntity> items = new ArrayList<>();
                for (Item item : feed.getItems()) {
                    items.add(new ItemEntity(item));
                }

                Log.d(TAG, "num items: " + items.size());

                db.itemDao().insert(items);

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

        @Override
        protected void onPostExecute(List<ItemEntity> itemEntities) {
            Log.d(TAG, "Job finish");
            ItemDatabase.destroyInstance();
            jobFinished(this.params, itemEntities == null);
        }

    }
}