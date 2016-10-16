package edu.ncsu.mavenbot.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class MavenBuildScheduler {
	
	public void runJob(){
		System.out.println("Job started running");
	}

}
