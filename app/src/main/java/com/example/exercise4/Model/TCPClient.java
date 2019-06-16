package com.example.exercise4.Model;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * A class which implements a TCP Client which connects to a server with given IP and Port, and
 * wait for inputs to send to the server. Inherit from AsyncTask class in order to use a background
 * thread which try reading strings from a BlockingQueue, and whenever a string is found, send it to
 * the server.
 */
public class TCPClient extends AsyncTask<String, Exception, Void> {

    private BlockingQueue<String> stringQueue;
    private OnExceptionListener exceptionListener;

    /**
     * TCPClient's constructor.
     * Initialize the stringQueue as a LinkedBlockingQueue, and then run his own
     * execute method in order to start the thread.
     * @param ip String - the server's ip address
     * @param port String - the server's port number.
     */
    public TCPClient(String ip, String port) {
        stringQueue = new LinkedBlockingQueue<>();
        this.execute(ip, port);
    }

    /**
     * Overriding the AsyncTask doInBackground method, parsing given IP and Port from strings to
     * an InetSocketAddress, creates a new TCP socket and then try to run it (with a timeout) with
     * the address given. If the socket connected successfully, will start reading strings from the
     * queue until given an empty string (""), which serves as a disconnect signal. For each string
     * read from the queue, translate it to a byte array and send it to the server.
     * On any case of exception, calls publishProgress method with given exception as parameter.
     * @param strings String[] - strings[0] = IP, strings[1] = Port
     * @return null
     */
    @Override
    protected Void doInBackground(String... strings) {
        String ipString = strings[0];
        String portString = strings[1];
        try {
            InetSocketAddress address
                    = new InetSocketAddress(ipString, Integer.parseInt(portString));
            Socket socket = new Socket();
            // Added a timeout, because "new Socket(address, port)"'s timeout was too long.
            socket.connect(address, 3000);
            try {
                OutputStream out = socket.getOutputStream();
                while (true) {
                    String input = stringQueue.take();
                    if (input == "") { // Disconnection flag - will break the loop.
                        break;
                    }
                    out.write(input.getBytes());
                    out.flush();
                }
            } catch (IOException | InterruptedException e) {
                publishProgress(e);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            publishProgress(e);
        }
        return null;
    }

    /***
     * Overriding the AsyncTask's onProgressUpdate method which is called on publishProgress calls.
     * If an exceptionListener exists, invoke it with given Exception parameter.
     * @param values Exception[] - values[0] = the given exception.
     */
    @Override
    protected void onProgressUpdate(Exception... values) {
        super.onProgressUpdate(values);
        if (exceptionListener != null) {
            exceptionListener.onException(values[0]);
        }
    }

    /**
     * Serve as the safe disconnect method for the client. Will clear the stringQueue from every
     * string that wasn't sent yet, and send an empty string (which serves as a disconnect flag) to
     * the queue.
     */
    public void disconnect() {
        stringQueue.clear();
        stringQueue.add("");
    }

    /**
     * Send given input to the server by adding it to the stringQueue.
     * @param input String - given input.
     */
    public void write(String input) {
        stringQueue.add(input);
    }


    /**
     * Set the onExceptionListener for the class. Will be invoked when an exception occurs on the
     * background task.
     * @param exceptionListener OnExceptionListener
     */
    public void setExceptionListener(OnExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    /**
     * A functional interface implemented by the exceptionLister.
     */
    public interface OnExceptionListener {
        void onException(Exception e);
    }
}
