package maven;
import java.io.*;
import java.util.*;

public class MavenOverseer {
	
	//the name of the directory that contains the project to look for updates
	public String ProjectLocation = "C:\\Users\\Daniel\\git\\BuildSlackers\\TestProjectID";
	
	//the name of the directory to copy the project to to look for updates
	private String ProjectUpdateLocation = "C:\\Users\\Daniel\\git\\BuildSlackers\\TestUpdates";
	
	private List<Dependency> dependencyList;
	
	public static void main(String[] args) {
		MavenOverseer runner = new MavenOverseer();
		runner.Run();
	}
	
	public MavenOverseer() {
		dependencyList = new ArrayList<Dependency>();
	}
	
	public void Run() {
		this.MakeDirectory();
		this.CopyProject();
		System.out.println("Project copied");
		Scanner in = new Scanner(System.in);
		in.nextLine();
		this.FindDependencies();
		this.FindUpdatesForDependencies();
		this.DeleteProject();
		for (int i = 0; i < dependencyList.size(); i++) {
			Dependency curr = dependencyList.get(i);
			System.out.print(curr.groupID + " " + curr.artifactID + " " + curr.currVersion + " " + curr.newerVersions);
			System.out.println();
		}
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
		
		public boolean equals(Object o) {
			Dependency other = (Dependency) o;
			return this.groupID.equals(other.groupID) && this.artifactID.equals(other.artifactID);
		}
	}
		
}
