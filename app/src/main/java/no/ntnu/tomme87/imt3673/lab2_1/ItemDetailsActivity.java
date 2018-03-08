package no.ntnu.tomme87.imt3673.lab2_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Tomme on 01.03.2018.
 *
 * WebView to show URL or description
 */
public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);

        final Intent i = getIntent();

        String url = i.getStringExtra(ItemListAdapter.REQUEST_URL);
        String desc = i.getStringExtra(ItemListAdapter.REQUEST_DESC);

        if(url != null) {
            webView.setWebViewClient(new WebViewClient()); // To avoid launching browser: https://stackoverflow.com/a/12802173
            webView.loadUrl(url);
        } else if(desc != null) {
            webView.loadData(i.getStringExtra(ItemListAdapter.REQUEST_DESC), "text/html", null);
        } else {
            webView.loadData("Nothing found", "text/html", null);
        }
    }
}
