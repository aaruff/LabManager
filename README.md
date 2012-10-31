CESS Lab Remote (Server)
=================================
The server package used to remotely control (start/stop/monitor)
applications on computers running the [Lab Remote Client](https://github.com/aaruff/AppRemoteClient).

Requirement
------------------
Java 1.6

Installation: Windows
------------------
1. Create the "LabRemote" folder on the C drive of the server (__C:\LabRemote__).
2. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app and unzip it in the __C:\LabRemote__ directory.
3. Add the program names, path, and arguments to the "application_info.txt" configuration file. 
The above file will be used by the server to locate the program to execute on the client computers.  

**The format for the application_info.txt file is as follows:**  
	<program name>, <path to the program to execute>, <command line arguments>,

**For Example:**  
	zLeaf-2.0.1, C:\zleaf\zLeaf-2.0.1.exe, /server 192.168.0.1 /language en,
	zLeaf-3.1.1, C:\zleaf\zLeaf-3.1.1.exe, /server 192.168.0.1 /language en,

The above configuration file specifies the location and arguments for two zLeaf programs.  
* The first section (ZLeaf-2.0.1) contains the name to be displayed for selection in the server program.
* The second section (C:\zleaf\zLeaf-2.0.1.exe) specifies the path to the program to be executed on the client.
* The third section (/server 192.168.0.1 /langauge en) specifies an addition information that must be passed to the program upon execution.  

4. Create a shortcut to the __C:\LabRemote\Remote-Server.jar__ on to the desktop for easy execution of the application remote server.  

__Note 1:__ The server must have port 2600 open in order for the server to be able to communicate with the clients.  
__Note 2:__ The Z-Tree program must be running on the server before z-Leaf clients can connect to it.


Installation: Linux and OS X
----------------------------
1. Create the "LabRemote" directory in the users home directory.
	* On OS X: /Users/<user>/LabRemote
	* On Linux /home/<user>/LabRemote
2. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app and unzip into the newly created LabRemote Directory.
3. Add the program names, path, and arguments to the "application_info.txt" configuration file. 
The above file will be used by the server to locate the program to execute on the client computers.

**The format for the application_info.txt file is as follows:**  
	<program name>, <path to the program to execute>, <command line arguments>,

**For Example:**  
	zLeaf-2.0.1, /usr/bin/wine, /home/subject/z-leafs/zLeaf-2.0.1.exe /server 192.168.0.1 /language en,

The above configuration file specifies the location and arguments for one zLeaf program, 
which is executed on a linux client using [wine](http://www.winehq.org/).  

* The first section (ZLeaf-2.0.1) contains the name to be displayed for selection in the server program.
* The second section (/usr/bin/wine) is the path to the Wine program that will be used to run the z-leaf program.
* The third section (/home/subject/z-leafs/zLeaf-2.0.1.exe /server 192.168.0.1 /language en) contains the path the the z-Tree program wine will execute and it's arguments.



License
------------------
[License: Academic Free License version 3.0](http://www.opensource.org/licenses/afl-3.0.php)
