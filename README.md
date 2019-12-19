# rest-benchmarks

Comparison between various interesting JVM frameworks & GoLang

Methodology:
Each of the rest endpoint
Accepts a json shown below, sleeps for 25 ms to mimic fairly expensive operations, unmarshals, adds a timestamp, marshals back to string and returns the response.
{"id":"123AEF", "name": "Juby"}

Machine tweaks applied

Client : OSX Mojave
```
ulimit -a
-t: cpu time (seconds)              unlimited
-f: file size (blocks)              unlimited
-d: data seg size (kbytes)          unlimited
-s: stack size (kbytes)             8192
-c: core file size (blocks)         0
-v: address space (kbytes)          unlimited
-l: locked-in-memory size (kbytes)  unlimited
-u: processes                       4096
-n: file descriptors                200000
```
Server : Linux
```
Linux ml-engine 5.0.0-32-generic #34~18.04.2-Ubuntu SMP Thu Oct 10 10:36:02 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux

ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 63794
max locked memory       (kbytes, -l) 16384
max memory size         (kbytes, -m) unlimited
open files                      (-n) 1048576
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited
max user processes              (-u) 63794
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited

```
Java commands & flags used
``` 
java -jar -server -Xms1024m -Xmx8192m -Djava.rmi.server.hostname=192.168.1.141 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false  -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.net.preferIPv4Stack=true app.jar

```

Results: 

Refer to AB_BenchMarks_result.txt
```
Vertx #1 @ 700+ tps  
Akka #2 @ 500+ tps
Springboot #3 @280+ tps
```

