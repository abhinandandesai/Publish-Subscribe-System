package edu.rit.cs;
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.RemoteException;
import java.lang.*;
import java.rmi.registry.LocateRegistry;

/**
 * The function helps setup the Server of the system.
 * Hostname and port are set and it does no take command line arguments.
 */
public class Server {



    private int port = 1099;
    private String hostName = "rmiserver";
    private EventManager manager = null;

    /**
     * Constructor that helps set up the server.
     */
    public Server() {
        try {
            if (hostName.length() == 0)
                hostName = "rmiserver";
            manager = new EventManager();
            System.out.println("Event Manager object created.");
            Naming.rebind("//rmiserver:1099/EventManager", manager);
            System.out.println("Rebind complete.");
            System.out.println("EventManager bound in registry at " + hostName + ":" + port);

            // This notifies the pending events and pending topics to reconnecting agents
            manager.startService();
            manager.startServiceAds();
        } catch (Exception e) {
            System.out.println( "EventManager error");
            System.exit(1);
        }
    }


    /**
     * Function to setup the RMI registry on a port and then provide a server console.
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(new SecurityManager());
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            LocateRegistry.getRegistry();
            System.err.println("java RMI registry already exists.");
        }

        Server server = new Server();
        server.manager.serverConsole();
    }
}
