package edu.ncsu.mavenbot.controller;

import java.io.*;
import java.util.*;

public class MavenOverseer {
	
	//the name of the directory that contains the project to look for updates
	public String CurrentDirectory=System.getProperty("user.dir");
	public String ProjectLocation = CurrentDirectory+"\\try\\";
	
	//the name of the directory to copy the project to to look for updates
	private String ProjectUpdateLocation = CurrentDirectory+"\\TestUpdates";
	
	private List<Dependency> dependencyList;
	
	
	
	public static void main(String[] args) { 
		MavenOverseer runner = new MavenOverseer();
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd");
		builder.redirectErrorStream(true);
		Process p;
		try {
			p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = r.readLine();
		while (line != null) {
			System.out.println(line);
			line = r.readLine();
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(runner.FindLatestGoodVersions());
	}
	
	public MavenOverseer() {
		dependencyList = new ArrayList<Dependency>();		
	}
	
	public String ListUpdateableDependencies() {
		//wipe previous list
		dependencyList.clear();
		//for now, since mocking data skip these steps
		this.MakeDirectory();
		this.CopyProject();
		this.FindDependencies();
		this.FindUpdatesForDependencies();
		this.DeleteProject(ProjectUpdateLocation);
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
	
	public boolean UpdateDependencyToLatestGoodVersion(int parIndex) {
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
			return false;
		}
		//make sure not null also, comment
		if (versionToUpdateTo == null || versionToUpdateTo.equals(dependencyList.get(index).currVersion)) {
			return false;
		}
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
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
		}
		return true;
	}
	
	//will just run, and return the dependency group:artifact id:currversion:latestupdateableversion
	public String FindLatestGoodVersions() {
		//need to update one at a time -> find list of current dependencies (then wipe it)
		dependencyList.clear();
		this.MakeDirectory();
		this.CopyProject();
		this.FindDependencies();
		//now have list of the dependencies
		//get the first of them, and until update doesn't work anymore, update->compile->test
		//if all those pass, if version higher than maxupdateableversion, set new maxupdateableversion
		int count = dependencyList.size();
		for(int i = 0; i < count; i++) {
			//set maxupdateable version to current one
			Dependency currDep = dependencyList.get(i);
			dependencyList.remove(i); //have to remove, so can add it back later
			boolean updateAvailable = true;
			while (updateAvailable) {
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", 
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
			this.DeleteProject(ProjectUpdateLocation);
			this.MakeDirectory();
			this.CopyProject();
		}
		//have gone through all of them, need to return the list
		boolean atleastOneUpdate = false;
		StringBuilder sb = new StringBuilder();
		sb.append("\"Your project can be updated to the following dependencies:\\n");
		for (int i = 0; i < dependencyList.size(); i++) {
			Dependency curr = dependencyList.get(i);
			if (curr.maxUpdateableVersion != null && 
					!curr.maxUpdateableVersion.equals(curr.currVersion)) {
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
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", 
				"-c", "cd " + ProjectUpdateLocation + "&& mvn test");
		try {
			Process p = builder.start();
			builder.redirectErrorStream(true);
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				if (line.contains("Tests run")) {
					String[] testResults = line.split(",");
					for (int i = 0; i < testResults.length; i++) {
						if (testResults[i].contains("Failures") || 
								testResults[i].contains("Errors") || testResults[i].contains("Skipped")) {
							//now split on space
							String[] numTests = testResults[i].split(" ");
							//look at second one
							if (!numTests[2].equals("0")) {
								return false;
							}
						}
					}
					return true;
				}
			}
			//for the mocking, return true
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean CompileProject() {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", 
				"-c", "cd " + ProjectUpdateLocation + " && mvn compile");
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
				if (line.contains("BUILD SUCCESS")) {
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void FindUpdatesForDependencies() {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", 
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
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", 
				"cd " + ProjectUpdateLocation + " && mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:list");
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
		File file = new File(ProjectUpdateLocation);
		/*if(file.exists())
        {
        	GitAdapter.deleteFolder(file);
        }*/
        file.mkdir();
	}
	
	private void CopyProject() {
		File src= new File(ProjectLocation);
		File dest= new File(ProjectUpdateLocation);
		try {
			GitAdapter.copyFolder(src, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		/**
		 * Performs cleanup, so that at the end we delete the copy of the project
		 */
	private void DeleteProject(String dir) {
		File copyProject = new File(dir);
		//delete all files first
		for (File c: copyProject.listFiles()) {
			if (c.isDirectory()) {
				DeleteProject(c.getAbsolutePath());
			} else {
				c.delete();
			}
		}
		copyProject.delete();			
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