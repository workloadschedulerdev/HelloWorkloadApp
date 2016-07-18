/*********************************************************************
 *
 * Licensed Materials - Property of IBM
 * Product ID = 5698-WSH
 *
 * Copyright IBM Corp. 2015. All Rights Reserved.
 *
 ********************************************************************/
package com.ibm.twa.bluemix.samples;

import java.io.PrintWriter;
import java.io.Writer;
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
import com.ibm.tws.simpleui.bus.TaskInstanceStatus;

import com.ibm.tws.model.Step;

public class HelloWorkload {
	// Once connected and authenticated correctly, this boolean is true
	boolean connected = false;
	// Hold the instance of the WorkloadService
	WorkloadService ws;
	// Holds the last created process id.
	long myProcessId;
	// If true adds <br> instead of \n for each output row
	boolean htmlOut = true;
	final String engineName = "engine";
	final String engineOwner = "engine";
	String tenantId = ""; //The tenantId cannot be null or "", use your id or leave the default value
	String workloadServiceName = "WorkloadScheduler";	 
	String agentName = "_CLOUD";
	ApiClient apiClient;
	
	//Enable debug mode if needed
	boolean debugMode = false;
	
	// Default empty constructor.
	public HelloWorkload(){};
		
	
	/**
	 *  Connects and authenticates to the server, exploring the content of 
	 *  VCAP_SERVICES content.
	 *  
	 *  @param o: Output Stream to write useful info
	 */
	public void helloWorkloadConnect(Writer o) throws JSONException{
		 PrintWriter out = new PrintWriter(o);
		 String vcapJSONString = System.getenv("VCAP_SERVICES");
         Object jsonObject = JSON.parse(vcapJSONString);
         JSONObject json = (JSONObject)jsonObject;
         String key;
         JSONArray twaServiceArray =null;
         
         println(out,"Looking for Workload Automation Service...");
         out.flush();

         for (Object k: json.keySet())
         {
             key = (String )k;            
             if (key.startsWith(workloadServiceName))
             {
                 twaServiceArray = (JSONArray)json.get(key);
                 println(out,"Workload Automation service found!");
                 out.flush();
                 break;
             }                       
         }
          if (twaServiceArray== null){
        	  println(out,"Could not connect: I was not able to find the Workload Automation service!");
        	  println(out,"This is your VCAP services content");
        	  println(out,vcapJSONString);
        	  out.flush();
        	  return;
          }
         
          JSONObject twaService = (JSONObject)twaServiceArray.get(0); 
          JSONObject credentials = (JSONObject)twaService.get("credentials");
         
                  
          
          println(out,"Starting Workload Automation connection..");
          out.flush();
          
          
          String url = (String) credentials.get("url");
          int index  = url.indexOf("tenantId=") +9 ;                    
          String prefix = url.substring(index, index+2);
          println(out,"prefix="+ prefix);
          tenantId = prefix;
          agentName = prefix+agentName;
          try {
        	  WorkloadService.disableCertificateValidation();
              ws = new WorkloadService(url);
              ws.setDebugMode(debugMode);
          }catch(Exception e){
        	  println(out,"Could not connect to the service: " + e.getClass().getName() + " " + e.getMessage());
        	  out.flush();
        	  return;
          }
          connected = true;
          println(out,"Connection obtained.");
          out.flush();

 		  apiClient = Configuration.getDefaultApiClient();
 		  apiClient.setBasePath(url);
 		  // Configure HTTP basic authorization: basicAuth
 	      HttpBasicAuth basicAuth = (HttpBasicAuth) apiClient.getAuthentication("basicAuth");
 	      basicAuth.setUsername("YOUR USERNAME");
 	      basicAuth.setPassword("YOUR PASSWORD");
	}
	
	/**
	 * Creates a very simple hello world process
	 * 
	 * @param o
	 * @throws ApiException 
	 */
	public void helloWorkloadCreate(Writer o) throws ApiException{
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
	        command.setAgent(agentName);
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
				
			} catch (Exception e) {
				println(out,"Could not connect complete the operation: " + e.getClass().getName() + " " + e.getMessage());
	        	out.flush();
	        	  
			} 
		    
	}
	
	/**
	 * Tracks a process created with a previous call to helloWorkloadCreate
	 * 
	 * @param o
	 */	
	public void helloWorkloadTrack(Writer o){
		PrintWriter out = new PrintWriter(o);
		
	    try {
	    	ProcessHistoryApi ops = new ProcessHistoryApi();
	    	List<ProcessHistoryInstance> list = ops.listProcessHistory(Long.toString(this.myProcessId), tenantId, engineName, engineOwner);
            for (ProcessHistoryInstance processHistoryInstance : list) {
            	println(out,"Tracking1");
                println(out,"Started: " + processHistoryInstance.getStartdate() + 
                        "\n Completed steps: " + processHistoryInstance.getCompletedsteps() + 
                        "\n Is completed: "  + (processHistoryInstance.getStatus() == TaskInstanceStatus.COMPLETED));
                if ((processHistoryInstance.getStatus() == TaskInstanceStatus.COMPLETED)) {
                    println(out,"The process has completed all the steps in: " + processHistoryInstance.getElapsedtime()/60000 + " minutes");                    
                } 
                
                out.flush();  
            }

		} catch (Exception e) {
			out.println("Could not connect complete the operation: " + e.getClass().getName() + " " + e.getMessage());
        	out.flush();  
		}    
	}
	
	public void println(PrintWriter out,String msg){
		if (this.htmlOut){
			out.print(msg + "<br>");
		}else{
			out.println(msg);
		}
	}
	
	
	public long getMyProcessId() {
		return myProcessId;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
