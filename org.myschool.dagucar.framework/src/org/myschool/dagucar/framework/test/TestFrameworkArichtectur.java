package org.myschool.dagucar.framework.test;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;
import org.myschool.dagucar.framework.program.ServerContext;
import org.myschool.dagucar.framework.program.StartDaguCarServer;
import org.myschool.dagucar.framework.remote.RemoteControl;

public class TestFrameworkArichtectur {

	RemoteControl remote = RemoteControl.remoteViaTcp;
	@BeforeClass
	public static void setUp() throws InterruptedException {
		StartDaguCarServer.main(new String[]{ServerContext.arg0});
		Thread.sleep(5000); //time for tcp server
	}



	public void testLocalTcpConnection() throws InterruptedException {
		try {
			this.remote.sendGoStraight(4);
			Thread.sleep(30 * 1000);
			this.remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void testAllDaguCarsTcpConnection() throws InterruptedException {
		try {
			for (int i=1; i<=3; i++) {
				this.remote.sendGoStraight(i);
			}
			Thread.sleep(60 * 1000);
			this.remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}


	public void testAllCommands() throws InterruptedException {

		try {
			//remote.activate("127.0.0.1");
			DirectionCommand[] directions=DirectionCommand.values();
			SpeedCommand[] speeds=SpeedCommand.values();
			int milliseconds = 3000;
			for (DirectionCommand d:directions) {
				for (SpeedCommand s:speeds) {
					this.remote.sendSingleCommand(2, d, s, milliseconds);
				}
			}
			this.remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}


	public void testToRemoteIP() {

		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			//remote.activate("10.63.208.218");
			remote.sendGoStraight(2);
			remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}

	}

}
