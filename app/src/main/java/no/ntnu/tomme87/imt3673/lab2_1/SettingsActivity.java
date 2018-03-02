package no.ntnu.tomme87.imt3673.lab2_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tomme on 02.03.2018.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
