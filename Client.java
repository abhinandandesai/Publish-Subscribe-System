package edu.rit.cs;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * This Class takes in command line input and acts the client to connect to the server.
 * The port and hostname are set but the state of the agent can be decided accordingly.
 *      any character : new agent
 *      [-load] : to load an existing saved state of the agent.
 */
public class Client {

    private String hostName = "rmiserver";
    private int port = 1099;
    private PubSubAgent agent = null;

    /**
     * Processing the command line and creating agent accordingly.
     * @param args
     */
    public Client(String[] args){
        if (args[0].equals("-load")) {
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream("agent.dat"));
                agent = (PubSubAgent) is.readObject();
                agent.reconnectServer();
                is.close();
            } catch (Exception e) {
                System.out.println("Object not loaded correctly.");
                System.out.println("New agent will be created.");
            }
        }
        try {
            if (hostName.length() == 0)
                hostName = "rmiserver";
            if (agent == null) {
                EventInterface server = (EventInterface) Naming.lookup("rmi://rmiserver:1099/EventManager");
                System.out.println("Connection to Server successful" + "\nHostName: " + hostName + "\nPort: " + port );
                agent = new PubSubAgent(server);
            }
        } catch (Exception e) {
            System.out.println("Cannot connect to Server. Try again");
            System.exit(1);
        }
    }


    /**
     * Main function that creates an instance and the provides the agent console.
     *
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(new SecurityManager());

        Client client = new Client(args);

        try {
            client.agent.console();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
