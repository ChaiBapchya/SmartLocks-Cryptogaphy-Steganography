# SmartLocks-Cryptogaphy-Steganography

# System Design
[](https://github.com/ChaiBapchya/SmartLocks-Cryptogaphy-Steganography/blob/master/Pictures/System%20Design.png)

# Circuit Design
[](https://github.com/ChaiBapchya/SmartLocks-Cryptogaphy-Steganography/blob/master/Pictures/Circuit%20diagram.png)


# Raspberry Pi 
## Bluetooth Module
```
pi@raspberrypi:~ $ ps auxw | grep obex-da
pi        1040  0.0  0.3  17316  3672 ?        S    10:43   0:00 /usr/bin/obex-data-server --no-daemon 
pi        1143  0.0  0.2   4280  1912 pts/0    S+   10:55   0:00 grep --color=auto obex-da
pi@raspberrypi:~ $ kill -9 1040
pi@raspberrypi:~ $ ps auxw | grep obex-da
pi        1145  0.0  0.2   4276  1948 pts/0    S+   10:56   0:00 grep --color=auto obex-da
pi@raspberrypi:~ $ sudo obexpushd -B -n
obexpushd 0.11.2 Copyright (C) 2006-2010 Hendrik Sattler
This software comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it
under certain conditions.
Listening on bluetooth/[00:00:00:00:00:00]:9
Creating file "abc3.png"
```
