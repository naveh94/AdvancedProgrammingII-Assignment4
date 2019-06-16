package com.example.exercise4.Model;


public class FlightGearClient extends TCPClient {

    // Constants: used for creating the command which is sent to the server.
    private final String SET_AILERON = "set controls/flight/aileron ";
    private final String SET_ELEVATOR = "set controls/flight/elevator ";
    private final String BREAK = "\n\r";

    /**
     * FlightGearClient's constructor. Calls to TCPClient constructor.
     * @param ipString String - ip
     * @param portString String - port
     */
    public FlightGearClient(String ipString, String portString) {
        super(ipString, portString);
    }

    /**
     * Creates a set aileron command with given value, and write it to the server.
     * @param value double - the aileron's value.
     */
    public void setAileron(double value) {
        String command = SET_AILERON + value + BREAK;
        super.write(command);
    }

    /**
     * Creates a set elevator command with given value, and write it to the server.
     * @param value double - the elevator's value.
     */
    public void setElevator(double value) {
        String command = SET_ELEVATOR + value + BREAK;
        super.write(command);
    }
}
