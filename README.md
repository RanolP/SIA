# SIA
SIA - Server Is Alive? The Minecraft's Server Status Checker.<br>
You can use GUI Connecting, plz run _SIA.class_
# Connect Type
Socket Type Connecting<br>
Minecraft Protocol Type Connecting [_Minecraft's Query Protocol_]<br>
Minecraft Ping Type Connecting [_Minecraft's Ping Protocol_]:<br>
  - 1.5 lower
  - 1.6
  - 1.7 upper

Socket Alive Checking
# Requirements
Gson - PingQuery_1_7

# How to use API?
Use with JAVA API
***
```java
Query query = new ???Query(ip, port);
query.connect(); //It will be return Connect Status.
```
***
You can get default things.<br>
likes motd, max players...<br>
and ProtocolQuery or PingQuery can get many things.
