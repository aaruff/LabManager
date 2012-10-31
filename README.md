CESS Lab Remote (Server)
=================================
The server package used to remotely control (start/stop/monitor)
applications on computers running the [Lab Remote Client](https://github.com/aaruff/AppRemoteClient).

Requirement
------------------
Java 1.6

Windows Installation
------------------
1. Create the "LabRemote" folder on the C drive of the server (C:\LabRemote).
1. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app and unzip it in the C:\LabRemote directory.
3. Add the program names, path, and arguments to the "application_info.txt" configuration file. 
The above file will be used by the server to locate the program to execute on the client computers. 

**The format for the application_info.txt file is as follows:**
`<program name>, <path to the program to execute>, <command line arguments>,`

**For Example:**
`zLeaf-2.0.1, C:\zleaf\zLeaf-2.0.1.exe, /server 192.168.0.1 /language en,
zLeaf-3.1.1, C:\zleaf\zLeaf-3.1.1.exe, /server 192.168.0.1 /language en,`

The above configuration file specifies the location and arguments for two zLeaf programs.
The first portion (ZLeaf-2.0.1) contains the name to be displayed for selection in the server program.
The second portion (C:\zleaf\zLeaf-2.0.1.exe) specifies the path to the program to be executed on the client.
The third portion (/server 192.168.0.1 /langauge en) specifies an addition information that must be passed to the program upon execution.

4. Note 1: The server must have port 2600 open in order for the server to be able to communicate with the clients.  
5. Note 2: A link to the LabRemote server application can be placed on the desktop of the server to make it easier for experimenters to start it.
6. Note 3: The Z-Tree program must be running on the server before z-Leaf clients can connect to it.


License
------------------
[License: Academic Free License version 3.0](http://www.opensource.org/licenses/afl-3.0.php)
