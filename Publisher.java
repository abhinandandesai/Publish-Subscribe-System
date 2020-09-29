package edu.rit.cs;
public interface Publisher {

    /**
     * Publish an event of a specific topic with title, content, and optional keywords
     *
     * @param event to be published
     */
    public void publish(Event event);

    /**
     * Advertise new topic for Agents.
     *
     * @param newTopic
     */
    public void advertise(Topic newTopic);

}