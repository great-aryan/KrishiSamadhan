package com.phoenix.krishisamadhan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.phoenix.krishisamadhan.ml.MobilenetV110224Quant;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import android.content.res.AssetManager;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class DiagnoseActivity extends AppCompatActivity {

    Button camera, predictbtn, gallery, btn;
    TextView result, imgtxt;
    ImageView imageObj;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        btn=(Button)findViewById(R.id.Logout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        gallery = findViewById(R.id.gallery);
        predictbtn = findViewById(R.id.predictbtn);
        camera = findViewById(R.id.camera);
        result = findViewById(R.id.result);
        imageObj = findViewById(R.id.imageObj);
        imgtxt = findViewById(R.id.imgtxt);
        int counter = 2;
        float v = 0;



        predictbtn.setTranslationX(400);
        imageObj.setTranslationY(300);

        predictbtn.setAlpha(v);
        imageObj.setAlpha(v);


        predictbtn.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(200).start();
        imageObj.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(300).start();
        getPermission();

        String[] labels=new String[39];
        int cnt=0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line=bufferedReader.readLine();
            while (line!=null){
                labels[cnt]=line;
                cnt++;
                line=bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        predictbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                    try {
                        MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(getApplicationContext());

                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                        tensorImage.load(bitmap);

                        ByteBuffer byteBuffer = tensorImage.getBuffer();
                        inputFeature0.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.
                        MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        result.setText("परिणाम : " + labels[getMax(outputFeature0.getFloatArray())] + " ");
                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        Toast.makeText(DiagnoseActivity.this, "पहले फोटो अपलोड करें", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DiagnoseActivity.this, "पहले फोटो अपलोड करें", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    int getMax(float[] arr) {
        int max=0;
        for(int i=0; i<arr.length; i++){
            if(arr[i] > arr[max]) max=i;
        }
        return max;
    }

    void getPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DiagnoseActivity.this, new String[]{android.Manifest.permission.CAMERA}, 11);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11) {
            if(grantResults.length>0){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==10){
            if (data!=null){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageObj.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (requestCode==12&& resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageObj.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}