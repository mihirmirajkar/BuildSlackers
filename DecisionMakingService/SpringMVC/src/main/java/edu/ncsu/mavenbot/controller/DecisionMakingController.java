package edu.ncsu.mavenbot.controller;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.ncsu.mavenbot.model.GitAdaptor;

@Controller
@RequestMapping("/decisioncontroller")
public class DecisionMakingController {
	
	@RequestMapping(value="/showcommand/{command}", method = RequestMethod.GET)
	public String getMovie(@PathVariable String command, ModelMap model) {

		model.addAttribute("command", command);
		return "commandDetails";

	}
	
	@RequestMapping(value="getjsonresponse", method = RequestMethod.GET)
	public @ResponseBody GitAdaptor getJSONResponse() {

		GitAdaptor gitAdaptor = new GitAdaptor();
		
		gitAdaptor.setUrl("github.ncsu.edu");
		gitAdaptor.setCommand("git clone"+gitAdaptor.getUrl());
		gitAdaptor.setUserName("psabhyan");
		gitAdaptor.setToken("ffhoofnasjnkPJOGOGN");
		
		return gitAdaptor;

	}

}
