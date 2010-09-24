#/bin/bash -e

mvn release:clean
mvn release:prepare

mvn package

ARTIFACT=`ls target/metrics.hfind-*.tar.gz`

echo
echo "$ARTIFACT built, ready to be uploaded:"
tar ztvf $ARTIFACT
echo
