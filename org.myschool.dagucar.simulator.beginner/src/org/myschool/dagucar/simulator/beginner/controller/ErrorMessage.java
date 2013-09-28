package org.myschool.dagucar.simulator.beginner.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ErrorMessage {
	public ErrorMessage(String msg, Throwable e) {
		super();
		this.msg = msg;
		this.e = e;
	}
	public String msg;
	public Throwable e;
	
	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		ps.println(msg);
		e.printStackTrace(ps);
		try {
			String content = baos.toString("UTF-8");
			return content;
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalStateException("Could not generate error message.", e1);
		}
	}
	
}
