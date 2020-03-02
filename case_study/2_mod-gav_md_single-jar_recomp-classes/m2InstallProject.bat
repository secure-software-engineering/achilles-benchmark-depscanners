@echo on

:: 2019/09/19 17:15:41

mvn install:install-file -Dfile=spring-web-5.0.5.RELEASE.jar -DgroupId=org.springframework -DartifactId=spring-web -Dversion=5.0.5.RELEASE_fix2082195026 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=spring-webmvc-5.0.0.RELEASE.jar -DgroupId=org.springframework -DartifactId=spring-webmvc -Dversion=5.0.0.RELEASE_patch1970762771 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=spring-expression-5.0.4.RELEASE.jar -DgroupId=org.springframework -DartifactId=spring-expression -Dversion=5.0.4.RELEASE_patch1284677370 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=spring-core-5.0.5.RELEASE.jar -DgroupId=org.springframework -DartifactId=spring-core -Dversion=5.0.5.RELEASE_fix1918455882 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=guava-23.0.jar -DgroupId=com.google.guava -DartifactId=guava -Dversion=23.0_update302428243 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=httpclient-4.1.3.jar -DgroupId=org.apache.httpcomponents -DartifactId=httpclient -Dversion=4.1.3_patch1101712826 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=jackson-databind-2.9.7.jar -DgroupId=com.fasterxml.jackson.core -DartifactId=jackson-databind -Dversion=2.9.7_fix128997094 -Dpackaging=jar -DgeneratePom=true

:: execute the command in the environment variable:: for instance, export DEPCMD="mvn org.owasp:dependency-check-maven:check"
IF "%DEPCMD%"=="" ECHO command is NOT defined
IF NOT "%DEPCMD%"=="" %DEPCMD%


