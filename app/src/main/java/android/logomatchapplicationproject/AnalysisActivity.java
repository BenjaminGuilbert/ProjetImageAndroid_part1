package android.logomatchapplicationproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import org.bytedeco.javacpp.opencv_features2d.*;
import org.bytedeco.javacpp.opencv_shape;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import static org.bytedeco.javacpp.opencv_highgui.imread;


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


        //prepare BOW descriptor extractor from the vocabulary already computed

        //final String pathToVocabulary = "vocabulary.yml" ; // to be define
        final Mat vocabulary;

        System.out.println("read vocabulary from file... ");
        Loader.load(opencv_core.class);
        opencv_core.CvFileStorage storage = opencv_core.cvOpenFileStorage("assets/Data_BOW/vocabulary.yml", null, opencv_core.CV_STORAGE_READ);
        Pointer p = opencv_core.cvReadByName(storage, null, "vocabulary", opencv_core.cvAttrList());
        opencv_core.CvMat cvMat = new opencv_core.CvMat(p);
        vocabulary = new opencv_core.Mat(cvMat);
        System.out.println("vocabulary loaded " + vocabulary.rows() + " x " + vocabulary.cols());
        opencv_core.cvReleaseFileStorage(storage);


        //create SIFT feature point extracter
        final opencv_nonfree.SIFT detector;
        // default parameters ""opencv2/features2d/features2d.hpp""
        detector = new opencv_nonfree.SIFT(0, 3, 0.04, 10, 1.6);
        //create a matcher with FlannBased Euclidien distance (possible also with BruteForce-Hamming)
        final FlannBasedMatcher matcher;
        matcher = new FlannBasedMatcher();

        //create BoF (or BoW) descriptor extractor
        final BOWImgDescriptorExtractor bowide;
        bowide = new BOWImgDescriptorExtractor(detector.asDescriptorExtractor(), matcher);

        //Set the dictionary with the vocabulary we created in the first step
        bowide.setVocabulary(vocabulary);
        System.out.println("Vocab is set");


        int classNumber = 3;
        String[] class_names;
        class_names = new String[classNumber];

        class_names[0] = "Coca";
        class_names[1] = "Pepsi";
        class_names[2] = "Sprite";


        final opencv_ml.CvSVM[] classifiers;
        classifiers = new opencv_ml.CvSVM[classNumber];
        for (int i = 0 ; i < classNumber ; i++) {
            //System.out.println("Ok. Creating class name from " + className);
            //open the file to write the resultant descriptor
            classifiers[i] = new opencv_ml.CvSVM();
            classifiers[i].load("assets/Data_BOW/classifiers/" + class_names[i] + ".xml");
        }

        Mat response_hist = new Mat();
        KeyPoint keypoints = new KeyPoint();
        Mat inputDescriptors = new Mat();


        File im = new File("assets/Data_BOW/TestImage/Coca_12.jpg");
        Mat imageTest = imread(im.getAbsolutePath(), 1);
        detector.detectAndCompute(imageTest, Mat.EMPTY, keypoints, inputDescriptors);
        bowide.compute(imageTest, keypoints, response_hist);

        // Finding best match
        float minf = Float.MAX_VALUE;
        String bestMatch = null;

        long timePrediction = System.currentTimeMillis();
        // loop for all classes
        for (int i = 0; i < classNumber; i++) {
            // classifier prediction based on reconstructed histogram
            float res = classifiers[i].predict(response_hist, true);
            //System.out.println(class_names[i] + " is " + res);
            if (res < minf) {
                minf = res;
                bestMatch = class_names[i];
            }
        }
        timePrediction = System.currentTimeMillis() - timePrediction;


        System.out.println(im.getName() + "  predicted as " + bestMatch + " in " + timePrediction + " ms");

        Bitmap bestresult = new Bitmap();


        imageToAnalyse.setImageBitmap();


    }








}
