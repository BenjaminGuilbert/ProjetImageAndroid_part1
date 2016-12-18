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

import org.bytedeco.javacpp.opencv_shape;
import static org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import static org.bytedeco.javacpp.opencv_features2d.DMatchVectorVector;
import static org.bytedeco.javacpp.opencv_features2d.KeyPoint;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_nonfree.SIFT;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import org.opencv.android.Utils;
import org.opencv.android.*;
import static org.bytedeco.javacpp.opencv_core.Mat;
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
    }








