package edu.rit.cs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.*;


/**
 * This class handles all server operations and majorly helps to notify about events and advertise Topics.
 */
public class EventManager extends UnicastRemoteObject implements EventInterface{

    private static final long serialVersionUID = 1L;
    //counters used to assign Unique IDs
    protected int topicID = 0;
    protected Integer subID = 0;
    protected Integer eventID = 0;
    protected LinkedHashSet<SubscriberManager> subsAndTopics ;
    //Events are stored here while they continue to try to contact a missing subscriber
    protected LinkedList<Event> pendingEvents;
    protected LinkedList<Topic> pendingTopics;

    // Maps from the name of a keyword to the ID of the clients that receive those keyword events
    // in order to allow for efficient content-filtering
    protected HashMap<String, LinkedHashSet<Integer>> contentFilter;
    protected ArrayList<Integer> agents;
    // Maps from the ID of a client to the actual RMI object of the client
    // This allows the client to leave and come back later without
    //changing the unique identifier
    protected HashMap<Integer, Subscriber> clientBinding;

    /**
     * Constructor of the class
     * @throws RemoteException
     */
    public EventManager() throws RemoteException {
        subsAndTopics = new LinkedHashSet<>();
        pendingEvents = new LinkedList<>();
        pendingTopics = new LinkedList<>();
        contentFilter = new HashMap<>();
        clientBinding = new HashMap<>();
        agents = new ArrayList<>();
    }

    /**
     * Provides a unique ID for the subscriber
     *
     * @param sub Subscriber details
     * @return Unique ID number
     * @throws RemoteException
     */
    public int connect(Subscriber sub) throws RemoteException {
        synchronized (clientBinding) {
            subID += 1;
            clientBinding.put(subID, sub);
            return subID;
        }


    }

    /**
     * Helps to reconnect an offline agent
     *
     * @param ID unique ID of the agent
     * @param sub Subscriber or agent details
     * @return unique ID given on first connection
     * @throws RemoteException
     */
    public int reConnect(Integer ID, Subscriber sub) throws RemoteException {
        synchronized (clientBinding) {
            clientBinding.put(ID, sub);
            return ID;
        }
    }

    /**
     * Removes the agent details from the ID
     *
     * @param ID
     */
    public void unbind(Integer ID) {
        synchronized (clientBinding) {
            clientBinding.put(ID, null);
        }
    }

    /**
     * Returns the object containing details of the subscriber.
     *
     * @param ID
     * @return
     */
    public Subscriber getSubscriber(Integer ID) {

        return clientBinding.get(ID);
    }


    /**
     * This function notifies events all the offline subscribers when they reconnect with the server.
     */
    public void startService() {

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {

                    }
                    synchronized (pendingEvents) {
                        while (pendingEvents.isEmpty()) {
                            try {
                                pendingEvents.wait();
                            } catch (Exception e) {

                            }
                        }
                        Iterator<Event> eventIterator = pendingEvents.iterator();
                        while (eventIterator.hasNext()) {
                            Event e = eventIterator.next();
                            if (notifySubs(e) == 0)
                                eventIterator.remove();
                        }
                    }
                }
            }
        });


        //Daemon allows this thread not to block program from exiting
        t.setDaemon(true);
        t.start();
    }


    /**
     * This function helps advertise the topics to offline agents when they come online again
     */
    public void startServiceAds() {

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {

                    }
                    synchronized (pendingTopics) {
                        while (pendingTopics.isEmpty()) {
                            try {
                                pendingTopics.wait();
                            } catch (Exception e) {

                            }
                        }
                        Iterator<Topic> topicIterator = pendingTopics.iterator();
                        while (topicIterator.hasNext()) {
                            Topic t = topicIterator.next();
                            if (notifyAgents(t) == 0)
                                topicIterator.remove();
                        }
                    }
                }
            }
        });


        //Daemon allows this thread not to block program from exiting
        t.setDaemon(true);
        t.start();
    }


    /**
     * Function to notify the subscribers about the event
     * @param event details of the event
     * @return return the number of agents left to notify
     */
    public int notifySubs(Event event){
        Iterator<Integer> subsIterator = event.iterator();
        while (subsIterator.hasNext()) {
            try {
                Integer subID = subsIterator.next();
                if (clientBinding.get(subID) != null) {
                    clientBinding.get(subID).notify(event);
                    subsIterator.remove();
                }
            } catch(RemoteException e) { } //Do nothing on remote exception, try again later
        }
        //when this returns 0, we know every subscriber has received the message
        int pending = event.clientsLeft();
        return pending;
    }


    /**
     * Notify all agents about the advertised topic
     *
     * @param topic details of the advertised topic
     * @return number of agents left to notify.
     */
    public int notifyAgents(Topic topic){
        Iterator<Integer> agentIterator = topic.iterator();
        while (agentIterator.hasNext()) {
            try {
                Integer agentID = agentIterator.next();
                if (clientBinding.get(agentID) != null) {
                    clientBinding.get(agentID).notifyAd(topic);
                    agentIterator.remove();
                }
            } catch(RemoteException e) { } //Do nothing on remote exception, try again later
        }
        //when this returns 0, we know every subscriber has received the message
        int pending = topic.agentsLeft();
        return pending;
    }


    /**
     * Function that handles or initiates the advertising process
     *
     * @param topic details of the topic
     * @return Unique ID of the topic
     * @throws RemoteException
     */
    public int advertise(Topic topic) throws RemoteException {
        synchronized (clientBinding) {
            Iterator it = clientBinding.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
               // System.out.println(pair.getKey() + " = " + pair.getValue());
                topic.addAgents((Integer) pair.getKey());
                it.remove(); // avoids a ConcurrentModificationException
            }

            if ( notifyAgents(topic) > 0) {
                synchronized (pendingTopics) {
                    pendingTopics.add(topic);
                    pendingTopics.notifyAll();
                }
            }
            return topicID;
        }
    }


    /**
     * The function handles and initiates the publishing process
     *
     * @param event details of event being published
     * @return unique ID of the Event
     * @throws RemoteException
     */
    public int publish(Event event) throws RemoteException {
        if (event.getID() != 0) {
            System.err.println("Event has already been published.");
            return 0;
        }
        synchronized (subsAndTopics) {
            for(SubscriberManager sAT : subsAndTopics) {
                Topic t1 = sAT.getTopic();
                Topic t2 = event.getTopic();
                if (t1.getTopicID() == t2.getTopicID()) {
                    eventID +=1;
                    event.setID(eventID);
                    event.addSubscriberList(sAT.getSubscribers());
                    String[] eventKeywords = event.getKeywords();
                    for(String key : eventKeywords )
                        event.addSubscriberList(contentFilter.get(key) );
                    if (notifySubs(event) > 0) {
                        synchronized (pendingEvents) {
                            pendingEvents.add(event);
                            pendingEvents.notifyAll();
                        }
                    }
                    return eventID;
                }
            }
        }
        System.err.println("Event topic not found.");
        return 0;
    }


    /**
     * Adding the advertised topic to database
     *
     * @param topic details of the topic
     * @return unique ID of the topic
     * @throws RemoteException
     */
    public int addTopic(Topic topic) throws RemoteException {
        synchronized (subsAndTopics) {
            if (topic != null) {
                subsAndTopics.add(new SubscriberManager(topic));
                topicID += 1;
                topic.setTopicID(topicID);
                advertise(topic);
                return topicID;
            }
            return 0;
        }
    }

    /**
     * Adding a new subscriber to an event
     *
     * @param subID Unique ID of the agent
     * @param topic details of the topic
     * @return True if successful, else False
     * @throws RemoteException
     */
    public boolean addSubscriber(Integer subID, Topic topic) throws RemoteException {
        for( SubscriberManager sAT : subsAndTopics) {
            if (sAT.getTopic().getTopicID() == topic.getTopicID()) {

                return sAT.addSubscriber(subID);
            }
        }
        return false;
    }

    /**
     *  Removing a Subscriber from a particular topic it unsubscribes from.
     *
     * @param subID unique ID number of the agent
     * @param topic details of the topic
     * @return True if successful, else False
     * @throws RemoteException
     */
    public boolean removeSubscriber(Integer subID, Topic topic) throws RemoteException {
        for( SubscriberManager sAT : subsAndTopics) {
            Topic t = sAT.getTopic();
            if (t.getTopicID() == topic.getTopicID()) {
                return sAT.removeSubscriber(subID);
            }
        }
        return false;
    }

    /**
     *  Removing a subscriber from all the topics.
     *
     * @param subID Unique ID of the agent
     * @return True if successful, else False
     * @throws RemoteException
     */
    public boolean removeSubscriber(Integer subID) throws RemoteException {
        for( SubscriberManager sAT : subsAndTopics) {
            sAT.removeSubscriber(subID);
        }
        return true;
    }

    /**
     * Displaying the Subscribers for the Topics
     *
     * @throws RemoteException
     */
    public void displaySubs() throws RemoteException {
        // Subscribers for specific topics
        for(SubscriberManager sAT : subsAndTopics) {
            Topic t = sAT.getTopic();
            System.out.print("Topic Title: " + t.getTopicName() + "\n" + sAT.printSubscribers());
            String contentPrint = "";
        }


        //Agents that have connected.
        Iterator it = clientBinding.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            System.out.println(((Integer) pair.getKey()) + " = " + pair.getValue());
        }

    }

    /**
     * Returning all topics.
     *
     * @return List of Topic Objects
     */
    public ArrayList<Topic> getTopics() {
        synchronized (subsAndTopics) {
            ArrayList<Topic> topicList = new ArrayList<>();
            for (SubscriberManager sAT : subsAndTopics)
                topicList.add(sAT.getTopic() );
            return topicList;
        }
    }

    /**
     * A console for the Server showing different functionalities
     * @throws RemoteException
     */
    public void serverConsole() throws RemoteException {
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("What would you like to do?");
            System.out.println(" 1: Display Topics.");
            System.out.println(" 2: Display Subscribers.");
            System.out.println(" 3: Quit server.");
            System.out.print("Enter an Option: ");
            int choice = 0;
            choice = in.nextInt();


            switch (choice) {
                case 1: {
                    for (SubscriberManager sAT : subsAndTopics) {
                        Topic t = sAT.getTopic();
                        System.out.print(t);
                    }
                    break;
                }
                case 2: {
                    displaySubs();
                    break;
                }
                case 3: {
                    in.close();
                    System.exit(0);
                }
                default:
                    System.out.println("Invalid Option");
            }
        } while (true);
    }


}
