# iostreams-commandline

Commandline for compressing, and decompressing from a file, or stdin.

## Usage

```
Usage: Main [-hV] (--from-file=FROM_FILE | --stdin) (--compress=COMPRESS[,
            COMPRESS...] [--compress=COMPRESS[,COMPRESS...]]... |
            --decompress=DECOMPRESS[,DECOMPRESS...] [--decompress=DECOMPRESS[,
            DECOMPRESS...]]... | --modes=MODES) [@<filename>...]
Run convert input using -
base64-encoder, base64-decoder, mime-encoder, mime-decoder, gzip, gunzip,
deflate, or inflate

      [@<filename>...]   One or more argument files containing options.
      --compress=COMPRESS[,COMPRESS...]
                         Valid values: "DEFLATE, GZIP, B64ENC, MIMEENC"
      --decompress=DECOMPRESS[,DECOMPRESS...]
                         Valid values: "INFLATE, GUNZIP, B64DEC, MIMEDEC"
      --from-file=FROM_FILE
                         Read from a file
  -h, --help             Show this help message and exit.
      --modes=MODES      Valid values: "compressB64, compressMime,
                           compressGzip, compressDeflate, compressB64Gzip,
                           compressMimeGzip, compressB64Deflate,
                           compressMimeDeflate, decompressB64, decompressMime,
                           decompressGunzip, decompressInflate,
                           decompressB64Gunzip, decompressMimeGunzip,
                           decompressB64Inflate, decompressMimeInflate"
      --stdin            Read from stdin
  -V, --version          Print version information and exit.

```
## COMPRESS

Supported COMPRESS values of option --compress:

| Operation  | COMPRESS | Description                       |
|------------|--------- |-----------------------------------|
| compress   | B64ENC   | encode input using b64 encoding   |
| compress   | MIMEENC  | encode input using mime encoding  |
| compress   | GZIP     | compress input using gzip         |
| compress   | DEFLATE  | compress input using deflate      |

## DECOMPRESS

Supported DECOMPRESS values of option --decompress:

| Operation  | DECOMPRESS | Description                       |
|------------|------------|-----------------------------------|
| decompress | B64DEC     | decode input using b64 decoding   |
| decompress | MIMEDEC    | decode input using mime decoding  |
| decompress | GUNZIP     | decode input using gunzip         |
| decompress | INFLATE    | decode input using inflate        |

## MODES

Supported MODES values of option --modes:

| Operation  | MODE                  | Description                       |
|------------|-----------------------|-----------------------------------|
| compress   | compressB65           | encode input using b64 encoding   |
| compress   | compressMime          | encode input using mime encoding  |
| compress   | compressGzip          | compress input using gzip         |
| compress   | compressDeflate       | compress input using deflate      |
| compress   | compressB64Gzip       | compress input using gzip+b64     |
| compress   | compressMimeGzip      | compress input using gzip+mime    |
| compress   | compressB64Deflate    | compress input using deflate+b64  |     
| compress   | compressMimeDeflate   | compress input using deflate+mime |
| decompress | decompressB64         | decode input using b64 decoding   |
| decompress | decompressMime        | decode input using mime decoding  |
| decompress | decompressGunzip      | decode input using gunzip         |
| decompress | decompressInflate     | decode input using inflate        |
| decompress | decompressB64Gunzip   | decode input using b64+gunzip     |
| decompress | decompressMimeGunzip  | decode input using mime+gunzip    |
| decompress | decompressB64Inflate  | decode input using b64+inflate    |
| decompress | decompressMimeInflate | decode input using mime+inflate   |

## Example A

Compress input file inp.txt using gzip + b64 
to inp_gzip_b64.txt

```
java -jar target/iostreams-commandline-1.0-SNAPSHOT-main.jar \
  --modes=compressB64Gzip \
  --from-file=inp.txt \
  > inp_gzip_b64.txt
```

## Example B

Decompress input file inp_gzip_b64.txt using b64 + gunzip 
to inp_gzip_b64_b65_gunzip.txt

```
java -jar target/iostreams-commandline-1.0-SNAPSHOT-main.jar \
  --modes=decompressB64Gunzip \
  --from-file=inp_gzip_b64.txt \
  > inp_gzip_b64_b64_gunzip.txt
```
