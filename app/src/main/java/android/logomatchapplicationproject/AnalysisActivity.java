package android.logomatchapplicationproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_features2d.*;
import org.bytedeco.javacpp.opencv_shape;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import org.opencv.android.Utils;
import org.opencv.android.*;
import org.opencv.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class AnalysisActivity extends AppCompatActivity {

    ImageView imageToAnalyse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;
        opencv_nonfree.SIFT sift = new opencv_nonfree.SIFT();
        Loader.load(opencv_calib3d.class);
        Loader.load(opencv_shape.class) ;

        imageToAnalyse = (ImageView) findViewById(R.id.imageToAnalyse);
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Mat imageMat2 = new Mat();


        org.opencv.core.Mat imageMat = new org.opencv.core.Mat();
        Utils.bitmapToMat(bmp, imageMat);

        //KeyPoint keyPointsTest = new KeyPoint();

        KeyPoint keyPointsTest = new KeyPoint();


        //KeyPointVectorVector keyPointsTest = new KeyPointVectorVector();
        Mat descriptorsTest = new Mat();

        //detect SURF features and compute descriptors for both images
        sift.detect(imageMat2,keyPointsTest);
        //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
        sift.compute(imageMat, keyPointsTest, descriptorsTest);

        //File fileTest = new File("assets/Data_BOW/TrainImage/Coca_1.jpg");
       // Bitmap myBitmap = BitmapFactory.decodeFile(fileTest.getAbsolutePath());

        //imageToAnalyse.setImageBitmap(myBitmap);
    }








}
