package edu.ncsu.mavenbot.test;

import java.io.*;
import java.util.*;

import edu.ncsu.mavenbot.adapters.GitAdapter;
import edu.ncsu.mavenbot.model.Dependency;

public class MavenOverseer{
	
	//the name of the directory that contains the project to look for updates
	public String currentDirectory;
	public String projectLocation;
	
	//the name of the directory to copy the project to to look for updates
	private String projectUpdateLocation;
	private String executablePath;
	private String defaultArg;
	private List<Dependency> dependencyList;
	
	public static void main(String[] args) { 
		MavenOverseer runner = new MavenOverseer();
		//ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd");
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd");
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
		System.out.println(runner.findLatestGoodVersions());
	}
	
	public MavenOverseer() {
		dependencyList = new ArrayList<Dependency>();		
	}
	
	public String listUpdateableDependencies() {
		//wipe previous list
		dependencyList.clear();
		//for now, since mocking data skip these steps
		this.makeDirectory();
		this.copyProject();
		this.findDependencies();
		this.findUpdatesForDependencies();
		this.deleteProject(projectUpdateLocation);
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
		/*if (atLeastOneUpdate) {
			return listDeps.toString();
		}*/
		return listDeps.toString();
		//no updates return failure
		//return "\"No dependencies for this project have newer versions available.\"";
	}
	
	public boolean updateDependencyToLatestGoodVersion(int parIndex) {
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
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
				"cd " + projectLocation + " && mvn versions:use-next-releases -Dincludes=" + 
				dependencyGroup + ":" + artifactID);*/
		ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), getDefaultArg(),
				"cd " + projectLocation + " && mvn versions:use-next-releases -Dincludes=" + 
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
	public String findLatestGoodVersions() {
		//need to update one at a time -> find list of current dependencies (then wipe it)
		dependencyList.clear();
		this.makeDirectory();
		this.copyProject();
		this.findDependencies();
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
				/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
						"cd " + projectUpdateLocation + " && mvn versions:use-next-releases -Dincludes=" + 
						currDep.groupID + ":" + currDep.artifactID);*/
				ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), getDefaultArg(), 
						"cd " + projectUpdateLocation + " && mvn versions:use-next-releases -Dincludes=" + 
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
					if (compileProject() && testsPass() && updateAvailable) {
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
			this.deleteProject(projectUpdateLocation);
			this.makeDirectory();
			this.copyProject();
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
	
	private boolean testsPass() {
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "cd " + projectUpdateLocation + "&& mvn test");*/
		ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), 
				getDefaultArg(), "cd " + projectUpdateLocation + "&& mvn test");
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
	
	private boolean compileProject() {
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "cd " + projectUpdateLocation + " && mvn compile");*/
		ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), 
				getDefaultArg(), "cd " + projectUpdateLocation + " && mvn compile");
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
	
	private void findUpdatesForDependencies() {
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
				"cd " + projectUpdateLocation + " && mvn versions:use-next-releases");*/
		ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), getDefaultArg(), 
				"cd " + projectUpdateLocation + " && mvn versions:use-next-releases");
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
	
	private void findDependencies() {
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", 
				"cd " + projectUpdateLocation + " && mvn -o dependency:list");*/
		System.out.println("Executing command : "+getExecutablePath()+getDefaultArg()+"cd " + projectUpdateLocation + " && mvn -o dependency:list");
		
		ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), getDefaultArg(), 
				"cd " + projectUpdateLocation + " && mvn -o dependency:list");
		builder.redirectErrorStream(true);
		try {
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			String output;
			StringBuilder sb = new StringBuilder();
			while( (output = r.readLine()) !=null){
				sb.append(output);
			}
			System.out.println("Console Output for listing dependency: ");
			System.out.println(sb.toString());
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
	
	private void makeDirectory() {
		File file = new File(projectUpdateLocation);
		/*if(file.exists())
        {
        	GitAdapter.deleteFolder(file);
        }*/
        file.mkdir();
	}
	
	private void copyProject() {
		File src= new File(projectLocation);
		File dest= new File(projectUpdateLocation);
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
	private void deleteProject(String dir) {
		File copyProject = new File(dir);
		//delete all files first
		for (File c: copyProject.listFiles()) {
			if (c.isDirectory()) {
				deleteProject(c.getAbsolutePath());
			} else {
				c.delete();
			}
		}
		copyProject.delete();			
	}

		public String getCurrentDirectory() {
			return currentDirectory;
		}

		public void setCurrentDirectory(String currentDirectory) {
			this.currentDirectory = currentDirectory;
		}

		public String getProjectLocation() {
			return projectLocation;
		}

		public void setProjectLocation(String projectLocation) {
			this.projectLocation = projectLocation;
		}

		public String getProjectUpdateLocation() {
			return projectUpdateLocation;
		}

		public void setProjectUpdateLocation(String projectUpdateLocation) {
			this.projectUpdateLocation = projectUpdateLocation;
		}

		public List<Dependency> getDependencyList() {
			return dependencyList;
		}

		public void setDependencyList(List<Dependency> dependencyList) {
			this.dependencyList = dependencyList;
		}

		public String getExecutablePath() {
			return executablePath;
		}

		public void setExecutablePath(String executablePath) {
			this.executablePath = executablePath;
		}

		public String getDefaultArg() {
			return defaultArg;
		}

		public void setDefaultArg(String defaultArg) {
			this.defaultArg = defaultArg;
		}
		
		
	
	/*private class Dependency {
		public String groupID;
		public String artifactID;
		public String currVersion;
		public List<String> newerVersions = new ArrayList<String>();
		public String maxUpdateableVersion;
		
		public boolean equals(Object o) {
			Dependency other = (Dependency) o;
			return this.groupID.equals(other.groupID) && this.artifactID.equals(other.artifactID);
		}
	}*/
}