package edu.rit.cs;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A class that handles the subscribers of the Topics
 */
public class SubscriberManager {

    private Topic topic;
    private LinkedHashSet<Integer> topicSubs;


    /**
     * Constructor of the class. It creates an empty set for storing future subscribers.
     * @param topic Topic object
     */
    public SubscriberManager(Topic topic){
        this.topic = topic;
        topicSubs = new LinkedHashSet<>();
    }

    /**
     * Return the topic object
     * @return object
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Adding new subscriber to the topic
     * @param subscriberID of the agent
     * @return true if successful, false if not
     */
    public synchronized boolean addSubscriber(Integer subscriberID){
        return topicSubs.add(subscriberID);
    }

    /**
     * Removing particular subscriber when an agent unsubscribes
     * @param subscriberID of the agent
     * @return true if successful, false if not
     */
    public synchronized boolean removeSubscriber(Integer subscriberID){
        return topicSubs.remove(subscriberID);
    }

    /**
     * Returning the number of subscribers for a topic.
     * @return Number of subscribers
     */
    public synchronized int getListSize(){
        return topicSubs.size();
    }

    /**
     * Returning ID list of the subscribers
     * @return List of ID numbers
     */
    public synchronized LinkedHashSet<Integer> getSubscribers(){
        return topicSubs;
    }

    /**
     * Use the iterator to remove during iteration
     *
     * @return iterator to iterate
     */
    public synchronized Iterator<Integer> iterator() {
        return topicSubs.iterator();
    }


    /**
     * Overriding equals function accordingly.
     *
     * @param obj
     * @return true if equal and false if not equal
     */
    public boolean equals(Object obj) {
        return topic.equals( ((SubscriberManager)obj).getTopic() );
    }


    /**
     * It will ensure collision for similar objects and not allow to store them both.
     *
     */
    public int hashCode() {
        return topic.hashCode();
    }

    /**
     * Overriding the function to print acording to requirements.
     * @return
     */
    public synchronized String toString() {
        String result = topic.toString() + "\nSubscriber List: ";
        for(int subID: topicSubs){
            result += subID + " ";
        }

        result += "\n";

        return result;
    }

    /**
     *  A function to print the subscriber list
     * @return return the final list in a string form
     */
    public synchronized String printSubscribers(){
        String result = "\n\tSubscriber List: ";
        for(int subID: topicSubs){
            result += subID + " ";
        }

        result += "\n";

        return result;
    }
}
