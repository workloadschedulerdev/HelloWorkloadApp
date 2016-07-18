package com.ibm.twa.bluemix.samples;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.twa.applab.client.WorkloadService;
import com.ibm.tws.api.ApiClient;
import com.ibm.tws.api.ApiException;
import com.ibm.tws.api.Configuration;
import com.ibm.tws.api.ProcessApi;
import com.ibm.tws.api.ProcessHistoryApi;
import com.ibm.tws.api.ProcessLibraryApi;
import com.ibm.tws.api.auth.HttpBasicAuth;
import com.ibm.tws.model.Process;
import com.ibm.tws.model.ProcessHistoryInstance;
import com.ibm.tws.model.ProcessLibrary;
import com.ibm.tws.model.CommandStep;
import com.ibm.tws.model.Step;
import com.ibm.tws.simpleui.bus.TaskInstanceStatus;

public class HelloWorkloadLocalTest {
	
	boolean connected = false;
	WorkloadService ws;
	long myProcessId;
	final String engineName = "BASE";
	final String engineOwner = "smadmin";
	final String tenantId = "AZ"; //The tenantId cannot be null or "", use your id or leave the default value
	// If true adds <br> instead of \n for each output row
	boolean htmlOut = true;
	ApiClient apiClient;
	String vcap = "{\"WorkloadScheduler-0.1\": [{"+
	         "\"name\": \"WorkloadScheduler-ko\"," +
	         " \"label\": \"WorkloadScheduler-0.1\"," +
	         " \"plan\": \"free\"," +
	         " \"credentials\": {" +
	         "   \"address\": \"sic37wastagmxo-222.wa.ibmserviceengage.com\"," +
	         "   \"prefix\": \"MH\"," +
	         "   \"userId\": \"luca.lazzaro%40it.ibm.com\"," +
	         "   \"password\": \"hZJvAFObgTDI6ShGCjw8NC30aqen%3Fi\"," +
	         "   \"url\": \"https://luca.lazzaro%40it.ibm.com:hZJvAFObgTDI6ShGCjw8NC30aqen%3Fi@sic37wastagmxo-222.wa.ibmserviceengage.com/ibm/TWSWebUI/Simple/rest?tenantId=MH&engineName=engine&engineOwner=engine\" " +
	         "}" +
	      "}" +
	   "]" +
	"}";


	
	private static HelloWorkloadLocalTest INSTANCE = new HelloWorkloadLocalTest();
	
	public void helloWorkloadConnect(OutputStream o) throws JSONException{
		 apiClient = Configuration.getDefaultApiClient();
		 apiClient.setBasePath("YOUR LOCALHOST URL");
		 // Configure HTTP basic authorization: basicAuth
	     HttpBasicAuth basicAuth = (HttpBasicAuth) apiClient.getAuthentication("basicAuth");
	     basicAuth.setUsername("YOUR USERNAME");
	     basicAuth.setPassword("YOUR PASSWORD");
		 
		 PrintWriter out = new PrintWriter(o);
		 String vcapJSONString = vcap;
         Object jsonObject = JSON.parse(vcapJSONString);
         JSONObject json = (JSONObject)jsonObject;
         String key;
         JSONArray twaServiceArray =null;
         
         out.println("Looking for Workload Automation Service...");
         out.flush();

         for (Object k: json.keySet())
         {
             key = (String )k;
             out.println("key:" + k);
             if (key.startsWith("WorkloadScheduler-0.1"))
             {
                 twaServiceArray = (JSONArray)json.get(key);
                 out.println("Workload Automation service found!");
                 out.flush();
                 break;
             }                       
         }
          if (twaServiceArray== null){
        	  out.println("Could not connect: I was not able to find the Workload Automation service!");
        	  out.println(vcapJSONString);
        	  out.println(jsonObject);
        	  out.println(json);
        	  out.flush();
        	  return;
          }
         
          JSONObject twaService = (JSONObject)twaServiceArray.get(0); 
          JSONObject credentials = (JSONObject)twaService.get("credentials");
          String url = (String) credentials.get("url");
          String hostname = (String) credentials.get("address");
          String userBid = (String) credentials.get("userId");
          String prefix = (String) credentials.get("prefix");
          String password = (String) credentials.get("password");
          
          out.println("Starting Workload Automation connection..");
          out.flush();
          
          StringBuffer info = new StringBuffer();
          info.append("https://")
            .append(userBid).append(":")
            .append(password)
            .append("@").append(hostname)
            .append(":443")
            .append("/ibm/TWSWebUI/Simple/rest?tenantId=")
            .append(prefix)
            .append("&engineName=engine&engineOwner=engine");
          
          try {
        	  WorkloadService.disableCertificateValidation();
              ws = new WorkloadService(url);
              ws.setDebugMode(true);
          }catch(Exception e){
        	  out.println("Could not connect to the service: " + e.getClass().getName() + " " + e.getMessage());
        	  out.flush();
        	  return;
          }
          connected = true;
          out.println("Connection obtained.");
          out.flush();
	}
	
	public void helloWorkloadCreate(OutputStream o) throws ApiException{
			PrintWriter out = new PrintWriter(o);
			//Create Process and Step
			ProcessApi apis = new ProcessApi();
			ProcessLibraryApi plApis = new ProcessLibraryApi();
			
			ProcessLibrary lib = plApis.listProcessLibrary(tenantId, engineName, engineOwner).get(0);
			int processLibraryId = lib.getId();
			Process process = new Process();
	    	process.setName("Hello World Process");
			process.setProcesslibraryid(processLibraryId);
			List<Step> steps = new ArrayList<Step>();
	        Step step = new Step();
	        
	        
	        
	        CommandStep command = new CommandStep();
	        command.setAgent("FBRILLANTE_1");
	        command.setCommand("echo Hello World");
	        step.setCommandStep(command);
	        steps.add(step);
	        process.setSteps(steps);
	        
	        
		    try {
		    	println(out,"Creating and enabling the process");
		    	out.flush();
		    	Process result = apis.createProcess(process, tenantId, engineName, engineOwner);
		    	myProcessId = result.getId();
		    	String id = Integer.toString(result.getId());
		    	apis.toggleProcessStatus(id, tenantId, engineName, engineOwner);
		    	if (result!=null) {
		    		println(out,"Running the process");
			    	out.flush();
			    	apis.runNowProcess(id, tenantId, engineName, engineOwner);
		    	}
		    }
				catch (Exception e) {
				out.println("Could not connect complete the operation: " + e.getClass().getName() + " " + e.getMessage());
	        	out.flush();
	        	  
			} 
		    
	}
	
	public void helloWorkloadTrack(OutputStream o){
		PrintWriter out = new PrintWriter(o);
	    try {
	    	ProcessHistoryApi ops = new ProcessHistoryApi();
	    	List<ProcessHistoryInstance> list = ops.listProcessHistory("313", tenantId, engineName, engineOwner);
	    	for (ProcessHistoryInstance processHistoryInstance : list) {
                out.println("Started: " + processHistoryInstance.getStartdate() + 
                        "\n Completed steps: " + processHistoryInstance.getCompletedsteps() + 
                        "\n Is completed: "  + (processHistoryInstance.getStatus() == TaskInstanceStatus.COMPLETED));
                if ((processHistoryInstance.getStatus() == TaskInstanceStatus.COMPLETED)) {
                    System.out.println("The process has completed all the steps in: " + processHistoryInstance.getElapsedtime()/60000 + " minutes");                    
                }
                
            }

		} catch (Exception e) {
			out.println("Could not connect complete the operation: " + e.getClass().getName() + " " + e.getMessage());
        	out.flush();  
		}    
	}
	
	public static HelloWorkloadLocalTest getInstance(){
		return INSTANCE;
	}
	
	public long getMyProcessId() {
		return myProcessId;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void println(PrintWriter out,String msg){
		if (this.htmlOut){
			out.print(msg + "<br>");
		}else{
			out.println(msg);
		}
	}
	
	public static void main(String[] args)  throws Exception{
		HelloWorkloadLocalTest h = HelloWorkloadLocalTest.getInstance();
		h.helloWorkloadConnect(System.out);
		h.helloWorkloadCreate(System.out);
		h.helloWorkloadTrack(System.out);
	}
}
