package edu.ncsu.mavenbot.adapters;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.MDC;

public class Logger extends org.apache.log4j.Logger {

	private org.apache.log4j.Logger logger;

	public static Logger getLogger(Class clazz){
		return new Logger(org.apache.log4j.Logger.getLogger(clazz));
	}
	
	public static Logger getLogger(String logger){
		return new Logger(org.apache.log4j.Logger.getLogger(logger));
	}
	protected Logger(String name) {
		super(name);
	}

	public void setIdentifier(String key, String value)
	{
		MDC.put(key, value); 
	}
	
	public void setService(String key, String value) {
		MDC.put(key, value); 
	}

	public Logger(org.apache.log4j.Logger logger) {
		super("");
		this.logger=logger;
	}

	public void debug(String message) {
		logger.debug(message);
	}
	public void error(String message, Throwable e) {
		logger.error(message,e);
		StringWriter stack = new StringWriter();
		e.printStackTrace(new PrintWriter(stack));
	}
	public void exception(Throwable e) {
		logger.error("Exception",e);
	}
	public void error(String message) {
		logger.error(message);
	}
	public void info(String message) {
		logger.info(message);
	}
}
