package com.example.exercise4;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FlightGearClient {

    private IClient client;

    private final String SET_AILERON = "set controls/flight/aileron ";
    private final String SET_ELEVATOR = "set controls/flight/elevator ";
    private final String BREAK = "\n\r";

    public FlightGearClient(String ipString, String portString) throws UnknownHostException {
        InetAddress ip = InetAddress.getByName(ipString);
        int port = Integer.parseInt(portString);
        client = new TCPClient(ip, port);
        client.connect();
    }

    public void stop() {
        client.disconnect();
    }

    public void setAileron(double value) {
        String command = SET_AILERON + value + BREAK;
        client.write(command);
    }

    public void setElevator(double value) {
        String command = SET_ELEVATOR + value + BREAK;
        client.write(command);
    }
}
