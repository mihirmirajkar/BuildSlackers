package edu.ncsu.mavenbot.model;

import java.util.ArrayList;
import java.util.List;


public class Dependency {

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
