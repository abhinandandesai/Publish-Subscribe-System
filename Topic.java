package edu.rit.cs;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collection;

/**
 * A Class to handle the Topic Details.
 */
public class Topic implements Serializable {
    private static final long serialVersionUID = 1L;
    private String[] keywords;
    private final String topicName;
    private int topicID;
    private LinkedHashSet<Integer> pendingAdvertise;

    /**
     * Constructor of the class
     * @param topicName Name of the topic
     * @param keywords words associaed with the topic
     */
    public Topic(String topicName, String keywords){
        this.topicName = topicName;
        this.keywords = keywords.split(",");
        this.pendingAdvertise = new LinkedHashSet<>();
    }

    /**
     * Return the topic ID for an object
     * @return ID number
     */
    public int getTopicID(){
        return topicID;
    }

    /**
     * Return keywords associated with the event.
     * @return Array of words
     */
    public String[] getKeywords(){
        return keywords;
    }

    /**
     * Return name of the Topic
     * @return String
     */
    public String getTopicName(){
        return topicName;
    }

    /**
     * Overriding Object equals function to hash correctly.
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        return this.topicName.equals(((Topic)obj).topicName);
    }

    /**
     * Iterator to allow for removal of Agents as they are notified.
     */
    public synchronized Iterator<Integer> iterator() {
        return pendingAdvertise.iterator();
    }

    /**
     * Used by SubscriberManager's hash to collide topics with same name.
     */
    public int hashCode() {
        return topicName.hashCode();
    }


    /**
     * Adding agent IDs to populate the list that needs to be notified
     *
     * @param ID that needs to be added to generate the notification list.
     * @return true if list was successfully added else false
     */
    public synchronized boolean addAgents(Integer ID) {
        if (ID != null)
            return pendingAdvertise.add(ID);
        return false;
    }

    /**
     * Agents left to notify of the topic advertisement.
     * @return
     */
    public synchronized int agentsLeft() {
        return pendingAdvertise.size();
    }

    /**
     * Setting a unique ID for the topic
     * @param idNum unique ID number
     * @return Object
     */
    public Topic setTopicID(int idNum){
        topicID = idNum;
        return this;
    }

    /**
     * Overriding the function to print according to requirements.
     * @return details of the topic.
     */
    public String toString(){
        String topicDetails = "ID: " + this.topicID + "\n" +
                "Name: " + this.topicName + "\n" +
                "Keywords: ";
        for (int index = 0; index <keywords.length; index++){
            if(index == (keywords.length - 1)){
                topicDetails += keywords[index] + ".\n";
            }
            else {
                topicDetails += keywords[index] + ", ";
            }
        }

        return topicDetails;
    }
}

