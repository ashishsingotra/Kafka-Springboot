package com.kafkaLibrary.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaLibrary.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        var CompletableFuture = kafkaTemplate.send(topic,key,value);

        return CompletableFuture
                .whenComplete((sendResult, throwable) -> {
                    if(throwable != null) {
                        handleFailure(key,value,throwable);
                    }else{
                        handleSuccess(key,value,sendResult);
                    }
                });
    }
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent_producerRecord(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        var producerRecord = buildProducerRecord(key,value);
        var CompletableFuture = kafkaTemplate.send(producerRecord);

        return CompletableFuture
                .whenComplete((sendResult, throwable) -> {
                    if(throwable != null) {
                        handleFailure(key,value,throwable);
                    }else{
                        handleSuccess(key,value,sendResult);
                    }
                });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {

        List<Header> recordHeaders = List.of(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord<>(topic,null,key,value,recordHeaders);
    }

    public SendResult<Integer, String> sendLibraryEvent_sync(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        var sendResult = kafkaTemplate.send(topic,key,value).get();
        handleSuccess(key,value,sendResult);
        return sendResult;
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {

        log.info("Message sent successfully for the key : {} and the value : {} , partition is ; {} ",
                key,value,sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message and exception is {}", ex.fillInStackTrace(),ex);
    }
}
