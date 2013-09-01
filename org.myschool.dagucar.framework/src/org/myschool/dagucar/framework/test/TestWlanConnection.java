package org.myschool.dagucar.framework.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;
import org.myschool.dagucar.framework.remote.RemoteControl;
import org.myschool.dagucar.framework.test.mock.TcpServer;

public class TestWlanConnection {

	
	RemoteControl remote = RemoteControl.remoteViaTcp;
	Thread tcpserver;
	@Before
	public void setUp() {
		tcpserver = new Thread(new TcpServer());
		tcpserver.setDaemon(false);
		tcpserver.start();
	}
	
	@After
	public void cleanUp() {
		try {
			Thread.sleep(200*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
		 tcpserver.interrupt();
		}
		
	}

	
	public void testLocalTcpConnection() throws InterruptedException {
		try {
			remote.activate("127.0.0.1");
			remote.sendGoStraight();
			remote.shutDown();
			Thread.sleep(200*1000);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	@Test
	public void testAllCommands() throws InterruptedException {

		try {
			remote.activate("127.0.0.1");
			DirectionCommand[] directions=DirectionCommand.values();
			SpeedCommand[] speeds=SpeedCommand.values();
			int milliseconds = 3000;
			for (DirectionCommand d:directions) {
				for (SpeedCommand s:speeds) {
					remote.sendSingleCommand(d, s, milliseconds);
				}
			}
			remote.shutDown();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	
	public void testToRemoteIP() {

		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			remote.activate("10.63.208.218");
			remote.sendGoStraight();
			remote.shutDown();
		} catch (IOException e) {
			throw new AssertionError(e);
		}

	}

}
