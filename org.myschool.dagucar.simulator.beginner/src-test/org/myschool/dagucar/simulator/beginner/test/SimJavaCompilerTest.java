package org.myschool.dagucar.simulator.beginner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;

@RunWith(JUnit4.class)
public class SimJavaCompilerTest {
	
	@Test
	public void testSimpleCompilation() throws IOException {
		SimContext context = SimContext.context;
		
		StringBuilder sourcecode=new StringBuilder();
		sourcecode.append("/* org.myschool.dagucar.simulator.beginner version 01.01 21.09.2013*/\r\n");
		sourcecode.append("public class MeinDaguCar {\r\n");
		sourcecode.append("}\r\n");
		
		Path parentPath=Paths.get("./bin");
		parentPath.toFile().mkdirs();
		Path filePath=Paths.get(parentPath.toString(), "MeinDaguCar.java");
		File file=filePath.toFile();
		FileWriter writer = new FileWriter(file);
		writer.write(sourcecode.toString());
		writer.close();
		
		context.setSourcefile(file);
		
		SimContext.javaCompiler.setContext(context);
		SimContext.javaCompiler.run();
		
		List<Diagnostic<? extends JavaFileObject>> messages=context.getMessages();

		assertNotNull(messages);
		assertTrue(messages.isEmpty());
		assertNotNull(context.getClassfile());
		assertTrue(context.getClassfile().exists());
		assertEquals(context.getClassfile().getAbsolutePath(), Paths.get("./bin/MeinDaguCar.class").toAbsolutePath().toString());
	}
	
	
	@Test
	public void testErrorCompilation() throws IOException {
		SimContext context = SimContext.context;
		
		StringBuilder sourcecode=new StringBuilder();
		sourcecode.append("/* org.myschool.dagucar.simulator.beginner version 01.01 21.09.2013*/\r\n");
		sourcecode.append("public class MeinDaguCar {\r\n");
		sourcecode.append("\t public Int test;\r\n");
		sourcecode.append("}\r\n");
		
		Path parentPath=Paths.get("./bin");
		parentPath.toFile().mkdirs();
		Path filePath=Paths.get(parentPath.toString(), "MeinDaguCar.java");
		File file=filePath.toFile();
		FileWriter writer = new FileWriter(file);
		writer.write(sourcecode.toString());
		writer.close();
		
		context.setSourcefile(file);
		SimContext.javaCompiler.setContext(context);
		SimContext.javaCompiler.run();
		
		
		List<Diagnostic<? extends JavaFileObject>> messages=context.getMessages();
		assertNotNull(messages);
//		for (Diagnostic<? extends JavaFileObject> d:messages) {
//			this.report(d);
//		}
		
		assertEquals("Wrong compiler message count", 1, messages.size(), 0);
		Diagnostic<? extends JavaFileObject> d = messages.get(0);
		assertEquals("compiler.err.cant.resolve.location", d.getCode());
		assertEquals(Kind.ERROR, d.getKind());
		assertEquals("Wrong line number", 3, d.getLineNumber());
		assertEquals("Wrong column number", 17, d.getColumnNumber());
	}
	
	@Test
	public void testLoadingAndExecuting() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		SimContext context = SimContext.context;
		
		StringBuilder sourcecode=new StringBuilder();
		sourcecode.append("/* org.myschool.dagucar.simulator.beginner version 01.01 22.09.2013*/\r\n");
		sourcecode.append("public class MeinDaguCar implements Runnable{\r\n");
		sourcecode.append("\tpublic void run() {\r\n");
		sourcecode.append("\t\tSystem.out.println(\"MeinDaguCar ist online!\");");
		sourcecode.append("\t}");
		sourcecode.append("}\r\n");
		
		Path parentPath=Paths.get("./bin");
		parentPath.toFile().mkdirs();
		Path filePath=Paths.get(parentPath.toString(), "MeinDaguCar.java");
		File file=filePath.toFile();
		FileWriter writer = new FileWriter(file);
		writer.write(sourcecode.toString());
		writer.close();
		
		context.setSourcefile(file);
		SimContext.javaCompiler.setContext(context);
		SimContext.javaCompiler.run();
		
		@SuppressWarnings("unchecked")
		Class<? extends Runnable> mycarClass = (Class<? extends Runnable>) Class.forName(context.getClassname());
		Runnable mycar=mycarClass.newInstance();
		
		File outfile = new File("./bin/sysout.txt");
		
		PrintStream newout = new PrintStream(new FileOutputStream(outfile));
		PrintStream out=System.out;
		System.setOut(newout);
		mycar.run();
		newout.close();
		System.setOut(out);
		List<Diagnostic<? extends JavaFileObject>> messages=context.getMessages();
		assertNotNull(messages);
		assertEquals("Wrong compiler message count", 0, messages.size(), 0);
		
		BufferedReader in = new BufferedReader(
				  new InputStreamReader(
				  new FileInputStream(outfile), "UTF-8"));
		try {assertEquals("MeinDaguCar ist online!", in.readLine());}
		finally {in.close();}

	}
	
	@SuppressWarnings("unused")
	private void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		System.out.println("Code->" +  diagnostic.getCode());
		System.out.println("Column Number->" + diagnostic.getColumnNumber());
		System.out.println("End Position->" + diagnostic.getEndPosition());
		System.out.println("Kind->" + diagnostic.getKind());
		System.out.println("Line Number->" + diagnostic.getLineNumber());
		System.out.println("Message->"+ diagnostic.getMessage(SimContext.local));
		System.out.println("Position->" + diagnostic.getPosition());
		System.out.println("Source" + diagnostic.getSource());
		System.out.println("Start Position->" + diagnostic.getStartPosition());
		System.out.println("\n");
	}
	
}
