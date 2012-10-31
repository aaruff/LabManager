CESS Lab Remote (Server)
=================================
The server package used to remotely control (start/stop/monitor)
applications on computers running the [Lab Remote Client](https://github.com/aaruff/AppRemoteClient).

Requirement
------------------
Java 1.6

Installation
------------------
1. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app.
2. Create a folder on the C drive of the server named "LabRemote".
3. Create a file named "application_info.txt". The above file will be used by the server to locate the program to execute on the client computers. 

**The format for the application_info.txt file is as follows:**

`<program name>, <path to the program to execute>, <command line agruments>,`

**For example:**
`zLeaf-2.0.1, zLeaf-2.0.1.exe, /server 192.168.0.1 /language en,
zLeaf-3.1.1, zLeaf-3.1.1.exe, /server 192.168.0.1 /language en,`

Whereby the above configuration will display zLeaf-2.0.1 and zLeaf-3.1.1 in the program selection list for the server remote. If zLeaf-2.0.1 is selected and started on a remote client the server will request that the clients selected execute zLeaf-2.0.1.exe with the flag /server 192.168.0.1 (the server's IP Address) and specifies the language as english.

4. Note 1: The server must have port 2600 open in order for the server to be able to communicate with the clients.  
5. Note 2: A link to the LabRemote server application can be placed on the desktop of the server to make it easier for experimenters to start it.
6. Note 3: The Z-Tree program must be running on the server before z-Leaf clients can connect to it.


License
------------------
[License: Academic Free License version 3.0](http://www.opensource.org/licenses/afl-3.0.php)
