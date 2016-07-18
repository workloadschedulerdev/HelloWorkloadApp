Hello Workload Application

This sample application demonstrates how to write a Hello World application leveraging the Workload Scheduler service and deploy it on Bluemix.


Files

The Hello Workload application contains the following contents:

*   helloWorkloadApp.war

    This WAR file is actually the application itself. It is the only file that is pushed to and run on the Bluemix cloud. Every time your application code is updated, you need to regenerate this WAR file and push it to Bluemix again. See the next section on detailed steps.
    
*   WebContent/

    This directory contains the client side code (HTML/CSS/JavaScript) of your application as well as the compiled server side java classes and necessary JAR libraries. The content inside this directory is all you need to generate the final WAR file.
    
*   src/

    This directory contains the server side code (JAVA) of your application. 
     
*   build.xml

    This file allows you to easily build your application using Ant.
    
*   instructions.md

    This file describes the Next Steps for getting started with this template.