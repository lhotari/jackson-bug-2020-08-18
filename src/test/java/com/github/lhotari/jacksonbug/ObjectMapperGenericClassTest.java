package com.github.lhotari.jacksonbug;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

// https://github.com/FasterXML/jackson-databind/issues/2821#issuecomment-687546808
public class ObjectMapperGenericClassTest {

    static final String JSON = "{ \"field\": { \"number\": 1 }, \"map\": { \"key\": \"value\" } }";

    static class GenericEntity<T> {
        T field;

        Map map;

        public void setField(T field) {
            this.field = field;
        }

        public T getField() {
            return field;
        }

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }
    }

    static class SimpleEntity {
        Integer number;

        public void setNumber(Integer number) {
            this.number = number;
        }

        public Integer getNumber() {
            return number;
        }
    }

    @Test
    public void test() throws Exception {
        ObjectMapper m = new ObjectMapper();
        GenericEntity<SimpleEntity> genericEntity = m.readValue(JSON, new TypeReference<GenericEntity<SimpleEntity>>() {
        });
    }
}
