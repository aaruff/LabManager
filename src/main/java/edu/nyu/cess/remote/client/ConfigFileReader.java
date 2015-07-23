package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.net.HostConfigurationInfo;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFileReader
{
    private final static Logger log = Logger.getLogger(ConfigFileReader.class);

    /**
     * Parses the config file and returns a HostConfigurationInfo object.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static HostConfigurationInfo readHostConfigFile(String fileName) throws Exception
    {

        String ip, localhost, portStr;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(fileName)) {
            Properties props = new Properties();
            props.load(in);

            ip = props.getProperty("ip");
            portStr = props.getProperty("port");
            localhost = props.getProperty("localhost");
        }
        catch(FileNotFoundException e) {
            log.fatal("Could not find the " + fileName + " config file.");
            throw new Exception();
        } catch (IOException e) {
            log.fatal("Fatal IO exception loading the " + fileName + " config file.");
            throw new Exception();
        }

        if (ip == null) {
            log.fatal("ip address not found in " + fileName + " file.");
            throw new Exception();
        }

        if (localhost == null ) {
            log.fatal("localhost not found in " + fileName + " file.");
            throw new Exception();
        }

        if (portStr == null) {
            log.fatal("port not found in " + fileName + " file.");
            throw new Exception();
        }

        if (! validateIpAddress(ip)) {
            log.fatal("Ill formatted IP address found in " + fileName + " file.");
            throw new Exception();
        }

        int port = Integer.parseInt(portStr);
        if (port < 1024 && port > 49151) {
            log.fatal("Port number in " + fileName + " file is out of range.");
            throw new Exception();
        }

        return new HostConfigurationInfo(ip, port, localhost);

    }

    /**
     * Provides simple IP address format validation.
     * @param ip
     * @return
     */
    private static boolean validateIpAddress(String ip)
    {
        if (ip == null) {
            return false;
        }

        // 4 octets in an IP Address
        String[] octets = ip.split("\\.");
        if (octets.length != 4) {
            return false;
        }

        // check the range of each octet
        for (String octet : octets) {
            if (Integer.parseInt(octet) < 0 && Integer.parseInt(octet) > 223) {
                return false;
            }
        }

        return true;
    }
}
