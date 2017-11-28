package com.vikas.dreamworthtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
//import com.cloudinary.android.Utils;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.UploadPolicy;
import com.squareup.picasso.Picasso;
import com.vikas.dreamworthtest.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Upload_Activity extends Activity {

    ImageView img;
    ProgressDialog pd;
    Button  btnUploadPhoto;
    private static final int CHOOSE_IMAGE_REQUEST_CODE = 1000;
    LinearLayout linearlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        init();
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMediaChooser();

            }
        });
    }

    private void openMediaChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST_CODE);
    }



    private void init() {
         pd = new ProgressDialog(Upload_Activity.this);
         pd.setMessage("Uploading Image");
         pd.setCancelable(false);

        img = (ImageView) findViewById(R.id.img);
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadImage);
        linearlayout= (LinearLayout) findViewById(R.id.linerlayout);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE_REQUEST_CODE && data != null && data.getData() != null) {
            Log.e("TAG","onActivityResult"+data.getData());
            MediaManager.get().upload(data.getData())
                    .unsigned("njwr2ao5")
                    .callback(new UploadCallback() {

                @Override
                public void onStart(String requestId)
                {
                    Log.e("TAG","onStart");
                    showSnackBar("Upload started...");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.e("TAG","onProgress");

                    pd.show();

                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    pd.dismiss();
                    Log.e("TAG","onSuccess="+(resultData.get("public_id").toString()));
                    Picasso.with(Upload_Activity.this).load(getUrlForMaxWidth(resultData.get("public_id").toString())).into(img);
                    showSnackBar("Upload complete!");
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.e("TAG","onError");
                    pd.dismiss();
                    showSnackBar("Upload error: " + error.getDescription());
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    pd.dismiss();
                    Log.e("TAG","onReschedule");
                    showSnackBar("Upload rescheduled.");
                }
            }).dispatch();
        }

    }
    private void showSnackBar(String message) {
        Snackbar.make(linearlayout, message, Snackbar.LENGTH_LONG).show();
    }
    public String getUrlForMaxWidth(String imageId) {
        int width = Utils.getScreenWidth(this);
        return MediaManager.get().getCloudinary().url().transformation(new Transformation().width(width)).generate(imageId);
    }
}