#!/bin/sh

#set -x

#
JAVA_CMD="$JAVA_HOME/bin/java"
JAR_FILE=target/iostreams-commandline-0.2.0-SNAPSHOT-main.jar

# generate sample input file
echo "Generate 'inp.txt'"
ls -R > inp.txt

# compress input file
echo "Compress 'inp.txt' to 'inp_gzip_b64.txt'"
${JAVA_CMD} -jar ${JAR_FILE} \
	--modes=COMPRESSB64GZIP \
	--from-file=inp.txt \
	> inp_gzip_b64.txt


# decompress compressed input file
echo "Decompress to 'inp_gzip_b64_b64_gunzip.txt'"
${JAVA_CMD} -jar ${JAR_FILE} \
	--modes=DECOMPRESSB64GUNZIP \
	--from-file=inp_gzip_b64.txt \
	> inp_gzip_b64_b64_gunzip.txt

# verify input file and decompressed compressed input file
echo "Compare files"
diff -s inp.txt inp_gzip_b64_b64_gunzip.txt
echo "diff rc $?"
