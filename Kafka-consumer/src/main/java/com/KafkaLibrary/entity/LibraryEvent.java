package com.KafkaLibrary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {

    @Id
    Integer libraryEventId;

    @Enumerated(EnumType.STRING)
    LibraryEventType libraryEventType;

    @OneToOne(mappedBy = "libraryEvent",cascade = CascadeType.PERSIST)
    @ToString.Exclude
    Book book;
}
