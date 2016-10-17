import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;

public class SwitchProject {

	public static void main(String[] args)
	{
		String repo=null;
		Scanner s = new Scanner(System.in);
		//System.out.println("Working on "+repo+".");
		//System.out.println("mihir");
		String CurrentDirectory= System.getProperty("user.dir");
		//System.out.println(CurrentDirectory);
		
		File file = new File(CurrentDirectory+"/BS-Mock");
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		//System.out.println("Select Repo");
		System.out.println(Arrays.toString(directories));
		System.out.println("Select Repo");
		repo = s.next();
		
		File source = new File(CurrentDirectory+"/BS-Mock/"+repo);
		File dest = new File(CurrentDirectory+"/local/"+repo);
		File file1 = new File(CurrentDirectory+"/local");
        //deleting User folder
				
        if(file1.exists())
        {
        	deleteFolder(file1);
        }
        file1.mkdir();
		

    	//make sure source exists
    	if(!source.exists()){

           System.out.println("Directory does not exist.");
           //just exit
           System.exit(0);

        }else{

           try{
        	copyFolder(source,dest);
           }catch(IOException e){
        	e.printStackTrace();
        	//error, just exit
                System.exit(0);
           }
        }

    	System.out.println("Done");
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
   // 	String repo="TrialDirectory";
  //  	String SRC_FOLDER = System.getProperty("user.dir");
 //   	SRC_FOLDER+="/User/"+repo;
//    	File directory = new File(SRC_FOLDER);

    	//make sure directory exists
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

