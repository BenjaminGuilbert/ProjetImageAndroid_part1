package android.logomatchapplicationproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import java.io.IOException;



/*
Principal Screen
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // defined at each class creation
    static String tag = MainActivity.class.getName();

    //Components of this screen
    Button btnCapture;
    Button btnAnalysis;
    Button btnLibrary;
    ImageView imageCaptured;

    // Request Code of the Capture activity
    static int Capture_RequestCode = 1;
    // Request Code of the Library activity
    static int Library_RequestCode = 2;

    static int Analysis_RequestCode = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnAnalysis = (Button) findViewById(R.id.btnAnalysis);
        btnLibrary = (Button) findViewById(R.id.btnLibrary);
        imageCaptured = (ImageView) findViewById(R.id.imageCaptured);
        imageCaptured.setImageResource(R.drawable.noimageicon);

        btnCapture.setOnClickListener(this);
        btnLibrary.setOnClickListener(this);
        btnAnalysis.setOnClickListener(this);

        Log.i(tag, "MainActivity : OnCreate");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Click on Capture Button
            case R.id.btnCapture:
                Intent mediaCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(mediaCapture, Capture_RequestCode);
                break;
            //Click on Library Button
            case R.id.btnLibrary:
                Intent mediaLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(mediaLibrary, Library_RequestCode);
                break;
            //Click on Analysis Button
            case R.id.btnAnalysis:
                //Intent intentAnalyse = new Intent(MainActivity.this, AnalysisActivity.class);
                // Bundle extras = new Bundle();
                // extras.putParcelable("image",imageCaptured.getDrawingCache());
                //passer url du fichier en parametre
                // intentAnalyse.putExtras(extras);
                // startActivity(intentAnalyse);

                //Convert to byte array
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageCaptured.buildDrawingCache();
                Bitmap bmp = imageCaptured.getDrawingCache();
               // bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();

                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    bmp.recycle();

                    //Pop intent
                    Intent intentAnalyse = new Intent(MainActivity.this, AnalysisActivity.class);
                    intentAnalyse.putExtra("image", filename);
                    startActivity(intentAnalyse);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if image is captured by the

        if((requestCode == Capture_RequestCode || requestCode == Library_RequestCode) && resultCode == RESULT_OK){

            Uri selectedImageUri = data.getData();
            //Configure the imageView to display the image in portrait and in the center of it
            imageCaptured.setRotation(90);
            imageCaptured.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageCaptured.setImageURI(selectedImageUri);

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://android.logomatchapplicationproject/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://android.logomatchapplicationproject/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
