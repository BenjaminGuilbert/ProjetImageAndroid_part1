package android.logomatchapplicationproject;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class AnalysisActivity extends AppCompatActivity {

    ImageView imageToAnalyse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        imageToAnalyse = (ImageView) findViewById(R.id.imageToAnalyse) ;


        Bundle extras = getIntent().getExtras();
        Bitmap bmp = extras.getParcelable("image");

        imageToAnalyse.setImageBitmap(bmp );
    }
}
