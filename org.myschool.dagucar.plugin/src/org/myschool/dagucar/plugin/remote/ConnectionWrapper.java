package org.myschool.dagucar.plugin.remote;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class ConnectionWrapper {
	private final Method openStream;
	private final Method close;
	private final Object obj;

	public ConnectionWrapper(Object obj) throws NoSuchMethodException, SecurityException {
		this.obj=obj;
		this.openStream = obj.getClass().getDeclaredMethod("openOutputStream");
		this.openStream.setAccessible(true);
		this.close = obj.getClass().getDeclaredMethod("close");
		this.close.setAccessible(true);
	}

	public OutputStream openOutputStream() throws IOException {
		try {
			return (OutputStream) this.openStream.invoke(this.obj);

		} catch (Exception e) {
			throw new IOException("Opening Bluetooth: " + e.getLocalizedMessage(), e);
		}
	}

	public void close() throws IOException {
		try {
			this.close.invoke(this.obj);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
