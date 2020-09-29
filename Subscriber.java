package edu.rit.cs;


import java.rmi.RemoteException;



public interface Subscriber extends java.rmi.Remote {

    /**
     * Subscribe to a topic
     *
     * @param topic Object containing topic details
     * @throws RemoteException
     */
    public void subscribe(Topic topic) throws RemoteException;



    /**
     * Unsubscribe from a topic
     *
     * @param topic Object containing topic details.
     * @throws RemoteException
     */
    public void unsubscribe(Topic topic) throws RemoteException;



    /**
     * Unsubscribe from all topics
     * @throws RemoteException
     */
    public void unsubscribe() throws RemoteException;

    /**
     * Notify the subscribers about a new event
     *
     * @param e Details about the event to be published
     * @throws RemoteException
     */
    public void notify(Event e) throws RemoteException;

    /**
     * Notify all agents about a new Topic.
     *
     * @param e Details about the Topic to be advertised
     * @throws RemoteException
     */
    public void notifyAd(Topic topic) throws RemoteException;

}