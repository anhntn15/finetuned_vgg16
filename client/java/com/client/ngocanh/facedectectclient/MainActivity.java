package com.client.ngocanh.facedectectclient;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView txtId;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private List<Bitmap> faceDetected;
    private FaceDetector mDetector;
    protected Matrix matrix;
    private BitmapFactory.Options options;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        txtId = (TextView) findViewById(R.id.txtview_id);

        faceDetected = new ArrayList<>();

        matrix = new Matrix();
        matrix.postRotate(90);
        options = new BitmapFactory.Options();
        options.inMutable = true;

        mDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .build();

        final FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .build();

        cameraSource = new CameraSource.Builder(this, detector)
                //.setRequestedPreviewSize(960, 1280)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // start method
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.e("zzz", "start camera source");
                    cameraSource.start(cameraView.getHolder());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        detector.setProcessor(new Detector.Processor<Face>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Face> detections) {
                final SparseArray<Face> faces = detections.getDetectedItems();
                if (faces.size() > 0) {
                    if (System.currentTimeMillis() - time < 2000)
                        return;

                    cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes) {

                            time = System.currentTimeMillis();

                            Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                                    options);

                            picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(),
                                    picture.getHeight(), matrix, true);

                            new ImageProcess(mDetector, getContext()).execute(picture);
/*
                            Bitmap tmp = getFaceCrop(picture);

                            if (tmp != null) {
                                faceDetected.add(tmp);
                                saveImage(tmp);
                            }

                            if (faceDetected.size() >= 10) {
                                isDetected = true;
                                cameraSource.stop();
                                finish();
                                return;
                            }
*/
                        }
                    });
                }
            }
        });

        if (!detector.isOperational()) {
            Log.e("zzz", "error");
            return;
        }


    }

    public Context getContext() {
        return getApplicationContext();
    }

    public Bitmap getFaceCrop(Bitmap bitmap) {

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = mDetector.detect(frame);

        Log.v("zzzz", "" + faces.size());

        if (faces.size() > 0) {
            Face face = faces.valueAt(0);

            int x = face.getPosition().x > 0 ? (int) face.getPosition().x : 0;
            int y = face.getPosition().y > 0 ? (int) face.getPosition().y : 0;

            Bitmap tmp = Bitmap.createBitmap(bitmap, x, y,
                    (int) (face.getWidth()),
                    (int) (face.getHeight()));

            return tmp;
        } else {
            return null;
        }
    }

    public void saveImage(Bitmap bitmap) {
        File dir = new File(
                Environment.getExternalStorageDirectory().toString());

        File imgFile = new File(dir.getAbsolutePath() + File.separator +
                faceDetected.size() + "_na.png");

        try {
            boolean x = imgFile.createNewFile();
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            FileOutputStream fout = new FileOutputStream(imgFile);
            fout.write(ostream.toByteArray());
            fout.close();

            Log.v("zzz ", x + imgFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cameraSource.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
