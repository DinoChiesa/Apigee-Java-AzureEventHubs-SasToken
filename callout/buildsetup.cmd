::
:: Created: <Tue Oct  6 11:46:13 2015>
:: Last Updated: <2023-March-13 15:59:30>
::

@echo.
@echo This script downloads JAR files and installs them into the local Maven repo.
@echo.

curl -O https://raw.githubusercontent.com/apigee/api-platform-samples/master/doc-samples/java-cookbook/lib/expressions-1.0.0.jar

:: mvn itself is a cmd script, so we need to use CALL
CALL mvn install:install-file  -Dfile="expressions-1.0.0.jar"  -DgroupId="com.apigee.edge"  -DartifactId="expressions"  -Dversion="1.0.0"  -Dpackaging="jar"  -DgeneratePom="true"

del expressions-1.0.0.jar

curl -O https://raw.githubusercontent.com/apigee/api-platform-samples/master/doc-samples/java-cookbook/lib/message-flow-1.0.0.jar

CALL mvn install:install-file   -Dfile="message-flow-1.0.0.jar"   -DgroupId="com.apigee.edge"   -DartifactId="message-flow"   -Dversion="1.0.0"   -Dpackaging="jar"   -DgeneratePom="true"

del message-flow-1.0.0.jar

@echo.
@echo done.
@echo.
