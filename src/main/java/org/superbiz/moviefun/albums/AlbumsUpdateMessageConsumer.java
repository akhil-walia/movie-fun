package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class AlbumsUpdateMessageConsumer {


    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateMessageConsumer(AlbumsUpdater albumsUpdater) {
        this.albumsUpdater = albumsUpdater;
    }


    public void consume(Message<?> message) {
        try {
            logger.debug("Starting albums update");
            //logger.debug("Message Body: {} ", (String)message.getPayload());
            albumsUpdater.update();
            logger.debug("Finished albums update");

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }
}
