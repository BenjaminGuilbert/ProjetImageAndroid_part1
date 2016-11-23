package android.logomatchapplicationproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_features2d.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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


        imageToAnalyse.setImageBitmap(bmp );
        //convert Bitmapo MAT
        //Utils.

        Mat tmp = new Mat(bmp.getHeight(), bmp.getWidth(),CV_8UC1);

    public static Mat load(File file, int flags) throws IOException {
        if(!file.exists()) {
            throw new FileNotFoundException("Image file does not exist: " + file.getAbsolutePath());
        }
        Mat image = imread(file.getAbsolutePath(), flags);
        if(image == null || image.empty()) {
            throw new IOException("Couldn't load image: " + file.getAbsolutePath());
        }
        return image;
    }
    public static void show(Mat image, String caption) {
        CanvasFrame canvas = new CanvasFrame(caption, 1);
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        FrameConverter<Mat> converter = new OpenCVFrameConverter.ToMat();
        canvas.showImage(converter.convert(image));
    }

    public static DMatch[] toArray(DMatchVector matches) {
        assert matches.size() <= Integer.MAX_VALUE;
        // for the simplicity of the implementation we will assume that number of key points is within Int range.
        int n = (int) matches.size();

        // Convert keyPoints to Scala sequence
        DMatch[] result = new DMatch[n];
        for (int i = 0; i < n; i++) {
            result[i] = new DMatch(matches.get(i));
        }

        return result;

        //  Bundle extras = getIntent().getExtras();
        //Bitmap bmp = extras.getParcelable("image");

    }


}
