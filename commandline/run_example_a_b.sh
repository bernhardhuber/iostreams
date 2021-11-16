#!/bin/sh

#set -x

JAVA_CMD="$JAVA_HOME/bin/java"
JAR_FILE=target/iostreams-commandline-1.0-SNAPSHOT-main.jar
# generate sample input file
ls -R > inp.txt

# compress input file
${JAVA_CMD} -jar ${JAR_FILE} \
	--modes=compressB64Gzip \
	--from-file=inp.txt \
	> inp_gzip_b64.txt


# decompress compressed input file
${JAVA_CMD} -jar ${JAR_FILE} \
	--modes=decompressB64Gunzip \
	--from-file=inp_gzip_b64.txt \
	> inp_gzip_b64_b64_gunzip.txt

# verify input file and decompressed compressed input file
diff -s inp.txt inp_gzip_b64_b64_gunzip.txt
echo "diff rc $?"
