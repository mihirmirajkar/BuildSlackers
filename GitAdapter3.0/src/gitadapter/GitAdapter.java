package gitadapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONObject;


public class GitAdapter {
	public static void main(String[] args) throws IOException, GitAPIException {
		String RepoUrl="https://github.com/BuildSlackers/";
		String repoName="a";
		String CurrentDirectory="C:/Users/Sumer/Desktop/My Files/NCSU Assignments/SE/temp";
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
      //  request();
        }
        
        
	private static void request() throws MalformedURLException, IOException {	//Need to fix this
		// TODO Auto-generated method stub
		String charset = "UTF-8";
		String responseBody ;
		String url = "https://api.github.com/users/BuildSlackers/repos";
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		//InputStream response = new URL(url).openStream();
		try (Scanner scanner = new Scanner(response)) {
		    responseBody = scanner.useDelimiter("\\A").next();
		    System.out.println(responseBody);
		}
		 JSONObject obj = new JSONObject(responseBody);
		 JSONArray arr = obj.getJSONArray("name");
		 for (int i = 0; i < arr.length(); i++)
		     System.out.println(arr.getInt(i));
		 
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
               System.exit(0);
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
