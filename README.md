## location-service

This will used to get location for the rider based on lat-long,address based on lat-long and distance between two points.
-------------------
### What you’ll need

A favorite text editor or IDE\
JDK 8 or later\
Install Gradle\
Install postgress\
Install Redis

-------------

### Build
1. run cmd `gradle clean build` or `gradlew clean build`.
2. This command will run unit test cases also.
3. Please connect postgress to the project.
4. Also, please create "GOOGLEMAPS_API_KEY" file in secret path or please comment the getApiKey() method inside GoogleMapsConfig.java file
-----
### Run
1. update mongoDb uri to localhost mongo address in application-local.yml file.
2. Kafka is also required, therefore please run the kafka server,bootstrap etc on local.
3. add  "-Dspring.profiles.active=local" in cmd as arguments 
4. run cmd ` gradle -Dspring.profiles.active=local clean :bootRun` or `gradlew -Dspring.profiles.active=local clean :bootRun`.

-----
  BUILD SUCCESSFUL
  Total time: 4.009 secs
  
  Project ran successfully.
