package android.logomatchapplicationproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import org.bytedeco.javacpp.opencv_nonfree.SIFT;

import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_highgui.imread;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

public class AnalysisActivity extends AppCompatActivity {

    // defined at each class creation
    static String tag = AnalysisActivity.class.getName();

    ImageView imageToAnalyse;
    File file_analysis;

    Mat[] descriptorsRef;



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


        // Convert bitmap to File
        Context context = this.getApplicationContext();
        file_analysis = new File(context.getFilesDir(),filename);
        file_analysis.mkdirs();

        String path = file_analysis.getAbsolutePath();
        try {
            FileOutputStream os = new FileOutputStream(file_analysis);
            if (bmp != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os );
            }
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*To display the saved file
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        imageToAnalyse.setImageBitmap(myBitmap);
        */

        int nFeatures = 0;
        int nOctaveLayers = 3;
        double contrastThreshold = 0.03;
        int edgeThreshold = 10;
        double sigma = 1.6;

        //Load the file captured in a Mat
        Mat pic_analysis;
        KeyPoint keyPointTest = new KeyPoint();
        Mat descriptorTest = new Mat();
        try {
            pic_analysis = load(file_analysis, IMREAD_COLOR);

            SIFT siftTest = new SIFT();

            // Create SIFT Ref Array
            siftTest = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
            //detect SURF features and compute descriptors for both images
            siftTest.detect(pic_analysis, keyPointTest);
            //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
            siftTest.compute(pic_analysis, keyPointTest, descriptorTest);

        } catch (IOException e) {
            e.printStackTrace();
        }



        //Get all references images
        Mat [] images_ref = new Mat[]{};
        try {
             /*
             **   get Ref Images with the path
             **   SIFT+compute
             **   Returns Array of Images Ref
             */
            images_ref = handling_ImagesRef("app/assets/Data_BOW/TestImage/",nFeatures,nOctaveLayers,contrastThreshold,edgeThreshold,sigma);
        } catch (IOException e) {
            e.printStackTrace();
        }


        BFMatcher matcher = new BFMatcher();
        DMatchVectorVector [] matches = new DMatchVectorVector[images_ref.length];
        DMatchVectorVector[] bestMatches = new DMatchVectorVector[images_ref.length];
        float minDistance = Float.MAX_VALUE;
        int imageRefNumber = 0;
        for(int i=0;i<images_ref.length;i++){
            matches[i] = new DMatchVectorVector();
            matcher.knnMatch(descriptorTest, descriptorsRef[i], matches[i], 2);
            bestMatches[i] = refineMatches(matches[i]);
            float distanceCurrent = scoreMatch(bestMatches[i],30);
            if(distanceCurrent<minDistance){
                minDistance = distanceCurrent;
                imageRefNumber = i;
            }

        }

        //Select the file with minimum distance
        File[] Files = new File("app/assets/Data_BOW/TestImage/").listFiles();
        File bestFileMatching = Files[imageRefNumber];

        //Display the file
        Bitmap myBitmap = BitmapFactory.decodeFile(bestFileMatching.getAbsolutePath());
        imageToAnalyse.setImageBitmap(myBitmap);

    }


    public static Mat load(File file, int flags) throws IOException {
        Mat image;
        if(!file.exists()) {
            throw new FileNotFoundException("Image file does not exist: " + file.getAbsolutePath());
        }
        image = imread(file.getAbsolutePath(),flags);
        if(image == null || image.empty()) {
            throw new IOException("Couldn't load image: " + file.getAbsolutePath());
        }
        return image;
    }

    static float scoreMatch(DMatchVectorVector matches, int numberToSelect) {
        DMatch[] sorted = toArray(matches);
        float somme = 0;
        Arrays.sort(sorted);
        for(int i=0;i<sorted.length;i++){
            somme += sorted[i].distance();
        }

        return somme / numberToSelect;

    }
    static DMatch[] toArray(DMatchVectorVector matches) {
        assert matches.size() <= Integer.MAX_VALUE;
        int n = (int) matches.size();

        // Convert keyPoints to Scala sequence
        DMatch[] result = new DMatch[n];
        for (int i = 0; i < n; i++) {
            result[i] = new DMatch(matches.get(i,0)); }
        return result;
    }


    private static DMatchVectorVector refineMatches(DMatchVectorVector oldMatches) {
        // Ratio of Distances
        double RoD = 0.6;
        DMatchVectorVector newMatches = new DMatchVectorVector();

        // Refine results 1: Accept only those matches, where best dist is < RoD
        // of 2nd best match.
        int sz = 0;
        newMatches.resize(oldMatches.size());

        double maxDist = 0.0, minDist = 1e100; // infinity

        for (int i = 0; i < oldMatches.size(); i++) {
            newMatches.resize(i, 1);
            if (oldMatches.get(i, 0).distance() < RoD
                    * oldMatches.get(i, 1).distance()) {
                newMatches.put(sz, 0, oldMatches.get(i, 0));
                sz++;
                double distance = oldMatches.get(i, 0).distance();
                if (distance < minDist)
                    minDist = distance;
                if (distance > maxDist)
                    maxDist = distance;
            }
        }
        newMatches.resize(sz);

        // Refine results 2: accept only those matches which distance is no more
        // than 3x greater than best match
        sz = 0;
        DMatchVectorVector brandNewMatches = new DMatchVectorVector();
        brandNewMatches.resize(newMatches.size());
        for (int i = 0; i < newMatches.size(); i++) {
            // TODO: Move this weights into params
            // Since minDist may be equal to 0.0, add some non-zero value
            if (newMatches.get(i, 0).distance() <= 3 * minDist) {
                brandNewMatches.resize(sz, 1);
                brandNewMatches.put(sz, 0, newMatches.get(i, 0));
                sz++;
            }
        }
        brandNewMatches.resize(sz);
        return brandNewMatches;
    }


    public Mat [] handling_ImagesRef(String path,int nFeatures,int nOctaveLayers,double contrastThreshold,int edgeThreshold,double sigma)throws IOException{
        File images_path = new File(path);
        File [] files = images_path.listFiles();

        Mat[] imagesRef = new Mat[files.length];
        KeyPoint[] keyPointsRef = new KeyPoint[files.length];
        SIFT siftRef = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        descriptorsRef = new Mat[files.length];

        for(int i=0;i<files.length;i++){
            //Load image
            imagesRef[i] = load(new File(files[i].getPath()), IMREAD_COLOR);

            //Create KeyPoints
            keyPointsRef[i] = new KeyPoint();

            //detect SURF features and compute descriptors for both images
            siftRef.detect(imagesRef[0],keyPointsRef[0]);
            //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
            descriptorsRef[i] = new Mat();
            siftRef.compute(imagesRef[i], keyPointsRef[i], descriptorsRef[i]);

        }
        System.out.println("Nb fichiers Ref : " + imagesRef.length);
        return imagesRef;
    }

    public void handling_Pic_to_Analyse(String path,int nFeatures,int nOctaveLayers,double contrastThreshold,int edgeThreshold,double sigma) throws IOException {
        File file_analysis = new File(path);
        Mat pic_analysis = load(file_analysis,IMREAD_COLOR);
        //normally it should be a KeyPointVector but doesn't work for Sift.detect
        KeyPoint keyPointsTest = new KeyPoint();

        SIFT siftTest = new SIFT();
        Mat descriptorsTest = new Mat();

        // Create SIFT Ref Array
        siftTest = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        //detect SURF features and compute descriptors for both images
        siftTest.detect(pic_analysis, keyPointsTest);
        //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
        siftTest.compute(pic_analysis, keyPointsTest, descriptorsTest);
    }





}
