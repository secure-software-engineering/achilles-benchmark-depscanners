#!/bin/bash
export DEPCMD="mvn org.owasp:dependency-check-maven:check -Dformat=csv"
echo "DEPCM is"
echo $DEPCMD

echo "Start running command in "
for f in *; do
    if [ -d "$f" ]; then
        # $f is a directory
        echo "$f"
        cd "$f"
        echo "$pwd"
	source m2InstallProject.sh
        cd ..
    fi
done
