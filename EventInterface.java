package edu.rit.cs;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface EventInterface extends java.rmi.Remote {

    /**
     * This method establishes connection between the server and a client
     *
     * @param sub Subscriber object connection to server
     * @return Unique ID for the subscriber.
     * @throws RemoteException
     */
    public int connect(Subscriber sub) throws RemoteException;

    /**
     * This method helps reconnect the client to the server
     *
     * @param sub Subscriber object that reconnects with the server
     * @param Unique ID number that was assigned to the agent on first connection
     * @return the ID number
     * @throws RemoteException
     */
    public int reConnect(Integer ID, Subscriber sub) throws RemoteException;

    /**
     * Publishes advertises a new topic
     *
     * @param topic the topic the Publisher wants to advertise
     * @return unique ID of this Topic
     * @throws RemoteException if server is offline
     */
    public int addTopic(Topic topic) throws RemoteException;

    /**
     * When agent subscribes, add it to the list
     *
     * @param subID unique  ID of the agent
     * @param t Topic of subscription
     * @return True if successful else False
     * @throws RemoteException
     */
    public boolean addSubscriber(Integer subID, Topic t) throws RemoteException;


    /**
     * Remove the agent the unsubscribes from all topics
     *
     * @param subID unique ID of the agent
     * @return True of successful else False
     * @throws RemoteException
     */
    public boolean removeSubscriber(Integer subID) throws RemoteException;

    /**
     * Remove the agent from the topic it unsubscribes from
     *
     * @param subID unique ID of the client
     * @param t Topic from which it unsubscribes
     * @return True if successful, False if not
     * @throws RemoteException
     */
    public boolean removeSubscriber(Integer subID, Topic t) throws RemoteException;


    /**
     * Publish an event to the subscribers.
     *
     * @param event Details of the event
     * @return Unique ID of the event
     * @throws RemoteException
     */
    public int publish(Event event) throws RemoteException;

    /**
     * Return the complete list of Topics available.
     *
     * @return ArrayList of Topics available
     * @throws RemoteException
     */
    public ArrayList<Topic> getTopics() throws RemoteException;

    /**
     * To disconnect from the server after saving it's state
     *
     * @param ID subscriberID
     * @throws RemoteException
     */
    public void unbind(Integer ID) throws RemoteException;


    /**
     * Search and return the Object corresponding to the specifice ID
     *
     * @param ID subscriberID
     * @return Subscriber object
     * @throws RemoteException
     */
    public Subscriber getSubscriber(Integer ID) throws RemoteException;
}