Lab Manager
=================================

Client
=================================
The client application starts, stops, and monitors applications
specified by the remote server.

Requirement
--------------
Java 1.8

Installation: Windows
------------------
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

Installation: Linux and OS X
------------------
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

