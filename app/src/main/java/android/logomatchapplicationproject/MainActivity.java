package android.logomatchapplicationproject;

import android.content.Intent;
import android.os.Bundle;
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
        imageCaptured.setImageResource(R.drawable.tower);

        btnCapture.setOnClickListener(this);
        btnLibrary.setOnClickListener(this);
        btnAnalysis.setOnClickListener(this);

        Log.i(tag, "MainActivity : OnCreate");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCapture:
                Intent intentCapture = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intentCapture, Capture_RequestCode);
                break;
            case R.id.btnLibrary:
                Intent intentLibrary = new Intent(MainActivity.this, LibraryActivity.class);
                startActivityForResult(intentLibrary, Library_RequestCode);
                break;
            case R.id.btnAnalysis:
                Intent intentAnalyse = new Intent(MainActivity.this, AnalysisActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("image",imageCaptured.getDrawingCache());
                intentAnalyse.putExtras(extras);
                startActivity(intentAnalyse);
                break;
            default:
                break;
        }
    }
}
