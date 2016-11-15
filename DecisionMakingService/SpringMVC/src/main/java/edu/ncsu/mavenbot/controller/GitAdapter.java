package edu.ncsu.mavenbot.controller;

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
import org.json.JSONObject;


public class GitAdapter {
	
	//really should make it a hashmap or dictionary or something of that sort, but oh well
	private List<String> possibleRepos = new ArrayList<String>();
	
	String changeRepo(String repoName) throws IOException, GitAPIException {
		if (!possibleRepos.contains(repoName)) {
			return "That is not a valid repo.";
		}
		String RepoUrl="https://github.com/BuildSlackers/";
		
		String CurrentDirectory=System.getProperty("user.dir");
		System.out.println("Current Path= "+CurrentDirectory);
		File file = new File(CurrentDirectory+"/try");
		if(file.exists())
        {
        	deleteFolder(file);
        }
        file.mkdir();
        System.out.println("Cloning from " + RepoUrl + " to " + CurrentDirectory);
        try (Git result = Git.cloneRepository()
                .setURI(RepoUrl+repoName)
                .setDirectory(file)
                .call()) {
        	  System.out.println("Having repository: " + result.getRepository().getDirectory());
        }
        	//System.out.println("These are the branches" +listRepo());
        return "success";
        }
        	
	
	public String listRepo() throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		possibleRepos = new ArrayList<String>();
		String charset = "UTF-8";
		String responseBody ;
		StringBuilder sb = new StringBuilder();
		String url = "https://api.github.com/users/BuildSlackers/repos";
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		//InputStream response = new URL(url).openStream();
		try (Scanner scanner = new Scanner(response)) {
		    responseBody = scanner.useDelimiter("\\A").next();
		    System.out.println(responseBody);
		}
		 JSONArray obj = new JSONArray(responseBody);
		 sb.append("\"");
		 for (int i =0;i<obj.length();i++)
		 {
			 JSONObject obj1 = obj.getJSONObject(i);
			 sb.append(obj1.getString("name")+"\\n");
			 //add it to the list
			 possibleRepos.add(obj1.getString("name"));
			 //System.out.println(obj1.getString("name"));
			 
		 }
		 sb.append("\"");
		 return sb.toString();
		
	}
	
	public boolean pushRepo(String repoName) {
		String repo = "https://danwrice:ProjectsVilla16@github.com/BuildSlackers/";
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", 
				"/c", "cd " + System.getProperty("user.dir") + "/try" + " && git commit -am \"Update dependency\"");
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
				System.out.println(line);
			}
			ProcessBuilder builder2 = new ProcessBuilder("cmd.exe", 
					"/c", "cd " + System.getProperty("user.dir") + "/try" + " && git push --repo " + repo + repoName);
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
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	    		   System.out.println("Directory copied from "
	                              + src + "  to " + dest);
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
	    	        System.out.println("File copied from " + src + " to " + dest);
	    	}
	    }
	
	public static void deleteFolder(File directory)
    {
    	if(!directory.exists()){

           System.out.println("Directory does not exist.");
           System.exit(0);
 
        }else{

           try{

               delete(directory);

           }catch(IOException e){
               e.printStackTrace();
           }
        }

    	System.out.println("Done");
    }

    public static void delete(File file)
    	throws IOException{

    	if(file.isDirectory()){

    		//directory is empty, then delete it
    		if(file.list().length==0){

    		   file.delete();
    		   System.out.println("Directory is deleted : "+ file.getAbsolutePath());

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
        	     System.out.println("Directory is deleted : " + file.getAbsolutePath());
        	   }
    		}
    	}else{
    		//if file, then delete it
    		file.delete();
    		System.out.println("File is deleted : " + file.getAbsolutePath());
    	}
    }
	
}
