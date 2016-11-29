package edu.ncsu.mavenbot.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.ncsu.mavenbot.service.DecisionMakingService;


public class GitAdapter {
	
	
	//really should make it a hashmap or dictionary or something of that sort, but oh well
	private List<String> possibleRepos = new ArrayList<String>();
	private String githubApiUrl;
	private String repoUrl;
	private String currentDirectory;
	private String executablePath;
	private String defaultArg;
	private String gitPushUrl;
	private static Logger logger = Logger.getLogger(GitAdapter.class);
	
	public String changeRepo(String repoName) throws IOException, GitAPIException {
		if (!possibleRepos.contains(repoName)) {
			logger.error("That is not a valid repo.");
			return "That is not a valid repo.";
		}
		File file = new File(currentDirectory+"/try");
		if(file.exists())
        {
        	deleteFolder(file);
        }
        file.mkdir();
        logger.debug("Cloning from " + repoUrl + " to " + currentDirectory);
        try (Git result = Git.cloneRepository()
                .setURI(repoUrl+repoName)
                .setDirectory(file)
                .call()) {
        	  logger.debug("Cloned repository to "+file.getAbsolutePath());
        }
        return "success";
        }
	public String listRepo() throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		possibleRepos = new ArrayList<String>();
		String charset = "UTF-8";
		String responseBody = null ;
		StringBuilder sb = new StringBuilder();
		logger.debug("Trying to connect : "+ githubApiUrl);
		URLConnection connection = new URL(githubApiUrl).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		try (Scanner scanner = new Scanner(response)) {
		    responseBody = scanner.useDelimiter("\\A").next();
		}
		catch (Exception e) {
			logger.error("Exception", e);
		}
		 try {
			JSONArray obj = new JSONArray(responseBody);
			 sb.append("\"");
			 for (int i =0;i<obj.length();i++)
			 {
				 JSONObject obj1 = obj.getJSONObject(i);
				 sb.append(obj1.getString("name")+System.lineSeparator());
				 //add it to the list
				 possibleRepos.add(obj1.getString("name"));
			 }
			 sb.append("\"");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception : ", e);
		}
		 catch (Exception e) {
			 logger.error("Exception : ", e);
		}
		 return sb.toString();
	}
	
	public boolean pushRepo(String repoName) {
		if (repoName!=null) {
			logger.debug("Committing changes to repository : " + repoName);
			ProcessBuilder builder = new ProcessBuilder(getExecutablePath(), getDefaultArg(),
					"cd " + currentDirectory + "/try" + " && git commit -am \"Update dependency\"");
			try {
				Process p = builder.start();
				builder.redirectErrorStream(true);
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while (true) {
					line = r.readLine();
					logger.debug(line);
					System.out.println(line);
					if (line == null) {
						break;
					}
					//System.out.println(line);
				}
			} catch (Exception e) {
				logger.error("Exception while commiting to repo : ", e);
				return false;
			}
			logger.debug("Pushing Repo " + repoName);
			ProcessBuilder builder2 = new ProcessBuilder(getExecutablePath(), getDefaultArg(),
					"cd " + currentDirectory + "/try" + " && git push --repo " + gitPushUrl + repoName);
			try {
				Process p2 = builder2.start();
				builder2.redirectErrorStream(true);
				BufferedReader r2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				String line2;
				while (true) {
					line2 = r2.readLine();
					if (line2 == null) {
						break;
					}
					System.out.println(line2);
				}

			} catch (Exception e) {
				logger.error("Exception while pushing: ", e);
				return false;
			} 
		}
		else
		{
			logger.error("Repo name is null");
			return false;
		}
		return true;
	}
	
	public static void copyFolder(File src, File dest)
	    	throws IOException{

	    	if(src.isDirectory()){

	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		}

	    		//list all the directory contents
	    		String files[] = src.list();

	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}

	    	}else{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
	    	        OutputStream out = new FileOutputStream(dest);

	    	        byte[] buffer = new byte[1024];

	    	        int length;
	    	        //copy the file content in bytes
	    	        while ((length = in.read(buffer)) > 0){
	    	    	   out.write(buffer, 0, length);
	    	        }

	    	        in.close();
	    	        out.close();
	    	        logger.debug("File copied from "+ src + "  to " + dest);
	    	}
	    }
	
	public static void deleteFolder(File directory)
    {
    	if(!directory.exists()){

           System.out.println("Directory does not exist.");
           logger.error("Directory does not exist.");
           System.exit(0);
 
        }else{
           try{
               delete(directory);
           }catch(IOException e){
               logger.error("File operation exception", e);
           }
        }

    	logger.debug("Done");
    	//System.out.println("Done");
    }

    public static void delete(File file)
    	throws IOException{

    	if(file.isDirectory()){

    		//directory is empty, then delete it
    		if(file.list().length==0){

    		   file.delete();
    		   //System.out.println("Directory is deleted : "+ file.getAbsolutePath());
    		   logger.debug("Directory is deleted : " + file.getAbsolutePath());

    		}else{

    		   //list all the directory contents
        	   String files[] = file.list();

        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);

        	      //recursive delete
        	     delete(fileDelete);
        	   }

        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     //System.out.println("Directory is deleted : " + file.getAbsolutePath());
        	     logger.debug("Directory is deleted : " + file.getAbsolutePath());
        	   }
    		}
    	}else{
    		//if file, then delete it
    		file.delete();
    		//System.out.println("File is deleted : " + file.getAbsolutePath());
    		logger.debug("Directory is deleted : " + file.getAbsolutePath());
    	}
    }

	public String getCurrentDirectory() {
		return currentDirectory;
	}
	public void setCurrentDirectory(String currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	public String getGithubApiUrl() {
		return githubApiUrl;
	}
	public void setGithubApiUrl(String githubApiUrl) {
		this.githubApiUrl = githubApiUrl;
	}
	public String getRepoUrl() {
		return repoUrl;
	}
	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
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
	public String getGitPushUrl() {
		return gitPushUrl;
	}
	public void setGitPushUrl(String gitPushUrl) {
		this.gitPushUrl = gitPushUrl;
	}
	

}
