package com.kafkaLibrary.domain;

public record Book(

        Integer bookId,
        String bookName,
        String bookAuthor
) {
}
