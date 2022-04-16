package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by NgocAnh on 11/26/2016.
 */

public class ServerTest {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1234);

            System.out.println("server start..");

            long time = System.currentTimeMillis();

            while (true) {
                Socket client = server.accept();
                System.out.println("client connect ... " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                PredictThread predict = new PredictThread(client);
                new Thread(predict).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
