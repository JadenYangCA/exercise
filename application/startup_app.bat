REM please update the app home before execute it
set "EXERCISE_APP_HOME=D:/application"

set server.port=8081
set "server.context-path=/exercise"
set server.tomcat.uri-encoding=UTF-8
set "appconfig.filePath=%EXERCISE_APP_HOME%/king-i.txt"
set "logging.path=%EXERCISE_APP_HOME%/log"

java -jar "%EXERCISE_APP_HOME%/exercise-0.0.1-SNAPSHOT.jar"