package com.example.qbclct.netwrkcn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qbclct.netwrkcn.models.Weather;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextView textView;
    private Button button;

    Weather weather = null;
    private WeatherListAdapter listAdapter;
    private ListView mainListView;
    String imgUrl;
    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickHandler(v);
            }
        });
    }


    public void myClickHandler(View view) {
        String stringUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        String picUrl = "http://images.all-free-download.com/images/graphicthumb/quarter_rest_54558.jpg";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            textView.setText("No network connection available.");
        }

    }

    private InputStream downloadIS(String myurl) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        int response = conn.getResponseCode();
        Log.d("httpRequestResponse", "The response is: " + response);
        inputStream = conn.getInputStream();
        return inputStream;
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        private static final String DEBUG_TAG = "HttpExample";


        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);

            } catch (IOException e) {

                return null;
            }
        }


        private String downloadUrl(String myurl) throws IOException {
               InputStream inputStream = null;
            try {
                inputStream = downloadIS(myurl);
                ///
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                ///

                String weatherXMLString = total.toString();

                return weatherXMLString;

            } finally {
                if (inputStream != null) {
                    inputStream.close();

                }
            }
        }


        @Override
        protected void onPostExecute(String result) {
            try {

                ArrayList<Weather> weatherList = parseXML(result);
                mainListView = (ListView) findViewById(R.id.listView);

                ///
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = {
                        FeedReaderContract.FeedEntry._ID,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_DATE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_TEXT
                };

// How you want the results sorted in the resulting Cursor
//                String sortOrder =
//                        FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATED + " DESC";

                Cursor c = db.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                c.moveToFirst();
                String itemId = c.getString(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)
                );


                ///

                if(weatherList != null && weatherList.size() > 0) {


                    listAdapter = new WeatherListAdapter(MainActivity.this, R.layout.list_view, weatherList);
                    mainListView.setAdapter(listAdapter);
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new DownloadImageTask().execute(imgUrl);

        }
    }
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            InputStream inputStream;
            try {
                Bitmap bitmap = downloadImgUrl(urls[0]);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private Bitmap downloadImgUrl(String myurl) throws IOException {
            InputStream inputStream = null;
            try {
                inputStream = downloadIS(myurl);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } finally {
                if (inputStream != null) {
                    inputStream.close();

                }
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap){

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
    }



    public ArrayList<Weather> parseXML(String weatherXML) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        String currentTag = "";
        ArrayList<Weather> weatherList = new ArrayList<>();

        System.out.println("Recieved XML :" + weatherXML);

        xpp.setInput(new StringReader(weatherXML));

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                System.out.println("Start tag " + xpp.getName());
                currentTag = xpp.getName();
                if(currentTag.equals("forecast")){
                    weather = new Weather();
                    weather.setDate(xpp.getAttributeValue(null, "date"));
                    weather.setText(xpp.getAttributeValue(null, "text"));

                    ///

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
// Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();

                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE, weather.getDate());
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TEXT, weather.getText());
// Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                    ///


                    weatherList.add(weather);
                }


            } else if (eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag " + xpp.getName());
            } else if (eventType == XmlPullParser.TEXT) {
                if (currentTag.equals("url")){
                    imgUrl = xpp.getText();
                }
                if (currentTag.equals("language")) {
                    System.out.println("Language is  " + xpp.getText());
                }
                System.out.println("Text " + xpp.getText());
            }
            eventType = xpp.next();
        }

        System.out.println("End document");
        return  weatherList;
    }
}



