package org.myschool.dagucar.framework.remote;

import java.io.IOException;

import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;

/**
 * Service that provides functions to send commands to the DaguCar. The implementation may be a bluetooth stream or a tcp stream.
 * @author Gunter
 *
 */
public interface ConnectionProvider {
	void openConnection() throws IOException;
	void writeAndFlush(int dagucarnumber, DirectionCommand direction, SpeedCommand speed, int milliseconds) throws IOException;
	void closeConnection() throws IOException;

}
