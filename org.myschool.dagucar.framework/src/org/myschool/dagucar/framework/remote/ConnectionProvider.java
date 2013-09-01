package org.myschool.dagucar.framework.remote;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service that provides functions to send commands to the DaguCar. The implementation may be a bluetooth stream or a tcp stream.
 * @author Gunter
 *
 */
public interface ConnectionProvider {
	OutputStream openConnection(Object connectionParameter) throws IOException;
	void closeConnection(OutputStream os) throws IOException;
}
