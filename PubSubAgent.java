package edu.rit.cs;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class acts as Publisher or Subscriber. It works according to the selected option.
 * This class will have multiple instance each acting as different Agents.
 */
public class PubSubAgent extends UnicastRemoteObject implements Publisher, Subscriber, Serializable {
    private static final long serialVersionUID = 1L;
    public EventInterface server;
    //Unique identifier assigned by the server
    private int agentID;
    public ArrayList<Topic> receivedTopics;


    //Used by the publisher
    public ArrayList<Topic> pubTopics;
    public ArrayList<Event> pubEvents;


    //Used by the subscriber
    public ArrayList<Topic> subscriberTopics;
    public ArrayList<String> subscriberKeywords;
    public ArrayList<Event> receivedEvents;


    /**
     *Constructor for the class.
     *
     * @param server
     * @throws RemoteException
     */
    public PubSubAgent(EventInterface server) throws RemoteException {
        this.server = server;
        if (server != null)
            this.agentID = server.connect(this);
        subscriberTopics = new ArrayList<>();
        subscriberKeywords = new ArrayList<>();
        receivedEvents = new ArrayList<>();
        receivedTopics = new ArrayList<>();
        pubTopics = new ArrayList<>();
        pubEvents = new ArrayList<>();
    }

    /**
     * This function sets up the server.
     *
     * @param server
     * @throws RemoteException
     */
    public void setupServer(EventInterface server) throws RemoteException {
        this.server = server;
        this.agentID = server.connect(this);
    }


    /**
     * This agent helps a returning agent to connect again with the server.
     * @throws RemoteException
     */
    public void reconnectServer() throws RemoteException {
        server.reConnect(this.agentID, this);
    }


    /**
     * Returns the server object
     * @return server
     */
    public EventInterface getServer() {
        return this.server;
    }


    /**
     * It lists out all the subscribed topics of any agent.
     */
    public void listSubscribedTopics() {
        for (Topic topic : subscriberTopics){
            System.out.print(topic);
        }
    }




    /**
     * It lists out the events that have been received by the agent.
     */
    public void listReceivedEvents() {
        for (Event event : receivedEvents) {
            System.out.print(event);
        }
    }


    /**
     * This function helps notify an event that was published
     * @param event Object that contains details of an event
     * @throws RemoteException
     */
    public void notify(Event event) throws RemoteException {
        System.out.println("Event Notification Received");
        System.out.println("These are the Details: ");
        System.out.println(event);
        receivedEvents.add(event);
    }


    /**
     * This function creates a thread to help the agent subscribe to a topic
     * @param topic object that carries information about the topic
     */
    public void subscribe(final Topic topic) {
        new Thread(new Runnable() {
            public void run() {
                int attempts = 0;
                while(++attempts < 20){
                    try {
                        if(server.addSubscriber((PubSubAgent.this.agentID), topic)){
                            subscriberTopics.add(topic);
                            System.out.print("Subscribed.");
                            return;
                        }
                    } catch (RemoteException e) {
                        System.err.println("Could not connect to server. Retrying...");
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.err.println("Couldn't subscribe to " + topic.getTopicID() + " - " + topic.getTopicName());
            }
        }).start();
    }


    /**
     * This function helps an agent to unsubscribe from a topic
     * @param topic object that contains the details of a topic
     */
    public void unsubscribe(final Topic topic) {

        new Thread(new Runnable() {
            public void run() {
                int attempts = 0;
                while(++attempts < 20) {
                    try {
                        if (server.removeSubscriber(PubSubAgent.this.agentID, topic))
                            subscriberTopics.remove(topic);
                        System.out.println("Unsubscribed from the Topic: " + topic.getTopicID() + " - " + topic.getTopicName());
                        return;
                    } catch(RemoteException e) {
                            System.err.println("Could not connect to server. Retrying...");
                        try {
                            Thread.sleep(800);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.err.println("Couldn't Unsubscribe from " + topic.getTopicID() + " - " + topic.getTopicName());
            }
        }).start();
    }


    /**
     * This function helps an agent to unsubscribe from all topics.
     */
    public void unsubscribe() {
        new Thread(new Runnable() {
            public void run() {
                int attempts = 0;
                while(++attempts < 20) {
                    try {
                        if (server.removeSubscriber(PubSubAgent.this.agentID)) {
                            subscriberKeywords.clear();
                            subscriberTopics.clear();
                        }
                        System.out.print("Unsubscribed from all Topics.");
                        return;
                    } catch(RemoteException e) {
                        System.err.println("Could not connect to server. Retrying...");
                        try {
                            Thread.sleep(800);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.err.println("Couldn't Unsubscribe from all the topics...");
            }
        }).start();
    }


    /**
     * This function helps to list out the topics that have been advertised by an agent.
     */
    public void topicsAdvertisedByPub() {
        int index = 1;
        System.out.println("Topics advertised by this publisher: ");
        for(Topic topic: pubTopics){
            System.out.println(index + "->" + topic + "\n");
            index++;
        }
    }

    /**
     *  This helps to list out all the events published by an agent.
     */
    public void eventsPublishedByPub() {
        int index = 1;
        System.out.println("Events published by this publisher: ");
        for (Event event : pubEvents) {
            System.out.println(index + "->" + event + "\n");
            index++;
        }
    }


    /**
     * This helps to publish an event if the topic exists.
     * @param event Details associated with the event
     */
    public void publish(final Event event) {
        if (event == null)
            return;

        new Thread(new Runnable() {
            public void run() {
                int attempts = 0;
                while(++attempts < 20) {
                    try {
                        int uniqueID = server.publish(event);
                        if (uniqueID != 0)
                            pubEvents.add(event.setID(uniqueID) );
                        return;
                    } catch(RemoteException e) {
                        System.err.println("Could not connect to server. Retrying...");
                        try {
                            Thread.sleep(800);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.err.println("Couldn't Publish Event: " + event.getID() + " - " + event.getTitle());
            }
        }).start();
    }


    /**
     * This function helps notify the agents about a new topic.
     * @param topic Object containing details of the topic.
     * @throws RemoteException
     */
    public void notifyAd(Topic topic) throws RemoteException {
        System.out.println("Topic Advertisement Received");
        System.out.println("These are the Details: ");
        System.out.println(topic);
        receivedTopics.add(topic);
    }


    /**
     * This function helps advertise a new topic that has been created.
     * @param t contains details of a topic
     */
    public void advertise(final Topic t) {

        new Thread(new Runnable() {
            public void run() {
                int attempts = 0;
                while(++attempts < 20) {
                    try {
                        int uniqueID = server.addTopic(t);
                        if (uniqueID != 0)
                            pubTopics.add(t.setTopicID(uniqueID));
                        else
                            System.err.println("Topic already exists on server. Please Select Another Name...");
                        return;
                    } catch(RemoteException e) {
                        System.err.println("Could not connect to server. Retrying...");
                        try {
                            Thread.sleep(800);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.err.println("Couldn't Advertise Topic: " +  t.getTopicID() + " - " + t.getTopicName());
            }
        }).start();
    }



    /**
     * The function helps to find a topic.
     * @return topic object if found or NULL.
     */
    public Topic findTopic() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the name of the Topic:");
        String identifier = input.nextLine().trim();
        List<Topic> topicList;
        try {
            topicList = server.getTopics();
        } catch (RemoteException f) {
            System.err.println("Couldn't Connect to Server...");
            return null;
        }
        try {
            for (Topic t : topicList ) {
                String topicName = t.getTopicName().toLowerCase();
                if (topicName.equals(identifier.toLowerCase())) {
                    return t;
                }
            }
            System.out.println("Topic is not listed.");

        } catch (NumberFormatException e) {
            System.err.println("This is not a Name");
        }
        return null;
    }


    /**
     * This function helps an agent to save it's state and quit the server and save the
     * information on a file to use it when it reconnects.
     *
     * @throws RemoteException
     */
    public void saveState() throws RemoteException {
        server.unbind(this.agentID);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("agent.dat"));
            oos.writeObject(this);
            oos.close();
        } catch (Exception e) {
            System.out.println("Object not saved correctly.");
        } finally {
            System.exit(0);
        }
    }


    /**
     * A console for the publisher.
     *
     * @throws RemoteException
     */
    public void optionsForPublisher() throws RemoteException {
        Scanner in = new Scanner(System.in);
        boolean breakLoop = false;
        do {
            System.out.println("What would you like to do as a Publisher:");
            System.out.println(" 1: Create and Advertise a topic.");
            System.out.println(" 2: Create and Publish an event.");
            System.out.println(" 3: List your advertised topics.");
            System.out.println(" 4: List your created events.");
            System.out.println(" 5: Save & Quit.");
            System.out.print("Enter an option: ");

            int choice = in.nextInt();


            switch (choice) {
                case 1: {
                    System.out.println("Enter topic name:");
                    in = new Scanner(System.in);
                    String name = in.nextLine().trim();
                    System.out.println("Enter keywords separated by commas: ");
                    in = new Scanner(System.in);
                    String keywords = in.nextLine().trim();
                    Topic t = new Topic(name, keywords);
                    advertise(t);
                    break;
                }
                case 2:{
                    Topic topic = findTopic();
                    if ( topic != null) {
                        System.out.println("Enter title for event: ");
                        in = new Scanner(System.in);
                        String title = in.nextLine().trim();
                        System.out.println("Enter keywords for event separated by commas [OPTIONAL]: ");
                        in = new Scanner(System.in);
                        String keywords = in.nextLine().trim();
                        if (keywords.length() == 0) {
                            keywords = null;
                        }
                        System.out.println("Enter content for the event [OPTIONAL]: ");
                        String content = "";
                        in = new Scanner(System.in);
                        String line = in.nextLine();
                        content += line;
                        Event e = new Event(topic, title, content, keywords);
                        publish(e);
                    }

                    break;
                }
                case 3: {
                    topicsAdvertisedByPub();
                    break;
                }
                case 4: {
                    eventsPublishedByPub();
                    break;

                }
                case 5: {
                    breakLoop = true;
                    in.close();
                    saveState();
                    break;
                }
                default:
                    System.out.println("Please enter a Valid Option.\n");
            }
        } while (!breakLoop);
    }


    /**
     * A console for the Subscriber
     * @throws RemoteException
     */
    public void optionsForSubscriber() throws RemoteException {
        Scanner in = new Scanner(System.in);
        boolean breakLoop = false;

        do {
            System.out.println("What would you like to do as a Subscriber:");
            System.out.println(" 1: Subscribe to a Topic.");
            System.out.println(" 2: Unsubscribe from a Topic.");
            System.out.println(" 3: Unsubscribe from all Topics.");
            System.out.println(" 4: Display subscribed topics.");
            System.out.println(" 5: View all available topics.");
            System.out.println(" 6: View all received events.");
            System.out.println(" 7: Save & Quit.");
            System.out.print("Enter an Option: ");

            int choice = in.nextInt();
            Topic topic = null;


            switch (choice) {
                case 1: {
                    topic = findTopic();
                    if (topic != null) {
                        subscribe(topic);
                    }
                    break;
                }
                case 2: {
                    topic = findTopic();
                    if (topic != null) {
                        unsubscribe(topic);
                    }
                    break;
                }
                case 3: {
                    unsubscribe();
                    break;
                }
                case 4: {
                    listSubscribedTopics();
                    break;
                }
                case 5: {
                    try {
                        ArrayList<Topic> allTopics = server.getTopics();
                        for (Topic t : allTopics)
                            System.out.print(t);
                    } catch (RemoteException e) {
                        System.out.println("Couldn't Connect to Server. Try again");
                    }
                    break;
                }
                case 6: {
                    listReceivedEvents();
                    break;
                }
                case 7: {
                    breakLoop = true;
                    in.close();
                    saveState();
                    break;
                }
                default:
                    System.out.println("Please enter a valid option.");
            }
        } while (!breakLoop);
    }


    /**
     * A console for the Agent to select how it wants to act as.
     *
     * @throws RemoteException
     */
    public void console() throws RemoteException {
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("Select one of the following options:");
            System.out.println(" 1: Be a publisher");
            System.out.println(" 2: Be a subscriber");
            System.out.println(" 3: Save & quit");
            System.out.print("Enter a number:");
            int choice = 0;
            try {
                choice = in.nextInt();
            } catch (Exception e) {
                System.err.println("Provide a Valid Option... ");
            }
            switch (choice) {
                case 1: {
                    optionsForPublisher();
                    break;
                }
                case 2: {
                    optionsForSubscriber();
                    break;
                }
                case 3: {
                    in.close();
                    saveState();
                    break;
                }
                default: System.out.println("Input not recognized, Please enter a valid option...");
            }
        } while (true);
    }



}