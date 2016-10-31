# SIA
SIA - Server Is Alive? The Minecraft's Server Status Checker.
You can use GUI Connecting, plz run _SIA.class_
# Connect Type
Socket Type Connecting
Minecraft Protocol Type Connecting [_Minecraft's Query Protocol_]
Minecraft Ping Type Connecting [_Minecraft's Ping Protocol_]:
  - 1.5 lower
  - 1.6
  - 1.7 upper

Socket Alive Checking

# How to use API?
Use with JAVA API
```java
Query query = new ???Query(ip, port);
query.connect(); //It will be return Connect Status.
```
You can get default things.
likes motd, max players...
and ProtocolQuery or PingQuery can get many things.
