package unit.com.kafkalibrary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaLibrary.Controller.LibraryEventsController;
import com.kafkaLibrary.domain.LibraryEvent;
import com.kafkaLibrary.producer.LibraryEventsProducer;
import com.kafkalibrary.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LibraryEventsController.class)
class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibrayEvent() throws Exception {

        var jsonReq = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());

        when(libraryEventsProducer.sendLibraryEvent_producerRecord(isA(LibraryEvent.class)))
                .thenReturn(null);

        var expectedErrorMsg = "book.bookId - must not be null,book.bookName - must not be blank";
        mockMvc
                .perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(jsonReq)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    @Test
    void postLibrayEvent_invalidValues() throws Exception {

        var jsonReq = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());

        when(libraryEventsProducer.sendLibraryEvent_producerRecord(isA(LibraryEvent.class)))
                .thenReturn(null);

        var expectedErrorMsg = "book.bookId - must not be null,book.bookName - must not be blank";
        mockMvc
                .perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(jsonReq)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMsg));
    }
}