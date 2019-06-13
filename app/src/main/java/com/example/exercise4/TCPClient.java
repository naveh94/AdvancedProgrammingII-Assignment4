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
                    Socket socket = new Socket(ip, port);
                    try {
                        OutputStream out = socket.getOutputStream();
                        while (true) {
                            String buffer = stringQueue.take();
                            if (buffer.equals("")) {
                                break;
                            }
                            buffer = buffer + "\n\r";
                            out.write(buffer.getBytes());
                            out.flush();
                        }
                    } catch (Exception e) {
                        Log.e("TCP", "S: Error", e);
                    } finally {
                        socket.close();
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
