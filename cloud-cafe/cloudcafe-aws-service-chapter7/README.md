1)Upload the application to Cloud 9


This projects remove additional workload from application.

Now this project just has the functionality to upload image and get detials from DB

 > Upon image upload a notification sent to SNS topic

 > A Lambda function is listening to the event process labels and upload information to database.


build using mvn:spring-boot:run (considering you have done java & maven set up already)


