package edu.ncsu.mavenbot.controller;

import java.io.*;
import java.util.*;

public class MavenOverseer {
	
	//the name of the directory that contains the project to look for updates
	public String ProjectLocation = "C:\\Users\\Daniel\\git\\BuildSlackers\\TestProjectID";
	
	//the name of the directory to copy the project to to look for updates
	private String ProjectUpdateLocation = "C:\\Users\\Daniel\\git\\BuildSlackers\\TestUpdates";
	
	private List<Dependency> dependencyList;

	
	//used for mocking data -> will know it is a bad project based on name
	public String projectName;
	
	/**The following is mock data to be used
	 * I don't know how I'm going to make it have an error condition for listing
	 * the dependencies, because I *shouldn't* really toggle the data, but that is probably
	 * what I will end up doing
	 */
	private Dependency dep1 = new Dependency();
	private Dependency dep2 = new Dependency();
	private Dependency dep3 = new Dependency();
	
	
	public static void main(String[] args) {
		MavenOverseer runner = new MavenOverseer();
		//System.out.print(runner.Run());
		System.out.print(runner.FindLatestGoodVersions());
		runner.UpdateDependencyToLatestGoodVersion(1);
	}
	
	public MavenOverseer() {
		dependencyList = new ArrayList<Dependency>();
		//initialize the mock data -> get rid of it later
		dep1.groupID = "io.dropwizard.metrics";
		dep1.artifactID = "metrics-core";
		dep1.currVersion = "3.1.0";
		dep1.maxUpdateableVersion = "3.1.2";
		dep1.newerVersions.add("3.1.1");
		dep1.newerVersions.add("3.1.2");
		
		dep2.groupID = "com.beust";
		dep2.artifactID = "jcommander";
		dep2.maxUpdateableVersion = "1.58";
		dep2.currVersion = "1.58";
		
		dep3.groupID = "junit";
		dep3.artifactID = "junit";
		dep3.currVersion = "3.8.1";
		dep3.maxUpdateableVersion = "4.12";
		dep3.newerVersions.add("3.8.2");
		dep3.newerVersions.add("4.0");
		dep3.newerVersions.add("4.2");
		dep3.newerVersions.add("4.8.2");
		dep3.newerVersions.add("4.10");
		dep3.newerVersions.add("4.12");
		
		dependencyList.add(dep1);
		dependencyList.add(dep2);
		dependencyList.add(dep3);
		
	}
	
	public String ListUpdateableDependencies() {
		if ("TestFolder6".equals(projectName)) {
			return "\"No dependencies for this project have newer versions available.\"";
		}
		//wipe previous list
		//dependencyList.clear();
		//for now, since mocking data skip these steps
		/*this.MakeDirectory();
		this.CopyProject();
		this.FindDependencies();
		this.FindUpdatesForDependencies();
		this.DeleteProject();*/
		StringBuilder listDeps = new StringBuilder();
		listDeps.append("\"The following dependencies can be updated to the versions listed:\\n");
		boolean atLeastOneUpdate = false;
		for (int i = 0; i < dependencyList.size(); i++) {
			Dependency curr = dependencyList.get(i);
			if (!curr.newerVersions.isEmpty() && !curr.currVersion.equals(curr.maxUpdateableVersion)) {
				atLeastOneUpdate = true;
			}
			listDeps.append(curr.groupID + ":" + curr.artifactID + ":" + curr.currVersion + ":" + curr.newerVersions);
			listDeps.append("\\n");
		}
		listDeps.append("\"");
		if (atLeastOneUpdate) {
			return listDeps.toString();
		}
		//no updates return failure
		return "\"No dependencies for this project have newer versions available.\"";
	}
	
	public String UpdateDependencyToLatestGoodVersion(int parIndex) {
		//need to have the dependencyList be the same (or have them pass in the version as well)
		//assuming dependencyList is the same, just update til it reaches maxVersion
		//need to make these updates on the project repo, instead of the fake, copy one
		//need to find the Dependency it is talking about
		Dependency dep = new Dependency();
		//dep.artifactID = artifactID;
		//dep.groupID = dependencyGroup;
		int index = parIndex - 1;//dependencyList.indexOf(dep);
		String versionToUpdateTo = "";
		String dependencyGroup = "";
		String artifactID = "";
		//check to make sure it is in bounds, if it isn't return an error message
		
		if (index >= 0 && index < dependencyList.size()) {
			versionToUpdateTo = dependencyList.get(index).maxUpdateableVersion;
			dependencyGroup = dependencyList.get(index).groupID;
			artifactID = dependencyList.get(index).artifactID;
		} else {
			return "That is an invalid selection.";
		}
		if (versionToUpdateTo.equals(dependencyList.get(index).currVersion)) {
			return "Invalid selection. That dependency cannot be updated.";
		}
		//for now, just set the dependency currversion to max upateable version
		dependencyList.get(index).currVersion = dependencyList.get(index).maxUpdateableVersion;
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
				"cd " + ProjectLocation + " && mvn versions:use-next-releases -Dincludes=" + 
				dependencyGroup + ":" + artifactID);
		builder.redirectErrorStream(true);
		boolean continueUpdate = true;
		while (continueUpdate) {
			try {
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				String newVersion = "";
				while (true) {
					line = r.readLine();
					if (line == null) {
						break;
					}
					if (line.contains("Updated")) {
						//know that a dependency has a newer version
						String[] parts = line.split("\\s+");
						newVersion = parts[parts.length - 1]; //last one is the new version
						if (newVersion.equals(versionToUpdateTo)) {
							continueUpdate = false;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		return "Update Successful";
	}
	
	//will just run, and return the dependency group:artifact id:currversion:latestupdateableversion
	public String FindLatestGoodVersions() {
		//need to update one at a time -> find list of current dependencies (then wipe it)
		//dependencyList.clear();
		//skip these steps for now, just return the mocking data
		/*this.MakeDirectory();
		this.CopyProject();
		this.FindDependencies();*/
		//now have list of the dependencies
		//get the first of them, and until update doesn't work anymore, update->compile->test
		//if all those pass, if version higher than maxupdateableversion, set new maxupdateableversion
		int count = dependencyList.size();
		/*for(int i = 0; i < count; i++) {
			//set maxupdateable version to current one
			Dependency currDep = dependencyList.get(i);
			dependencyList.remove(i); //have to remove, so can add it back later
			currDep.maxUpdateableVersion = currDep.currVersion;
			boolean updateAvailable = true;
			while (updateAvailable) {
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
						"cd " + ProjectUpdateLocation + " && mvn versions:use-next-releases -Dincludes=" + 
						currDep.groupID + ":" + currDep.artifactID);
				builder.redirectErrorStream(true);
				try {
					//whether or not there was an updated version last time,
					//set to true, so we always try at least once
					updateAvailable = false;
					Process p = builder.start();
					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					String newVersion = "";
					while (true) {
						line = r.readLine();
						if (line == null) {
							break;
						}
						if (line.contains("Updated")) {
							updateAvailable = true;
							//know that a dependency has a newer version
							String[] parts = line.split("\\s+");
							newVersion = parts[parts.length - 1]; //last one is the new version
						}
					}
					//now need to compile, and test
					if (CompileProject() && TestsPass() && updateAvailable) {
						//set version update
						currDep.maxUpdateableVersion = newVersion;
					}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
			//no more updates available, so add in the maxupdateable version
			dependencyList.add(i, currDep);
			//now need to reset it to what it was before -> need to delete currProject and make new copy
			this.DeleteProject();
			this.MakeDirectory();
			this.CopyProject();
		}*/
		//have gone through all of them, need to return the list
		boolean atleastOneUpdate = false;
		StringBuilder sb = new StringBuilder();
		sb.append("\"Your project can be updated to the following dependencies:\\n");
		for (int i = 0; i < dependencyList.size(); i++) {
			Dependency curr = dependencyList.get(i);
			if (!curr.maxUpdateableVersion.equals(curr.currVersion)) {
				atleastOneUpdate = true;
				sb.append((i+1) +". " + curr.groupID + ":" + curr.artifactID + ":" + curr.currVersion + ":" + curr.maxUpdateableVersion);
				sb.append("\\n");
			}
		}
		sb.append("\"");
		if (atleastOneUpdate) {
			return sb.toString();
		}
		return "\"No updates are available for any dependencies.\"";
		
	}
	
	private boolean TestsPass() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "cd " + ProjectUpdateLocation + "&& mvn test");
		try {
			Process p = builder.start();
			builder.redirectErrorStream(true);
			return true;
			//for the mocking, return true
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean CompileProject() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "cd " + ProjectUpdateLocation + " && mvn compile");
		builder.redirectErrorStream(true);
		try {
			Process p = builder.start();
			//for now just return true
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private void FindUpdatesForDependencies() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
				"cd " + ProjectUpdateLocation + " && mvn versions:use-next-releases");
		builder.redirectErrorStream(true);
		try {
			//whether or not there was an updated version last time,
			//set to true, so we always try at least once
			boolean updateAvailable = true;
			while (updateAvailable) {
				updateAvailable = false;
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) {
						break;
					}
					if (line.contains("Updated")) {
						updateAvailable = true;
						//know that a dependency has a newer version
						String[] parts = line.split("\\s+");
						String depPart = parts[2];
						String[] depSplit = depPart.split(":");
						String depGroup = depSplit[0];
						String depArtifact = depSplit[1];
						String depVersion = depSplit[3];
						String updatedVersion = parts[parts.length - 1]; //last one is the new version
						Dependency currDependency = new Dependency();
						currDependency.groupID = depGroup;
						currDependency.artifactID = depArtifact;
						int storedIndex = dependencyList.indexOf(currDependency);
						if (storedIndex != -1) {
							dependencyList.get(storedIndex).newerVersions.add(updatedVersion);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void FindDependencies() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
				"cd " + ProjectUpdateLocation + " && mvn -o dependency:list");
		builder.redirectErrorStream(true);
		try {
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				if (line.contains("The following files have been resolved:")) {
					//all dependencies are guaranteed to have a colon
					line = r.readLine();
					while (line.contains(":")) {
						//split on whitespace
						String[] parts = line.split("\\s+");
						//take the second one
						String dependency = parts[1];
						//split on colon
						String[] dependencyParts = dependency.split(":");
						Dependency dep = new Dependency();
						dep.groupID = dependencyParts[0];
						dep.artifactID = dependencyParts[1];
						dep.currVersion= dependencyParts[3];
						dependencyList.add(dep);
						line = r.readLine();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void MakeDirectory() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
				"mkdir " + ProjectUpdateLocation);
		try {
			Process p = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void CopyProject() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "xcopy " + ProjectLocation + " " + ProjectUpdateLocation + " /e");
		try {
			Process p = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		/**
		 * Performs cleanup, so that at the end we delete the copy of the project
		 */
	private void DeleteProject() {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "rmdir " + ProjectUpdateLocation + " /s /q");
		try {
			Process p = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	private class Dependency {
		public String groupID;
		public String artifactID;
		public String currVersion;
		public List<String> newerVersions = new ArrayList<String>();
		public String maxUpdateableVersion;
		
		public boolean equals(Object o) {
			Dependency other = (Dependency) o;
			return this.groupID.equals(other.groupID) && this.artifactID.equals(other.artifactID);
		}
	}
		
}