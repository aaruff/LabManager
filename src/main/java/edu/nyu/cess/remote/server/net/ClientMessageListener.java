package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.server.message.MessageHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ClientMessageListener implements Runnable
{
    final Logger logger = Logger.getLogger(ClientMessageListener.class);
    private ObjectInputStream objectInputStream;

    private MessageHandler messageHandler;
    private Socket socket;

    public ClientMessageListener(Socket socket, MessageHandler messageHandler)
    {
        this.socket = socket;
        this.messageHandler = messageHandler;
    }

    public void run()
	{
        Message message;

        String clientIpAddress = socket.getInetAddress().getHostAddress();

        logger.info("Waiting for message from Client " + clientIpAddress);
        while ((message = readDataPacket()) != null) {
            logger.info("Data Packet Received");
			messageHandler.handleInboundMessage(message);
        }

        logger.info("Client " + clientIpAddress + " connection closed...");
    }

    private Message readDataPacket()
    {
        Message message = null;
        boolean streamInitialized = true;

        if (socket.isConnected()) {

            if (objectInputStream == null) {
                streamInitialized = initializeObjectInputStream();
            }

            if (objectInputStream != null && streamInitialized) {
                try {
                    Object object = objectInputStream.readObject();
                    message = (Message) object;
                } catch (ClassNotFoundException e) {
                    logger.error("The Serialized Object Not Found", e);
                    message = null;
                } catch (IOException e) {
                    logger.error(e);
                    message = null;
                }
            }
        }

        return message;
    }

    private boolean initializeObjectInputStream()
    {
        boolean result = false;

        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    result = true;
                } catch (IOException ex) {
                    objectInputStream = null;
                    result = false;
                }
            }
        }
        return result;
    }
}
