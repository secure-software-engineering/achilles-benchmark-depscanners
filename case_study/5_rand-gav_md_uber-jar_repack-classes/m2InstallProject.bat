@echo on

:: 2019/09/19 17:26:14

mvn install:install-file -Dfile=franken.jar -DgroupId=angry -DartifactId=bender -Dversion=12077541070 -Dpackaging=jar -DgeneratePom=true

:: execute the command in the environment variable:: for instance, export DEPCMD="mvn org.owasp:dependency-check-maven:check"
IF "%DEPCMD%"=="" ECHO command is NOT defined
IF NOT "%DEPCMD%"=="" %DEPCMD%


