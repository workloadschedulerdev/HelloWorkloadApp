Get started with Hello Workload
-----------------------------------
Welcome to the Hello Workload application!

This sample application demonstrates how to write a Hello World application leveraging the Workload Scheduler service and deploy it on Bluemix.
Before deploying it to Bluemix, in the HelloWorkload class you have to set your basicAuth and the hostname to connect on. This sample use our
new REST api, so you'll need to download the java-client avaible on Bluemix in order to use them.

Here the instructions if you use the command line cf instead of ACE
1. Install the cf command-line tool(https://www.ng.bluemix.net/docs/#cli/index.html#cli).
2. Extract the package and `cd` to it.
3. Connect to Bluemix:

		cf api https://api.ng.bluemix.net

4. Log into Bluemix:

		cf login -u <your_username>
		cf target -o <your_username> -s dev
				
5. Compile the JAVA code and generate the war package using ant.
6. Deploy your app:

		cf push <your_app_name> -p helloWorkloadApp.war --no-start
		cf create-service WorkloadService Standard <your_service_name>
		cf bind-service <your_app_name> <your_service_name>
		cf start 

7. Access your app <your_app_name>
