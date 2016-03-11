# LabManager
LabManager is a lab management tool that provides remote execution, and monitoring, of applications on remote computers from a central location.

## Setup
Java Version: 1.8

### Client

#### Windows
1. Create the __LabRemote__ folder on the C drive of each client computer (__C:\LabRemote__).
2. Download the [Client](https://github.com/downloads/aaruff/AppRemoteClient/Remote-Client.zip) app and unzip it in the __C:\LabRemote__ directory.
3. Edit the "server_location.txt" in the __C:\LabRemote__ directory and enter the Servers IP address separated with a comma followed by 2600 (port).

	**The format for the server_location.txt file is as follows:**

		IP Address , Port Number

	**For Example:**

		192.168.0.1 , 2600

	The above configuration file specifies the server's IP address (192.168.0.1), and the port with which it uses to communicates with the server (port 2600).

4. Place a shortcut to C:\LabRemote\Remote_Client.jar into the Windows Startup folder.
	* Instructions on how to access the windows startup folder can be found [here](http://windows.microsoft.com/en-US/windows-vista/Run-a-program-automatically-when-Windows-starts)

__Note__: If a firewall is enabled the port 2600 must be made available inorder for the client to communicate with the server.

#### Linux and OS X
1. Create the __LabRemote__ directory in the users home directory.
	* On OS X: /Users/<user>/LabRemote
	* On Linux /home/<usr>/LabRemote
2. Download the [Client](https://github.com/downloads/aaruff/AppRemoteClient/Remote-Client.zip) app and unzip it into the newly created directory.
3. Add the Lab Remote Server's IP Address and Port Number to the __server_location.txt__ file  in the __LabRemote__ directory.

	**The format for the server_location.txt file is as follows:**

		IP Address , Port Number

	**For Example:**

		192.168.0.1 , 2600

	The above configuration file specifies 192.168.0.1 as the servers IP address separated by acomma, followed by the servers port number 2600.

__Note__: If a firewall is enabled the port 2600 must be made available inorder for the client to communicate with the server.


### Server

#### Windows
1. Create the __LabRemote__ folder on the C drive of the server (__C:\LabRemote__).
2. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app and unzip it in the __C:\LabRemote__ directory.
3. Add the program names, path, and arguments to the __application_info.txt__ configuration file.
	The above file will be used by the server to locate the program to execute on the client computers.

	**The format for the application_info.txt file is as follows:**

		program name , path to the program to execute , command line arguments ,
	_Note:_ Each section is separated by a comma.

	**Example:**

		zLeaf-2.0.1, C:\zleaf\zLeaf-2.0.1.exe, /server 192.168.0.1 /language en,
		zLeaf-3.1.1, C:\zleaf\zLeaf-3.1.1.exe, /server 192.168.0.1 /language en,

	The above configuration file specifies the location and arguments for two zLeaf programs.
	* The first section (ZLeaf-2.0.1) contains the name to be displayed for selection in the server program.
	* The second section (C:\zleaf\zLeaf-2.0.1.exe) specifies the path to the program to be executed on the client.
	* The third section (/server 192.168.0.1 /langauge en) specifies an addition information that must be passed to the program upon execution.

4. Create a shortcut to the __C:\LabRemote\Remote-Server.jar__ on to the desktop for easy execution of the application remote server.

__Note 1:__ The server must have port 2600 open in order for the server to be able to communicate with the clients.
__Note 2:__ The Z-Tree program must be running on the server before z-Leaf clients can connect to it.


#### Linux and OS X
1. Create the __LabRemote__ directory in the users home directory.
	* On OS X: /Users/<user>/LabRemote
	* On Linux /home/<user>/LabRemote
2. Download the [Server](https://github.com/downloads/aaruff/AppRemoteServer/Remote-Server.zip) app and unzip into the newly created LabRemote Directory.
3. Add the program names, path, and arguments to the __application_info.txt__ configuration file.
The above file will be used by the server to locate the program to execute on the client computers.

	**The format for the application_info.txt file is as follows:**

		program name , path to the program to execute , command line arguments ,
	_Note:_ Each section is separated by a comma.

	**Example:**

		zLeaf-2.0.1, /usr/bin/wine, /home/subject/z-leafs/zLeaf-2.0.1.exe /server 192.168.0.1 /language en,

	The above configuration file specifies the location and arguments for one zLeaf program,
	which is executed on a linux client using [wine](http://www.winehq.org/).
	* The first section (ZLeaf-2.0.1) contains the name to be displayed for selection in the server program.
	* The second section (/usr/bin/wine) is the path to the Wine program that will be used to run the z-leaf program.
	* The third section (/home/subject/z-leafs/zLeaf-2.0.1.exe /server 192.168.0.1 /language en) contains the path the the z-Tree program wine will execute and it's arguments.
