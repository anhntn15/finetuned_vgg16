package com.client.ngocanh.facedectectclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NgocAnh on 11/20/2016.
 */

public class ImageProcess extends AsyncTask<Bitmap, Void, String> {
    private static Bitmap mBitmap;
    private static FaceDetector mDetector;
    private String result = "";
    private Context mContext;

    public void process() {
        Bitmap bitmap = mBitmap;

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = mDetector.detect(frame);

        if (faces.size() > 0) {
            Face face = faces.valueAt(0);

            // get face closest camera
            for (int i = 1; i < faces.size(); i ++){
                Face faceTmp = faces.valueAt(i);
                if (face.getHeight() >= faceTmp.getHeight() && face.getWidth() >= faceTmp.getWidth())
                    face = faceTmp;
            }

            int x = face.getPosition().x > 0 ? (int) face.getPosition().x : 0;
            int y = face.getPosition().y > 0 ? (int) face.getPosition().y : 0;

            Bitmap tmp = Bitmap.createBitmap(bitmap, x, y,
                    (int) (face.getWidth()),
                    (int) (face.getHeight()));

            //tmp = Bitmap.createScaledBitmap(tmp, 200, 200, false);

            result = sendBitmap(tmp);
/*
            File dir = new File(
                    Environment.getExternalStorageDirectory().toString());

            File imgFile = new File(dir.getAbsolutePath() + File.separator +
                    //(int)(Math.random() * 100)
                    result
                    + "_sNA.png");


            try {
                imgFile.createNewFile();
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                FileOutputStream fout = new FileOutputStream(imgFile);
                fout.write(ostream.toByteArray());
                fout.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
*/
        }
    }

    public String sendBitmap(Bitmap bitmap) {
        try {
            Socket client = new Socket("192.168.1.37", 1234);

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
            byte []imgByte = byteOutputStream.toByteArray();

            OutputStream outputStream = client.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(imgByte.length);
            dataOutputStream.write(imgByte);

            InputStream inputStream = client.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String res = dataInputStream.readUTF();

            //dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
            client.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error socket";
    }

    public ImageProcess(FaceDetector detector, Context context) {
        mDetector = detector;
        mContext = context;
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        mBitmap = params[0];

        if (mBitmap == null)
            return "Fail";

        process();

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
