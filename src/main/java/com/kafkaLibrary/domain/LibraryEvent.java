package com.kafkaLibrary.domain;


public record LibraryEvent(
        Integer LibraryEventId,
        LibraryEventType libraryEventType,

        Book book
) {
}
