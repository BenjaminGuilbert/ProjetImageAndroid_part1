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
import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_features2d.*;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_shape;

import static org.bytedeco.javacpp.opencv_highgui.imread;

import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import org.opencv.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class AnalysisActivity extends AppCompatActivity  {

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
       SIFT sift;
        Loader.load(opencv_calib3d.class);
        Loader.load(opencv_shape.class);

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



        Mat [] images_ref = new Mat[]{};
        //normally it should be a KeyPointVector but doesn't work for Sift.detect
        KeyPoint keyPointsTest = new KeyPoint();
        Mat descriptorsTest = new Mat();

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

        // TRABSFORM BMP IMAGE TO FILE
        File file_analysis = new File("xx/xxx/xxxx/xxxx/");
        Mat pic_analysis;
        try {
            pic_analysis = load(file_analysis, IMREAD_COLOR);

            SIFT siftTest = new SIFT();

            // Create SIFT Ref Array
            siftTest = SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
            //detect SURF features and compute descriptors for both images
            siftTest.detect(pic_analysis, keyPointsTest);
            //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
            siftTest.compute(pic_analysis, keyPointsTest, descriptorsTest);

         } catch (IOException e) {
             e.printStackTrace();
        }





        BFMatcher matcher = new BFMatcher();
        DMatchVector [] matches = new DMatchVector[images_ref.length];
        for(int i=0;i<images_ref.length;i++){
            matches[i] = new DMatchVector();
            matcher.match(descriptorsTest, descriptorsRef[i], matches[i]);
        }

        //long t = System.currentTimeMillis();

        //matcher.knnMatch(descriptors[0], descriptors[1], matches, 2);

       // DMatchVectorVector bestMatches = refineMatches(matches);

        //****** FOR TEST ****//
       // byte[] mask = null;
       //    drawMatches(images[0], keypoints[0], images[1], keypoints[1], bestMatches, images[0]);





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
        //normally it should be a KeyPointVector but doesn't work for Sift.detect
        KeyPoint[] keyPointsRef = new KeyPoint[files.length];
        SIFT siftRef =SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        Mat[] descriptorsRef = new Mat[files.length];

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
        siftTest = SIFT.create(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        //detect SURF features and compute descriptors for both images
        siftTest.detect(pic_analysis, keyPointsTest);
        //Create CvMat initialized with empty pointer, using simply 'new Mat()' leads to an exception
        siftTest.compute(pic_analysis, keyPointsTest, descriptorsTest);
    }
    }








