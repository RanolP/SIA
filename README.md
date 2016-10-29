# SIA
SIA - Server Is Alive? The Minecraft's Server Status Checker.
# Connect Type
Socket Type Connecting<br>
Minecraft Protocol Type Connecting [_Minecraft's List Ping Packet_]
# How to use?
Create Query Class with ip & port.
***
    Query query = new ???Query(ip, port);
    query.connect(); //It will be return Connect Status.
***
You can get default things.
likes motd, max players...
and ProtocolQuery can get many things.
