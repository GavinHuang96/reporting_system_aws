Please use http://34.94.33.47/index.html to visit the application.

To receive emails from async report generation, please use your true email to register.

If you have any questions, please send an email to xhuang60@usc.edu.

# New features
1. Add signon/signin mechanism.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Use spring security and jwt to add authentication and authorization. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/tree/master/ClientService/src/main/java/com/antra/report/client/security))

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Users need to signup and singin ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/endpoint/AuthenticationController.java)) before using the application.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Now the data model is as following.

![alt text](https://github.com/GavinHuang96/reporting_system_aws/blob/master/UML.png)

2. Add update and delete operation.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Add update and delete endpoint to client service ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/endpoint/ReportController.java)), pdf service ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/PDFService/src/main/java/com/antra/evaluation/reporting_system/endpoint/PDFGenerationController.java)) and excel service ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ExcelService/src/main/java/com/antra/evaluation/reporting_system/endpoint/ExcelGenerationController.java)).

3. Use thread pool to call (including generate/update/delete request) PDF and Excel services simultaneously. ([See here](https://github.com/GavinHuang96/reporting_system_aws/blob/121a485616506451957f1596aa92209367ae68d7/ClientService/src/main/java/com/antra/report/client/service/ReportServiceImpl.java#L147))

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Submit the api requests to thread pool at the same time when using the sync api.

4. Using MySQL and MongoDB on the remote database server instead of embedded local database.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The client service uses mysql for user, report request, excel report, pdf report table and mongodb for request data table.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The excel service uses mongodb for excel data. (Previously, it uese hashmap.)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The pdf service uses mongodb for pdf data.

5. Separate the frontend from client service backend. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/tree/master/Frontend))

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Now frontend, client service, excel service, pdf service, MySQL server and MongoDB server are running on different docker containers.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Since time is limited to create a react app, I deploy frontend on Apache server to mock the frontend-backend separation architecture. 

6. Deploy the Excel and PDF microservice on AWS's Elastic Container Service.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Build each service in docker container, store them on ECR and deploy them on ESC.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The requests to the services are distributed by ELB and the log is sent to CloudWatch.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The autoscale policy is based on CPU usage.

6. Save generated excel and pdf files on AWS's S3. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/99c42e0252116f198053498871984c5c3193a005/ExcelService/src/main/java/com/antra/evaluation/reporting_system/service/ExcelServiceImpl.java#L36))

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Previously, generated excel files are stored on local. It doesn't make much sense because the server has limited space.

7. Using google's OAuth to authorize Gmail API in lambda function. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/569a22afc445ea40eb835574dba475d1c061f236/Lambda/lambda_function.py#L11))

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Logining gmail using password in script is disabled by google. OAuth token is more othen used in api.


# Othere improvement
1. Send emails to the users' registered email instead of the hardcoded email. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/650af87ad3471ce66a8a1a8c69697eac257bdb3b/ClientService/src/main/java/com/antra/report/client/endpoint/ReportSQSListener.java#L37))

2. Only send email to the users when they use async API. Sync API gets results immediately so it doesn't make sense to send an email.

3. Remove the submitter information from both frontend and backend because now each user has their own workspace by registration.

4. Save users' raw JSON input in MongoDB ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/entity/DataEntity.java)) and retrieve it when users want to update the old report so that they don't need to enter it again. (Test it by clicking the update link in the frontend.)

5. Change the bidirectional one-to-one relationship (report request - excel report, report request - pdf report) to unidirectional. Because we never use the relationship from the excel report or pdf report side.

6. Add a general service to make all kinds of HTTP requests (We use Get, Post, Put, Delete in the application) in one function. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/service/RestCallServiceImpl.java))

7. Extend the report status to GENERATE_PENDING, GENERATED, GENERATE_FAILED, UPDATE_PENDING, UPDATED, UPDATE_FAILED, DELETE_PENDING, DELETED, DELETE_FAILED ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/pojo/type/RequestStatus.java)) and add status for report request ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/ClientService/src/main/java/com/antra/report/client/pojo/type/RequestStatus.java)), which can represent our data model better.

8. Rewrite the frontend javascript code and update HTML to bootstrap5. ([See Here](https://github.com/GavinHuang96/reporting_system_aws/blob/master/Frontend/app.js)) So our frontend doesn't rely on jQuery anymore because jQuery is believed to be an outdated technology.

9. Convert the created and updated time on the frontend to the user's local time.

10. Unify the excel and pdf service interface. Since there are similar, now they have the same interface.

11. Reorganize the project structure to make it more clear.

12. Remove unused code from the project to make it more concise.







