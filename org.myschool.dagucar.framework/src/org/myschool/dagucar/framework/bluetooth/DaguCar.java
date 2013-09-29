package org.myschool.dagucar.framework.bluetooth;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.bluetooth.RemoteDevice;

public class DaguCar implements Runnable{
	
	public int getNumber() {
		return number;
	}

	public RemoteDevice getRemoteDevice() {
		return remoteDevice;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}
	private class Task {
		@Override
		public String toString() {
			return "Task [code=" + code + ", sleepmillis=" + sleepmillis + "]";
		}
		public Task(byte code, int sleepmillis) {
			super();
			this.code = code;
			this.sleepmillis = sleepmillis;
		}
		byte code;
		int sleepmillis;
	};
	
	private final int number;
	private final RemoteDevice remoteDevice;
	private final String serviceUrl;
	private final BluetoothCommunicator bluetooth;
	private final LinkedBlockingQueue<Task> queue= new LinkedBlockingQueue<Task>();
	private final Thread thread;
	private Task currentTask;
	private boolean online;


	public DaguCar(int number, RemoteDevice remoteDevice, String serviceUrl) {
		super();
		this.number = number;
		this.remoteDevice = remoteDevice;
		this.serviceUrl = serviceUrl;
		this.currentTask = null;
		this.bluetooth = new BluetoothCommunicator(this);
		this.thread = new Thread(this, "DaguCar-" + this.number + "-Thread");
		this.thread.start();
	}
	
	@Override
	public void run() {
		this.online = this.bluetooth.check();
		try {
			while (true) {
				this.currentTask = this.queue.take(); //blocking
				//... work on current task
				try {
					this.consume();
				} catch (Throwable ex) {
					System.out.println("Error running Task! Go to next Task!");
					ex.printStackTrace();
				}
			}
		} catch (Throwable ex) {
			System.out.println("Error running Task! Go to next Task!");
			ex.printStackTrace();
		} finally {
			this.bluetooth.cleanup();
			this.online = false;
		}
	}

	/**
	 * Called by this.thread!
	 * 
	 * Handles this.currentTask
	 *
	 */
	private void consume() {
		if (this.currentTask==null) throw new IllegalStateException("No task given for :" + this.toString());
		try {
			this.bluetooth.send(this.currentTask.code);
			Thread.sleep(this.currentTask.sleepmillis);
		} catch (IOException | InterruptedException e) {
			System.out.println("Coud not run task " + this.currentTask);
			e.printStackTrace();
		}
		
	}

	/**
	 * Is called by other Thread!
	 * Queues one import tasks to a FIFO without running them immediately, and returns after queuing.
	 *
	 * @return actually queued import task
	 * @throws InterruptedException 
	 */
	public Task queue(byte code, int sleepmillis) throws InterruptedException {
		Task task=new Task(code, sleepmillis);
		queue.put(task);
		return task;
	}


	public boolean isOnline() {
		return online;
	}
	
		
	@Override
	public String toString() {
		return "DaguCar [number=" + number + ", remoteDevice=" + remoteDevice
				+ ", serviceUrl=" + serviceUrl + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + number;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DaguCar other = (DaguCar) obj;
		if (number != other.number)
			return false;
		return true;
	}

	public void cleanup() {
		if (this.thread!=null) this.thread.interrupt();
		if (this.bluetooth!=null) this.bluetooth.cleanup();
	}


}
