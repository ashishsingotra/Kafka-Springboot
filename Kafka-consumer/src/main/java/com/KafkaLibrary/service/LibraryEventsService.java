package com.KafkaLibrary.service;

import com.KafkaLibrary.entity.LibraryEvent;
import com.KafkaLibrary.repository.LibraryEventsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventsService {

    @Autowired
    LibraryEventsRepository libraryEventsRepository;

    @Autowired
    ObjectMapper objectMapper;

    public  void processLibraryEvent(ConsumerRecord<Integer,String> record) throws JsonProcessingException {

        LibraryEvent libraryEvent = objectMapper.readValue(record.value(),LibraryEvent.class);
        log.info("Library event received : {}",libraryEvent);

        switch (libraryEvent.getLibraryEventType()){
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                validate(libraryEvent);
                save(libraryEvent);
                    break;
               default:


        }
    }

    private void validate(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId() == null){
            throw new IllegalArgumentException("Library event id is null");
        }
        Optional<LibraryEvent> optionalLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if(!optionalLibraryEvent.isPresent()){
            throw new IllegalArgumentException("Library event id not found");
        }
        log.info("Validation successful for library event : {}",optionalLibraryEvent.get());
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
    }
}
