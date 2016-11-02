package android.logomatchapplicationproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnOk;
    ImageView imgCaptured;
    Intent result;
    Uri uriDeFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        btnOk = (Button) findViewById(R.id.btnOk);
        imgCaptured = (ImageView) findViewById(R.id.imageCaptured);

        result = new Intent();

        Intent mediaChooser =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(mediaChooser, 1);

        btnOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        result.putExtra("imageCaptured", uriDeFoto);
        setResult(RESULT_OK, result);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK) {
            // User took a photo
            uriDeFoto=data.getData();
            imgCaptured.setImageURI(uriDeFoto);
        } else {
            // User cancelled the action
            setResult(resultCode, result);
            finish();
        }
    }
}
