package com.github.lhotari.jacksonbug;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.cloudevents.CloudEvent;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class JacksonBugTest {
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static final class MyValue {
        private final List<CloudEvent<?, ?>> events;

        @JsonCreator
        public MyValue(List<CloudEvent<?, ?>> events) {
            this.events = events;
        }

        public List<CloudEvent<?, ?>> getEvents() {
            return this.events;
        }
    }

    // this test passes with Jackson 2.11.1, but fails with Jackson 2.11.2
    @Test
    public void reproduceSerializerBug() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        CloudEventImpl<String> cloudEvent = CloudEventBuilder.<String>builder()
                .withId("id")
                .withType("type")
                .withSource(URI.create("urn:source"))
                .withData("Hello")
                .build();
        MyValue val = new MyValue(Collections.singletonList(cloudEvent));
        // fails with com.fasterxml.jackson.databind.JsonMappingException: Strange Map type java.util.Map: cannot determine type parameters (through reference chain: com.github.lhotari.jacksonbug.JacksonTest$MyValue["events"]->java.util.Collections$SingletonList[0]->io.cloudevents.v1.CloudEventImpl["attributes"])
        objectMapper.writeValueAsString(val);
    }
}
