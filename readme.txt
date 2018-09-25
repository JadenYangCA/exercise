1. The Spring Boot app path is : /application/exercise-0.0.1-SNAPSHOT.jar

2. application.properties must be in the same directory with exercise-0.0.1-SNAPSHOT.jar.
   king-i.txt path can be set in /application/application.properties.

3. how to start the microservice application:
    java -jar exercise-0.0.1-SNAPSHOT.jar

4. when application is running, the API documentation can be found at:
    http://localhost:8081/exercise/swagger-ui.html#!/searching-controller/findLocationsInTextUsingGET
    API is : http://localhost:8081/exercise/text/{query_text}

5. Deploying a Spring Boot Application on AWS Using AWS Elastic Beanstalk:
    https://aws.amazon.com/blogs/devops/deploying-a-spring-boot-application-on-aws-using-aws-elastic-beanstalk/
