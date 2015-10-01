package main.flick;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotoUrl;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

import org.apache.http.impl.conn.tsccm.WaitingThread;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends ListActivity {

    //the API key - connection id to flickr APP
    private static final String API_KEY = "da4fdf9200e5d844bede4724e40f51a6";

    //Network innternet manger system - check the connection
    ConnectivityManager cm;
    NetworkInfo networkInformation;

    //list view to store all the catgory
    ListView myList;

    //array of elements to display on the listView
    String[] myItems = {"Cat", "Dog", "Bird"};

    //how many photo download from flickr - depending on the category selected (by user)
    byte sumPhotosToDownload = 27;

    //global object that conatin all the methods and varibels from all the app. use to easy access
    Global gObject;

    Connection connection = new Connection();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup the listView XML to java code
        myList = (ListView) findViewById(android.R.id.list);

        //init the global object and get it
        gObject = (Global) getApplication();

        //dispaly the listView
        populateListView();

        //checking connection to the internet
        checkConnection();


    }


    private void populateListView() {

        //set the list opptios with the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, myItems);

        //set the adapter on the list
        getListView().setAdapter(adapter);

        //set the listener of the items list
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gObject.setCategory((String) myList.getItemAtPosition(position));
                connection.execute();

              //  ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
              //  progressDialog.setTitle("Download");
              //  progressDialog.setMessage("Loading...please wait");
               // progressDialog.show();
                Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_SHORT).show();


            }
        });


    }
    private void showProgressBar(){

        ProgressDialog progressProgressDialog = null;
        int progress = 0 ;
        Handler progressHandler;

        progressProgressDialog.setProgress(0);
        progressHandler = new Handler() {
            @Override
            public void close() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void publish(LogRecord record) {

            }
        };
    }

    public boolean checkConnection() {

        cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        networkInformation = cm.getActiveNetworkInfo();

        if (networkInformation != null && networkInformation.isConnected()) {

            Connection connection = new Connection();
           // connection.execute();
            return true;

        } else {
            showAlert();
            return false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private void showAlert() {

        String title = "Check your internet connection", possative = "Ok", negative = "no";
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);

        dialogBox.setMessage(title);
        dialogBox.setCancelable(true);
        dialogBox.setPositiveButton(possative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert = dialogBox.create();
        alert.show();
    }


    private void downloadPhoto() throws MalformedURLException {
        Flickr flickr = new Flickr(API_KEY);
        Bitmap bmp;

        PhotosInterface photosInterface = flickr.getPhotosInterface();
        SearchParameters myParameters = new SearchParameters();
        myParameters.setText(gObject.getCategory());
        myParameters.setSort(SearchParameters.INTERESTINGNESS_DESC);

        URL url;


        try {
            PhotoList myList = photosInterface.search(myParameters, sumPhotosToDownload, 1);
            for (Photo photo : myList) {


                Log.i("Url:", photo.getSmallUrl().toString());
                Log.i("Title:", photo.getTitle());

                //display the photo from spesific url
                url = new URL(photo.getSmallUrl().toString());
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //img.setImageBitmap(bmp);

                gObject.addItem(bmp);


            }
        } catch (Exception e) {
            Log.e("Error", "" + e);
        }

        Log.i("DownlodaPhoto() ", "Success to download  photo : " + gObject.getSize());

        try {
            Intent nextActivity = new Intent(MainActivity.this, main.flick.photosActivity.class);
            startActivity(nextActivity);
        } catch (Exception e) {
            Log.e("Error", " " + e);
        }


    }


    private class Connection extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            try {
                downloadPhoto();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

}





