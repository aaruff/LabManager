package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppInfo;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.app.ClientAppInfoCollection;
import edu.nyu.cess.remote.server.client.ClientPoolExecutionManager;
import edu.nyu.cess.remote.server.io.LabLayoutFile;
import edu.nyu.cess.remote.server.lab.Computer;
import edu.nyu.cess.remote.server.lab.LabLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mockito.Mockito.mock;

/**
 * This class is used to simulate clients connecting and disconnecting to the view.
 */
public class LabFrameTest
{
    private final ArrayList<Computer> connectedComputers = new ArrayList<>();
    private final ArrayList<Computer> availableComputers;
    private final ClientAppInfoCollection appCollection;
    private final ViewController viewController;

    public LabFrameTest() throws IOException
    {
        LabLayout labLayout = getLabLayout();
        if (labLayout == null) {
            throw new IOException("Unable to load lab layout file.");
        }

        availableComputers = labLayout.getAllComputers();

        ClientPoolExecutionManager mockedClientPoolExeManager = mock(ClientPoolExecutionManager.class);

        appCollection = getClientAppInfoCollection();
        viewController = new ViewController(appCollection, mockedClientPoolExeManager, labLayout);
        viewController.display();
    }

    public static void main(String[] args)
    {
        try {
            LabFrameTest viewTest = new LabFrameTest();
            viewTest.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        String command = "";
        while ( ! command.equals("q")) {
            command = displayPromptMenu();

            switch(command) {
                case "a":
                    connectComputer();
                    break;
                case "r":
                    disconnectComputer();
                    break;
                case "l":
                    listConnectedComputers();
                    break;
                case "u":
                    updateClientAppExeStatus();
                    break;
                default:
                    displayCommandNotFound();
            }
        }

        notifyTestComplete();
    }

    private String displayPromptMenu()
    {
        System.out.println("Commands: (a = add, r = remove, q = quit, l = list connected clients, u = update client state)");
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }

    private void displayCommandNotFound()
    {
        System.out.println("Command not found.");
    }


    private ClientAppInfoCollection getClientAppInfoCollection()
    {
        HashMap<String, AppInfo> appInfoMap = new HashMap<>();
        appInfoMap.put("Foo Bar Bin Baz Doo Bin Bow doo bee doo bee doo", new AppInfo("Foo Bar Bin Baz Doo Bin Bow doo bee doo bee doo", "", ""));
        appInfoMap.put("Bar", new AppInfo("Bar", "", ""));
        appInfoMap.put("Bin", new AppInfo("Bin", "", ""));
        appInfoMap.put("Baz", new AppInfo("Baz", "", ""));
        appInfoMap.put("Doo", new AppInfo("Doo", "", ""));
        appInfoMap.put("Doo12", new AppInfo("Doo12", "", ""));
        return new ClientAppInfoCollection(appInfoMap);
    }

    private void disconnectComputer()
    {
        System.out.println("Enter number of random clients to remove from 1 to " + availableComputers.size());
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        for (int i = 1; i <= number; ++i) {
            if(connectedComputers.size() > 0) {
                Random random = new Random();
                int randomComputerId = random.nextInt(connectedComputers.size());
                Computer removedComputer = connectedComputers.remove(randomComputerId);
                availableComputers.add(removedComputer);
                viewController.notifyClientDisconnected(removedComputer.getIp());
                System.out.println("Removed computer " + removedComputer.getName());
            }
            else {
                if (availableComputers.size() == 0) {
                    System.out.println("All available computers have been connected.");
                }
                else {
                    System.out.println("All computers have been disconnected.");
                }
            }
        }

    }

    private void notifyTestComplete()
    {
        System.out.println("Testing complete.");
    }

    private void connectComputer()
    {
        System.out.println("Enter number of random clients to add from 1 to " + availableComputers.size());
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        for (int i = 1; i <= number; ++i) {
            if (availableComputers.size() > 0) {
                Random random = new Random();
                int randomComputerId = random.nextInt(availableComputers.size());
                Computer addedComputer = availableComputers.remove(randomComputerId);
                connectedComputers.add(addedComputer);
                viewController.notifyNewClientConnected(addedComputer.getName(), addedComputer.getIp());
                System.out.println("Added computer " + addedComputer.getName());
            }
            else {
                if (availableComputers.size() == 0) {
                    System.out.println("All available computers have been connected.");
                }
                else {
                    System.out.println("All computers have been disconnected.");
                }
            }
        }
    }

    private void updateClientAppExeStatus()
    {
        listConnectedComputers();
        System.out.println("Enter the ID of the app you would like to execute:");
        for (int k = 0; k < appCollection.getAppNames().length; ++k) {
            System.out.println(k + " => " + appCollection.getAppNames()[k]);
        }

        Scanner scanner = new Scanner(System.in);
        String appIdStr = scanner.next();
        int appId = Integer.valueOf(appIdStr);
        String appName = appCollection.getAppNames()[appId];

        System.out.println("Enter 'start' or 'stop':");
        String exeState = scanner.next();
        AppState appState = (exeState.equals("start")) ? AppState.STARTED : AppState.STOPPED;

        AppInfo appInfo = appCollection.getAppInfo(appName);
        AppExe appExe = new AppExe(appInfo, appState);

        System.out.println("Enter the ID of the computer to update:");
        int id = Integer.valueOf(scanner.next());
        Computer computer = connectedComputers.get(id);

        viewController.notifyClientAppUpdate(appExe, computer.getIp());

        System.out.println("App Updated: " + appExe.toString());
    }

    private LabLayout getLabLayout()
    {
        LabLayout labLayout = null;
        try (InputStream inputStream = getClass().getResourceAsStream("/lab-layout.yaml")) {
            labLayout = LabLayoutFile.readFile(inputStream);
        }
        catch(FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return labLayout;
    }

    private void listConnectedComputers()
    {
        for (int i = 0; i < connectedComputers.size(); ++i) {
            System.out.println("ID=" + i + ", Name=" + connectedComputers.get(i).getName() + ", IP=" + connectedComputers.get(i).getIp());
        }
    }
}
