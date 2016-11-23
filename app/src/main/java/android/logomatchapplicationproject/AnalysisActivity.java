package android.logomatchapplicationproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileInputStream;

public class AnalysisActivity extends AppCompatActivity {

    ImageView imageToAnalyse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        imageToAnalyse = (ImageView) findViewById(R.id.imageToAnalyse) ;
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



      //  Bundle extras = getIntent().getExtras();
        //Bitmap bmp = extras.getParcelable("image");

        imageToAnalyse.setImageBitmap(bmp );
    }


}
