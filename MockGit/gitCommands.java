import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.*;
 
public class gitCommands {
    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String CurrentDirectory= System.getProperty("user.dir");
        // JavaScript code in a String
        engine.eval(Files.newBufferedReader(Paths.get(CurrentDirectory+"/src/Jsfunctions.js"), StandardCharsets.UTF_8));;
        
        Invocable inv = (Invocable) engine;
 
        // invoke the global function named "hello"
        inv.invokeFunction("listBrnaches", "Scripting!!" );
    }
}
