package android.logomatchapplicationproject;

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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //Click on Capture Button
            case R.id.btnCapture:
                Intent mediaCapture =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(mediaCapture, Capture_RequestCode);
                break;
            //Click on Library Button
            case R.id.btnLibrary:
                Intent mediaLibrary = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(mediaLibrary, Library_RequestCode);
                break;
            //Click on Analysis Button
            case R.id.btnAnalysis:
                Intent intentAnalyse = new Intent(MainActivity.this, AnalysisActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("image",imageCaptured.getDrawingCache());
                //passer url du fichier en parametre
                intentAnalyse.putExtras(extras);
                startActivity(intentAnalyse);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if image is captured by the
        if(requestCode == Capture_RequestCode && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            imageCaptured.setImageURI(selectedImageUri);
        }
        else if(requestCode == Library_RequestCode && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            imageCaptured.setImageURI(selectedImageUri);
        }

    }

    
}
