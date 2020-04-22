package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of al message entries
     */
    List<Message> findAll();


    /**
     * Find a single message entry by id.
     *
     * @param id the id of the message entry
     * @return the message entry
     */
    Message findOne(Long id);

    /**
     * Publish a single message entry
     *
     * @param message to publish
     * @return published message entry
     */
    Message publishMessage(Message message);

}
