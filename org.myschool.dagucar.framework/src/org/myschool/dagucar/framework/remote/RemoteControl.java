package org.myschool.dagucar.framework.remote;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.myschool.dagucar.framework.command.CommandUtils;
import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;

/**
 * When the BluetoothSender is active the model car commandos are also send via bluetooth to the pysical
 * model car (called DaguCar).
 *
 * Provides send methods to send commands like goStraight via bluetooth to the DaguCar.
 *
 * Add your send Methods.
 *
 * Tip:
 * Refer to the enumerations DirectionCommand and SpeedCommand for the definition of possible directions and speeds.
 *
 * Technical documentation:
 *
 * This bluetooth sender utilizes the the 'bluecove 2.1.1-Snapshot API' which support most 32-Bit operating systems with
 * bluetooth device drivers and bluetooth stacks and is licences under the Apache Software Licence, Version 2.0. Problems occure in 64-bit systems - only Microsoft Bluetooth stack is supported,
 * so on Windows 64-Bit systems make sure your bluetooth device runs with a microsoft driver.
 *
 * Native libraies to support bluetooth stacks for Mac OS X, WIDCOMM, BlueSoleil and Microsoft Bluetooth are included.
 *
 * @author Günter Öller
 * @version 1.0.1
 */
public class RemoteControl {
    private OutputStream os = null;
    /* this factor can be increased if battery runs low or decreased if battery is really new*/
    private double factor = 1.0;
    private final ConnectionProvider connectionProvider;

    /**
     * Singelton instance is used by all worlds"
     */
    public static final RemoteControl remoteViaBluetooth=new RemoteControl(new ConnectionProviderBluetooth());
    /**
     * Singelton instance is used by all worlds"
     */
    public static final RemoteControl remoteViaTcp=new RemoteControl(new ConnectionProviderTcpClient(18080));



    private RemoteControl(ConnectionProvider connectionProvider)  {
    	this.connectionProvider = connectionProvider;
    }

    /**
     * Sends the 'go straight' commando to DaguCar. This commands is constructed by setting the DaguCar in differnt states.
     * Eg: first we have to get moving, than we can hold a relative high speed and when we finally want to stop we have to stop the motors and
     * wait while the car is roling along until the speed is gone to zero.
     */
    public void sendGoStraight() {
        try {
            if (!this.isActive()) return; //make sure the connection is active
            this.sendNextState(DirectionCommand.NORTH,SpeedCommand.SLOW, 200); //speed up
            this.sendNextState(DirectionCommand.NORTH,SpeedCommand.CRUISING, (int) (400*factor)); //hold speed
            this.sendNextState(DirectionCommand.NORTH,SpeedCommand.STOP, 400); //role along
        } catch (Exception e) {
            System.out.println("Could not send 'go straight' to DaguCar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGoBack() {
        try {
            if (!this.isActive()) return; //make sure the connection is active
            this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.SLOW, 200); //speed up
            this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.CRUISING, (int) (400*factor)); //hold speed
            this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.STOP, 400); //role along
        } catch (Exception e) {
            System.out.println("Could not send 'go straight' to DaguCar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGoFrontLeft() {
        try {
            if (!this.isActive()) return; //make sure the connection is active
            this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.STOP, 100);
            this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.MOVING_RIGHT_ALONG, 200); //speed up
            this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.MOVING_QUICK, (int) (400*factor)); //hold speed
            this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.STOP, 400); //role along
            this.sendNextState(DirectionCommand.NORTH,SpeedCommand.STOP, 100);
        } catch (Exception e) {
            System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendGoFrontRight() {
        try {
            if (!this.isActive()) return; //make sure the connection is active
            this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.STOP, 100);
            this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.MOVING_RIGHT_ALONG, 200); //speed up
            this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.MOVING_QUICK, (int) (400*factor)); //hold speed
            this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.STOP, 400); //role along
            this.sendNextState(DirectionCommand.NORTH,SpeedCommand.STOP, 100);
        } catch (Exception e) {
            System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
            e.printStackTrace();
        }
    }





    /**
     * Activates the bluetooth connection to the DaguCar. The DaguCar must be switched on and connection
     * must be establishable.
     * @throws IOException 
     */

	public void activate(String ip) throws IOException {
		cacheConnection(ip);
	}


    /**
     * Returns if an active connection to the DaguCar is open.
     */
    public boolean isActive() {
        return os!=null;
    }

    /**
     * Shuts down the bluetooth connection to the DaguCar safely.
     */
    public void shutDown() throws IOException {
        releaseConnection();
    }

    /* sends a command to the DaguCar. For possible commands see DirectionCommand and SpeedCommand enumerations and the
     * technical documentation under http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Robotics/DaguCarCommands.pdf.
     */
    private void sendNextState(DirectionCommand direction, SpeedCommand speed, int milliseconds) throws IOException {
    	ByteBuffer buffer = ByteBuffer.allocate(5);
    	buffer.put((byte)(direction.code + speed.code));
    	byte[] bytes =buffer.putInt(milliseconds).array();
    	os.write(bytes);
        os.flush();
    }

    /*
     * creates a bluetoothconnection and opens an output stream to the service given bei this.serviceURL.
     */
    private void cacheConnection(String ip) throws IOException {
        os = this.connectionProvider.openConnection(ip);
    }

    /*
     * closes and releases connection object and output stream.
     */
    private void releaseConnection() throws IOException {
        /*
        * Close all resources
        */
        if (os!=null) {
            os.close();
            os=null;
        }
        if (connectionProvider!=null){
        	connectionProvider.closeConnection(null);
        }
    }
	
	 public void sendSingleCommand(DirectionCommand direction, SpeedCommand speed, int milliseconds) {
		 try {
	            if (!this.isActive()) throw new IllegalStateException("Connection was not activated!"); //make sure the connection is active
	            this.sendNextState(direction,speed, milliseconds);
	        } catch (Exception e) {
	            System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
	            e.printStackTrace();
	        }
	 }


}
