package edu.ncsu.mavenbot.controller;


import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.ncsu.mavenbot.controller.GitAdapter;
import edu.ncsu.mavenbot.model.GitAdaptor;
import edu.ncsu.mavenbot.controller.MavenOverseer;



@Controller
@RequestMapping("/decisioncontroller")
public class DecisionMakingController {
	
	MavenOverseer mvn = new MavenOverseer();
	String project = "";
	
	@ResponseBody
	@RequestMapping(value="/executecommand/{command}", method = RequestMethod.GET)
	public String getMovie(@PathVariable String command, ModelMap model) throws IOException, GitAPIException {
		if (command.equals("ls")) {
			
			GitAdapter adapter = new GitAdapter();
			return "{\"responseMessage\":" + adapter.listRepo() + "}";
		}
		
		if (command.contains("cp_")) {
			//get the project name
			String projectName = command.split("_")[1];
			project = projectName;
			GitAdapter adapter = new GitAdapter();
			return "{\"responseMessage\":\"" + adapter.changeRepo(projectName) + "\"}";
		}
		
		if (command.equals("ld")) {
	//		mvn.projectName = project;
			return "{\"responseMessage\":" + mvn.ListUpdateableDependencies() + "}";
		}
		
		if (command.equals("up")) {
			return "{\"responseMessage\":" + mvn.FindLatestGoodVersions() + "}";
		}
		if (command.contains("up_")) {
			String after = command.split("_")[1];
			return "{\"responseMessage\":"+"\"" + 
					mvn.UpdateDependencyToLatestGoodVersion(Integer.parseInt(after)) + "\"}";
		}
		
		return "";
	}
	
	
	@RequestMapping(value="/getjsonresponse", method = RequestMethod.GET)
	public @ResponseBody GitAdaptor getJSONResponse() {

		GitAdaptor gitAdaptor = new GitAdaptor();
		
		gitAdaptor.setUrl("github.ncsu.edu");
		gitAdaptor.setCommand("git clone"+gitAdaptor.getUrl());
		gitAdaptor.setUserName("psabhyan");
		gitAdaptor.setToken("ffhoofnasjnkPJOGOGN");
		
		return gitAdaptor;

	}

}
