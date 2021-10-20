package com.limonnana.skate.web.rest;

import com.limonnana.skate.domain.Event;
import com.limonnana.skate.repository.EventRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Util {

    private final EventRepository eventRepository;
    private final Logger log = LoggerFactory.getLogger(Util.class);

    public Util(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public ResponseEntity<Event> getActiveEvent() throws Exception {
        log.debug("REST request to get Active Event from Util class: {}");
        List<Event> events = eventRepository.findAll();
        Event result = null;
        if (events == null) {
            throw new Exception();
        }
        for (Event e : events) {
            if (e.isActive()) {
                result = e;
                break;
            }
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(result));
    }
}
