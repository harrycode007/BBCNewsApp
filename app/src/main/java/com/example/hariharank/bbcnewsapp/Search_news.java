package com.example.hariharank.bbcnewsapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Search_news extends AppCompatActivity {

    static final String KEY_AUTHOR = "author";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
    static final String KEY_PUBLISHEDAT = "publishedAt";
    String API_KEY = "b0319e45fed846ba8d151cc29ff2d4c3";
    String NEWS_SOURCE = "bbc-news";
    ListView listNews;
    ProgressBar loader;
    Button search;
    EditText search_text;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_news);
        listNews = (ListView) findViewById(R.id.search_listNews);
        loader = (ProgressBar) findViewById(R.id.loader);
        search_text = findViewById(R.id.search_text);
        search = findViewById(R.id.search);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(search_text.getText().toString().trim())) {
                    if (Function.isNetworkAvailable(getApplicationContext())) {
                        if (dataList.size() != 0){
                            dataList.clear();
                        }
                        listNews.setEmptyView(loader);
                        Search_news.DownloadNews newsTask = new Search_news.DownloadNews();
                        newsTask.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            xml = Function.excuteGet("https://newsapi.org/v2/everything?q="+ search_text.getText().toString() + "&sources=" + "bbc-news" + "&sortBy=top&apiKey=b0319e45fed846ba8d151cc29ff2d4c3", urlParameters);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (xml.length() > 10) {

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_AUTHOR, jsonObject.optString(KEY_AUTHOR).toString());
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());
                        dataList.add(map);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }

                ListNewsAdapter adapter = new ListNewsAdapter(Search_news.this, dataList);
                listNews.setAdapter(adapter);

                listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(view.getContext(), Uri.parse(dataList.get(+position).get(KEY_URL)));
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), R.string.no_news, Toast.LENGTH_SHORT).show();
            }
        }


    }

}
