package org.myschool.dagucar.plugin.remote;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.myschool.dagucar.plugin.actor.CarAction;



public class DaguCarRemote implements Runnable{

	public static final String dagucarDeviceName = "DaguCar";
	public static final String dagucarDevice1 = "201304231621";
	public static final String dagucarDevice2 = "201304231670";
	public static final String dagucarDevice3 = "201304231728";
	public static final String dagucarDevice4 = "001210240245";

	public static final String[] devices = {dagucarDevice1,dagucarDevice2,dagucarDevice3,dagucarDevice4};

	private final String serviceUrl;
	private boolean connected;
	private BluetoothCommunicator con;


	public void act(CarAction act) {
		if (!this.connected) {
			return;
		} else {
			switch (act) {
			case back:
				this.sendGoBack(act);
				break;
			case left:
				this.sendGoFrontLeft(act);
				break;
			case right:
				this.sendGoFrontRight(act);
				break;
			case forward:
				this.sendGoStraight(act);
				break;
			default:
				break;
			}
		}
	}


	final class Task {
		@Override
		public String toString() {
			return "Task [code=" + this.code + ", sleepmillis=" + this.sleepmillis + "]";
		}
		byte code;
		int sleepmillis;
		CarAction action;
		public Task(byte code, int sleepmillis, CarAction action) {
			super();
			this.code = code;
			this.sleepmillis = sleepmillis;
			this.action = action;
		}

	};

	private final LinkedBlockingQueue<Task> queue= new LinkedBlockingQueue<Task>();
	private final Thread thread;
	private final int number;
	private Task currentTask;
	public double factor=1.0;
	//determines how many circles are done in calibration.
	public double circlesDone=1.0;


	public DaguCarRemote(int number) {
		this.serviceUrl = "btspp://"+devices[number-1]+":1;authenticate=false;encrypt=false;master=false";
		this.number = number;
		this.connected = this.connect();
		this.currentTask = null;
		this.thread = new Thread(this, "DaguCar-" + this.number + "-Thread");
		this.thread.start();
	}

	private boolean connect() {
		this.con=new BluetoothCommunicator(this);
		return this.con.check();
	}

	public String getServiceUrl() {
		return this.serviceUrl;
	}

	@Override
	public void run() {
		try {
			while (this.connected) {
				this.currentTask = this.queue.poll(5000, TimeUnit.MILLISECONDS); //blocking
				//... work on current task
				try {
					if (this.currentTask!=null) {
						this.consume();
					}
				} catch (Throwable ex) {
					System.out.println("Error running Task! Go to next Task!");
					ex.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			//Nothing toodo
		} catch (Throwable ex) {
			System.out.println("Closed Bluetooth ");
			ex.printStackTrace();
		} finally {
			if (this.con!=null) {
				this.con.cleanup();
			}
			this.connected = false;
		}
	}

	/**
	 * Called by this.thread!
	 * 
	 * Handles this.currentTask
	 *
	 */
	private void consume() {
		if (this.currentTask==null) {
			throw new IllegalStateException("No task given for :" + this.toString());
		}
		try {
			if (this.currentTask.action!=null) {
				System.out.println("DaguCar " +  this.currentTask.action);
			}
			this.con.send(this.currentTask.code);
			Thread.sleep(this.currentTask.sleepmillis);
		} catch (IOException e) {
			System.out.println("Coud not run task " + this.currentTask);
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Coud not run task " + this.currentTask);
			e.printStackTrace();
		}

	}

	/**
	 * Is called by other Thread!
	 * Queues one import tasks to a FIFO without running them immediately, and returns after queuing.
	 * @param action TODO
	 *
	 * @return actually queued import task
	 * @throws InterruptedException
	 */
	private Task queue(byte code, int sleepmillis, CarAction action) throws InterruptedException {
		Task task=new Task(code, sleepmillis, action);
		this.queue.put(task);
		return task;
	}




	/**
	 * Sends the 'go straight' commando to DaguCar. This commands is constructed by setting the DaguCar in differnt states.
	 * Eg: first we have to get moving, than we can hold a relative high speed and when we finally want to stop we have to stop the motors and
	 * wait while the car is roling along until the speed is gone to zero.
	 * @param action TODO
	 * @throws IOException
	 */
	public void sendGoStraight(CarAction action)  {
		try {
			this.sendNextState(DirectionCommand.NORTH,SpeedCommand.CRUISING, 50, action);
			this.sendNextState(DirectionCommand.NORTH,SpeedCommand.CRUISING, (int)(240*this.factor), null); //speed up
			this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.STOP, 10, null); //role along
		} catch (Exception e) {
			System.out.println("Could not send 'go straight' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendGoBack(CarAction action) {
		try {
			if (this.factor>1.5) {
				this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.MOVING_RIGHT_ALONG, 50, action);
				this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.MOVING_RIGHT_ALONG, (int)(250*this.factor/1.5), null);
			} else {
				this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.CRUISING, 50, action);
				this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.CRUISING, (int)(250*this.factor), null); //speed up
			}

			this.sendNextState(DirectionCommand.NORTH,SpeedCommand.STOP, 0, null); //role along
		} catch (Exception e) {
			System.out.println("Could not send 'go straight' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendGoFrontLeft(CarAction action) {
		try {
			this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.STOP, 100, action);
			this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.CRUISING, (int)(200/this.circlesDone), null); //hold speed
			this.sendNextState(DirectionCommand.NORTHWEST,SpeedCommand.MOVING_RIGHT_ALONG, (int)(550/this.circlesDone), null); //hold speed
			this.sendNextState(DirectionCommand.SOUTHWEST,SpeedCommand.SLOW, 50, null); //role along
			this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.STOP, 0, null); //role along
		} catch (Exception e) {
			System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendGoFrontRight(CarAction action) {
		try {
			this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.STOP, 100, action);
			this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.CRUISING, (int)(200/this.circlesDone), null); //hold speed
			this.sendNextState(DirectionCommand.NORTHEAST,SpeedCommand.MOVING_RIGHT_ALONG, (int)(550/this.circlesDone), null); //hold speedf
			this.sendNextState(DirectionCommand.SOUTHEAST,SpeedCommand.SLOW, 50, null); //role along
			this.sendNextState(DirectionCommand.SOUTH,SpeedCommand.STOP, 0, null); //role along
		} catch (Exception e) {
			System.out.println("Could not send 'go front left ' to DaguCar: " + e.getMessage());
			e.printStackTrace();
		}
	}





	/* sends a command to the DaguCar. For possible commands see DirectionCommand and SpeedCommand enumerations and the
	 * technical documentation under http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Robotics/DaguCarCommands.pdf.
	 */
	private void sendNextState(DirectionCommand direction, SpeedCommand speed, int milliseconds, CarAction action) throws IOException, InterruptedException {
		this.con.send((byte) (direction.code | speed.code));
		Thread.sleep(milliseconds);
		if (action!=null) {
			System.out.println("DaguCar " +  action);
		}

		//this.queue((byte) (direction.code | speed.code), milliseconds, action);
	}

	public void sendNextState(DirectionCommand direction, SpeedCommand speed, int milliseconds) throws IOException, InterruptedException {
		this.queue((byte) (direction.code | speed.code), milliseconds, null);
	}

	public void sendNextByte(byte code, int milliseconds) throws IOException, InterruptedException {
		this.queue(code, milliseconds, null);
	}

	@Override
	public String toString() {
		return "DaguCar " + this.number;
	}

	public void close() {
		if (this.thread!=null) {
			this.thread.interrupt();
		}
		if (this.con!=null) {
			this.con.cleanup();
		}
	}

}
