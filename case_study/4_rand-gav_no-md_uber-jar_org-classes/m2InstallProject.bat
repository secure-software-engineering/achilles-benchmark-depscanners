@echo on

:: 2019/09/19 17:21:30

mvn install:install-file -Dfile=franken.jar -DgroupId=bold -DartifactId=dave.mcfly -Dversion=18865497620 -Dpackaging=jar -DgeneratePom=true

:: execute the command in the environment variable:: for instance, export DEPCMD="mvn org.owasp:dependency-check-maven:check"
IF "%DEPCMD%"=="" ECHO command is NOT defined
IF NOT "%DEPCMD%"=="" %DEPCMD%


