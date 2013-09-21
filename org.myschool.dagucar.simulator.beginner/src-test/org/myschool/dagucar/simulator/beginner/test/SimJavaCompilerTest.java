package org.myschool.dagucar.simulator.beginner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;

@RunWith(JUnit4.class)
public class SimJavaCompilerTest {
	
	@Test
	public void testSimpleCompilation() throws IOException {
		SimContext context = new SimContext();
		
		StringBuilder sourcecode=new StringBuilder();
		sourcecode.append("/* org.myschool.dagucar.simulator.beginner version 01.01 21.09.2013*/\r\n");
		sourcecode.append("public class MeinDaguCar {}");		
		
		File file=new File("./MeinDaguCar.java");
		FileWriter writer = new FileWriter(file);
		writer.write(sourcecode.toString());
		writer.close();
		
		context.setSourcefile(file);
		
		SimContext.javaCompiler.setContext(context);
		SimContext.javaCompiler.run();

		
		assertNotNull(context.getClassfile());
		assertTrue(context.getClassfile().exists());
		assertEquals(context.getClassfile().getAbsolutePath(), new File("./MeinDaguCar.class").getAbsolutePath());
	}
	
	
}
