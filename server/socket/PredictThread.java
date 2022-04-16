package socket;

import java.io.*;
import java.net.Socket;

/**
 * Created by NgocAnh on 11/26/2016.
 */
public class PredictThread implements Runnable {

    private Socket client;

    public PredictThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = client.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int lenImg = dataInputStream.readInt();
            byte[] tmp = new byte[lenImg];

            File imgFile = new File("D:/" + (lenImg) + ".png");
            FileOutputStream fout = new FileOutputStream(imgFile);

            dataInputStream.readFully(tmp);
            fout.write(tmp);
            fout.close();

            String result = "predict_" + imgFile.length();

            OutputStream outputStream = client.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(result);

            System.out.println(result);

            System.out.println("done");

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
