package org.myschool.dagucar.framework.remote;
import java.io.IOException;

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
 * Native libraries to support bluetooth stacks for Mac OS X, WIDCOMM, BlueSoleil and Microsoft Bluetooth are included.
 *
 * @author Günter Öller
 * @version 1.0.1
 */
public class RemoteControl {
	/* this factor can be increased if battery runs low or decreased if battery is really new*/
	private double factor = 1.0;
	private final ConnectionProvider connectionProvider;

	/**
	 * Singleton instance is used by all worlds"
	 */
	public static final RemoteControl remoteViaTcp=new RemoteControl(new ConnectionProviderTcpClient());



	private RemoteControl(ConnectionProvider connectionProvider)  {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * Sends the 'go straight' commando to DaguCar. This commands is constructed by setting the DaguCar in differnt states.
	 * Eg: first we have to get moving, than we can hold a relative high speed and when we finally want to stop we have to stop the motors and
	 * wait while the car is roling along until the speed is gone to zero.
	 * @param dagucarnumber TODO
	 * @throws IOException
	 */
	public void sendGoStraight(int dagucarnumber) throws IOException {
		this.sendNextState(dagucarnumber,DirectionCommand.NORTH, SpeedCommand.SLOW, 500); //speed up
		this.sendNextState(dagucarnumber,DirectionCommand.NORTH, SpeedCommand.CRUISING, (int) (500*this.factor)); //hold speed
		this.sendNextState(dagucarnumber,DirectionCommand.NORTH, SpeedCommand.STOP, 500); //role along
	}

	public void sendGoBack() {
		try {
			this.sendNextState(2,DirectionCommand.SOUTH, SpeedCommand.SLOW, 200); //speed up
			this.sendNextState(2,DirectionCommand.SOUTH, SpeedCommand.CRUISING, (int) (400*this.factor)); //hold speed
			this.sendNextState(2,DirectionCommand.SOUTH, SpeedCommand.STOP, 400); //role along
		} catch (Exception e) {
			System.out.println("Could not send 'go straight' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendGoFrontLeft() {
		try {
			this.sendNextState(2,DirectionCommand.NORTHWEST, SpeedCommand.STOP, 100);
			this.sendNextState(2,DirectionCommand.NORTHWEST, SpeedCommand.MOVING_RIGHT_ALONG, 200); //speed up
			this.sendNextState(2,DirectionCommand.NORTHWEST, SpeedCommand.MOVING_QUICK, (int) (400*this.factor)); //hold speed
			this.sendNextState(2,DirectionCommand.NORTHWEST, SpeedCommand.STOP, 400); //role along
			this.sendNextState(2,DirectionCommand.NORTH, SpeedCommand.STOP, 100);
		} catch (Exception e) {
			System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendGoFrontRight() {
		try {
			this.sendNextState(2,DirectionCommand.NORTHEAST, SpeedCommand.STOP, 100);
			this.sendNextState(2,DirectionCommand.NORTHEAST, SpeedCommand.MOVING_RIGHT_ALONG, 200); //speed up
			this.sendNextState(2,DirectionCommand.NORTHEAST, SpeedCommand.MOVING_QUICK, (int) (400*this.factor)); //hold speed
			this.sendNextState(2,DirectionCommand.NORTHEAST, SpeedCommand.STOP, 400); //role along
			this.sendNextState(2,DirectionCommand.NORTH, SpeedCommand.STOP, 100);
		} catch (Exception e) {
			System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}





	/* sends a command to the DaguCar. For possible commands see DirectionCommand and SpeedCommand enumerations and the
	 * technical documentation under http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Robotics/DaguCarCommands.pdf.
	 */
	private void sendNextState(int dagucarnumber, DirectionCommand direction, SpeedCommand speed, int milliseconds) throws IOException {
		this.connectionProvider.writeAndFlush(dagucarnumber, direction, speed, milliseconds);
	}


	/*
	 * closes and releases connection object and output stream.
	 */
	public void releaseConnection() throws IOException {
		if (this.connectionProvider!=null){
			this.connectionProvider.closeConnection();
		}
	}

	public void sendSingleCommand(int dagucarnumber, DirectionCommand direction, SpeedCommand speed, int milliseconds) {
		try {
			this.sendNextState(dagucarnumber ,direction, speed, milliseconds);
		} catch (Exception e) {
			System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}


}
