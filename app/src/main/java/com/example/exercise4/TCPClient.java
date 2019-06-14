package com.example.exercise4;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPClient implements IClient {

    private Thread clientThread;
    private BlockingQueue<String> stringQueue;

    public TCPClient(final InetAddress ip, final int port) {
        stringQueue = new LinkedBlockingQueue<>();
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("TCP","Connecting...");
                    Socket socket = new Socket(ip, port);
                    Log.i("TCP","Connected successfully");
                    try {
                        Log.i("TCP", "Creating output stream...");
                        OutputStream out = socket.getOutputStream();
                        Log.i("TCP", "Created output stream successfully.");
                        while (true) {
                            String buffer = stringQueue.take();
                            if (buffer.equals("")) {
                                Log.i("ClientThread","Client received close command.");
                                break;
                            }
                            buffer = buffer + "\n\r";
                            out.write(buffer.getBytes());
                            out.flush();
                            Log.i("ClientThread", "Wrote to server: '" + buffer + "'.");
                        }
                    } catch (Exception e) {
                        Log.e("TCP", "S: Error", e);
                    } finally {
                        socket.close();
                        Log.i("TCP","TCP socket was closed successfully.");
                    }
                } catch (Exception e) {
                    Log.e("TCP", "C: Error", e);
                }
            }
        });
    }

    @Override
    public void connect() {
        clientThread.start();
    }

    @Override
    public void disconnect() {
        stringQueue.clear();
        stringQueue.add("");
    }

    @Override
    public void write(String input) {
        stringQueue.add(input);
    }
}
