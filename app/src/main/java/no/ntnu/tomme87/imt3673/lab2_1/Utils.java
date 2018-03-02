package no.ntnu.tomme87.imt3673.lab2_1;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Tomme on 02.03.2018.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static final int JOB_ID = 1001;

    private Utils() {}

    public static void setupScheduler(Context context) {

        // Get frequency from preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String frequencyString = sharedPreferences.getString(SettingsFragment.FREQUENCY, context.getString(R.string.list_result_frequency_default));
        if (frequencyString == null) {
            Log.d(TAG, "Job scheduling failed: frequency null");
            return;
        }

        long frequency = Long.parseLong(frequencyString) * 60000L; // 60k in 1 minute

        // Schedule the job
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID); // Cancel old job
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, RssFetchService.class));
        builder.setPersisted(true)
                .setPeriodic(frequency)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        int ret = jobScheduler.schedule(builder.build());
        if (ret == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled successfully, frequency: " + frequency);
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }
}
