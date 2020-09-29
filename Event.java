package edu.rit.cs;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * This class helps to handle the event details.
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private int eventID = 0;
    private Topic topic;
    private String title;
    private String content;
    private String[] keywords;
    private LinkedHashSet<Integer> pendingNotifications;

    /**
     * Constructor for the class.
     * @param topic Topic for the event
     * @param title Title for the event
     * @param content Information for the event
     * @param keywords Words to associate the event to.
     */
    public Event(Topic topic, String title, String content, String keywords){
        this.topic = topic;
        this.title = title;
        this.content = content;
        if(keywords == null){
            this.keywords = topic.getKeywords();
        }
        else{
            this.keywords = keywords.split(",");
        }
        pendingNotifications = new LinkedHashSet<>();
    }

    /**
     * Helps to set a unique ID for the event
     * @param number ID number
     * @return Object
     */
    public Event setID(int number){
        this.eventID = number;
        return this;
    }


    /**
     * Returns ID
     * @return ID
     */
    public int getID() {
        return eventID;
    }

    /**
     * returns topic
     * @return object
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Returns event Title.
     * @return String
     */
    public String getTitle() {
        return this.title;
    }


    /**
     * Return information about the event.
     * @return the content of this Event
     */
    public String getContent() {
        return content;
    }

    /**
     *  Returns keywords associated with the event.
     * @return keywords used for filtering that match to this event
     */
    public String[] getKeywords() {
        return keywords;
    }



    /**
     * Override the equals function in order to hash an Event correctly and keep it unique.
     */
    public boolean equals(Object obj) {
        Event e = (Event) obj;
        return this.topic.equals(e.topic) && this.title.equals(e.title);
    }

    /**
     * Defining my own hashcode function for the Event.
     */
    public int hashCode() {
        return topic.hashCode() + title.hashCode();
    }

    /**
     * Overriding the function to display according to requirements.
     * @return
     */
    public String toString() {
        String eventDetails = "EventID: " +this.eventID + "\n" +
                "Title: "+ this.title + "\n" +
                "Main Topic: "+ topic.getTopicID() +"-"+ topic.getTopicName() + "\n" +
                "Content: " + content + "\n" +
                "Keywords: ";
        for (int index = 0; index <keywords.length; index++) {
            if (index == (keywords.length - 1)) {
                eventDetails += keywords[index] + ".\n";
            } else {
                eventDetails += keywords[index] + ", ";
            }
        }
        return eventDetails;
    }

    /**
     * Iterator to allow for removal of Subscribers after they are notified.
     */
    public synchronized Iterator<Integer> iterator() {
        return pendingNotifications.iterator();
    }

    /**
     * Number of clients left to notify
     */
    public synchronized int clientsLeft() {
        return pendingNotifications.size();
    }

    /**
     *
     * @param list List of subscribers.
     * @return True if list was added else false.
     */
    public synchronized boolean addSubscriberList(Collection<Integer> list) {
        if (list != null)
            return pendingNotifications.addAll(list);
        return false;
    }
}
