package edu.ncsu.mavenbot.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.jgit.api.errors.GitAPIException;

import edu.ncsu.mavenbot.adapters.GitAdapter;
import edu.ncsu.mavenbot.adapters.Logger;
import edu.ncsu.mavenbot.adapters.MavenOverseer;
import edu.ncsu.mavenbot.model.BotResponseModel;

public class DecisionMakingService {

	private GitAdapter gitAdapter;
	private MavenOverseer mavenOverseer;
	private BotResponseModel botResponseModel;
	private String project;
	private static Logger logger = Logger.getLogger(DecisionMakingService.class);
	public BotResponseModel executeCommand(String command){
		//logger.debug("Executing Command : "+command);
		try {
			if (command.equals("ls")) {
				botResponseModel.setResponseMessage(gitAdapter.listRepo());
				botResponseModel.setSuccessful(true);
			}
			if (command.contains("cp_")) {
				String porojectName = command.split("_")[1];
				botResponseModel.setResponseMessage(gitAdapter.changeRepo(porojectName));
				botResponseModel.setSuccessful(true);
			}
			if (command.contains("ld_")) {
				String project = command.split("_")[1];
				if (project == null || project.equalsIgnoreCase("null") || project.trim().length() == 0 || project.trim().equals("")) {
					botResponseModel.setSuccessful(false);
					botResponseModel.setResponseMessage("This bot has not yet been configured to monitor a project.");
					logger.error("This bot has not yet been configured to monitor a project.");
				}
				else{
					gitAdapter.changeRepo(project);
					botResponseModel.setSuccessful(true);
					botResponseModel.setResponseMessage(mavenOverseer.listUpdateableDependencies());
				}
			}
			if (command.contains("up_")) {
				project = command.trim().split("_")[1];
				if (project == null || project.equalsIgnoreCase("null") || project.trim().length() == 0 || project.equals("")) {
					botResponseModel.setSuccessful(false);
					botResponseModel.setResponseMessage("This bot has not yet been configured to monitor a project.");
					logger.error("This bot has not yet been configured to monitor a project.");
				}
				else{
					gitAdapter.changeRepo(project);
					botResponseModel.setResponseMessage(mavenOverseer.findLatestGoodVersions());
					botResponseModel.setSuccessful(true);
				}
			}
			if (command.contains("upversion_")) {
				String after = command.split("_")[1];
				int depIndex = -1;
				try {
					depIndex = Integer.parseInt(after);
				} catch (NumberFormatException e) {
					botResponseModel.setResponseMessage("That is an invalid dependency selection.");
					botResponseModel.setSuccessful(false);
					logger.error("That is an invalid dependency selection.", e);
				}
				boolean success = mavenOverseer.updateDependencyToLatestGoodVersion(depIndex);
				if (success) {
					//GitAdapter adapter = new GitAdapter();
					boolean success2 = gitAdapter.pushRepo(project); //need to push back up
					if (success2) {
						botResponseModel.setResponseMessage("Update Successful");
						botResponseModel.setSuccessful(true);
					}
				}
				else{
					botResponseModel.setSuccessful(false);
					botResponseModel.setResponseMessage("Failure");
					logger.error("Failure");
				}
			}
		} catch (MalformedURLException e) {
				botResponseModel.setSuccessful(false);
				botResponseModel.setResponseMessage("I could not do the processing. Please enter the information again.");
				logger.error("Malformed URL detected. ", e);
		} catch (IOException e) {
			botResponseModel.setSuccessful(false);
			botResponseModel.setResponseMessage("I could not do the processing. Please enter the information again.");
			logger.error("File related error. ", e);
		} catch (GitAPIException e) {
			botResponseModel.setSuccessful(false);
			botResponseModel.setErrorMessage("I could not do the processing. Please enter the information again.");
			logger.error("Git Error", e);
		}
		return botResponseModel;
	}
	public void setGitAdapter(GitAdapter gitAdapter) {
		this.gitAdapter = gitAdapter;
	}
	public void setMavenOverseer(MavenOverseer mavenOverseer) {
		this.mavenOverseer = mavenOverseer;
	}
	public void setBotResponseModel(BotResponseModel botResponseModel) {
		this.botResponseModel = botResponseModel;
	}
	public void setProject(String project) {
		this.project = project;
	}
}
