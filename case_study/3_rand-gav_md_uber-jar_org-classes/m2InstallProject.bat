@echo on

:: 2019/09/19 17:20:14

mvn install:install-file -Dfile=franken.jar -DgroupId=admiring -DartifactId=marge.simpson -Dversion=2706191000 -Dpackaging=jar -DgeneratePom=true

:: execute the command in the environment variable:: for instance, export DEPCMD="mvn org.owasp:dependency-check-maven:check"
IF "%DEPCMD%"=="" ECHO command is NOT defined
IF NOT "%DEPCMD%"=="" %DEPCMD%


