# Reproduces a Jackson serializer bug introduced in 2.11.2

## To reproduce

```
./gradlew test
```

Fails with this exception

```
com.github.lhotari.jacksonbug.JacksonBugTest > reproduceSerializerBug() FAILED
    com.fasterxml.jackson.databind.JsonMappingException: Strange Map type java.util.Map: cannot determine type parameters (through reference chain: com.github.lhotari.jacksonbug.JacksonBugTest$MyValue["events"]->java.util.Collections$SingletonList[0]->io.cloudevents.v1.CloudEventImpl["attributes"])
        at com.fasterxml.jackson.databind.JsonMappingException.from(JsonMappingException.java:295)
        at com.fasterxml.jackson.databind.SerializerProvider.reportMappingProblem(SerializerProvider.java:1309)
        at com.fasterxml.jackson.databind.SerializerProvider._createAndCacheUntypedSerializer(SerializerProvider.java:1447)
        at com.fasterxml.jackson.databind.SerializerProvider.findValueSerializer(SerializerProvider.java:562)
        at com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter._findAndAddDynamic(UnwrappingBeanPropertyWriter.java:211)
        at com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter.serializeAsField(UnwrappingBeanPropertyWriter.java:102)
        at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:755)
        at com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:178)
        at com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:119)
        at com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serialize(IndexedListSerializer.java:79)
        at com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serialize(IndexedListSerializer.java:18)
        at com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:728)
        at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:755)
        at com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:178)
        at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:480)
        at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:319)
        at com.fasterxml.jackson.databind.ObjectMapper._writeValueAndClose(ObjectMapper.java:4407)
        at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(ObjectMapper.java:3661)
        at com.github.lhotari.jacksonbug.JacksonBugTest.reproduceSerializerBug(JacksonBugTest.java:39)

        Caused by:
        java.lang.IllegalArgumentException: Strange Map type java.util.Map: cannot determine type parameters
            at com.fasterxml.jackson.databind.type.TypeFactory._mapType(TypeFactory.java:1178)
            at com.fasterxml.jackson.databind.type.TypeFactory._fromWellKnownClass(TypeFactory.java:1471)
            at com.fasterxml.jackson.databind.type.TypeFactory._fromClass(TypeFactory.java:1414)
            at com.fasterxml.jackson.databind.type.TypeFactory.constructType(TypeFactory.java:705)
            at com.fasterxml.jackson.databind.introspect.AnnotatedClass.resolveType(AnnotatedClass.java:229)
            at com.fasterxml.jackson.databind.introspect.AnnotatedMethod.getParameterType(AnnotatedMethod.java:143)
            at com.fasterxml.jackson.databind.introspect.AnnotatedWithParams.getParameter(AnnotatedWithParams.java:86)
            at com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector._addCreators(POJOPropertiesCollector.java:500)
            at com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector.collectAll(POJOPropertiesCollector.java:327)
            at com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector.getJsonValueAccessor(POJOPropertiesCollector.java:203)
            at com.fasterxml.jackson.databind.introspect.BasicBeanDescription.findJsonValueAccessor(BasicBeanDescription.java:252)
            at com.fasterxml.jackson.databind.ser.BasicSerializerFactory.findSerializerByAnnotations(BasicSerializerFactory.java:396)
            at com.fasterxml.jackson.databind.ser.BeanSerializerFactory._createSerializer2(BeanSerializerFactory.java:216)
            at com.fasterxml.jackson.databind.ser.BeanSerializerFactory.createSerializer(BeanSerializerFactory.java:165)
            at com.fasterxml.jackson.databind.SerializerProvider._createUntypedSerializer(SerializerProvider.java:1474)
            at com.fasterxml.jackson.databind.SerializerProvider._createAndCacheUntypedSerializer(SerializerProvider.java:1442)
            ... 16 more
```

## Comments

* Test passes with Jackson 2.11.1 version, but fails with [2.11.2](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.11.2) .
* [Diff between Jackson 2.11.2 and 2.11.1](https://github.com/FasterXML/jackson-databind/compare/jackson-databind-2.11.1...jackson-databind-2.11.2)

Changes for #2796 might have caused the change in behavior.

Looks like the problem that https://github.com/FasterXML/jackson-databind/commit/910edfb634f55cdb8d78ac7d9caf00d8133a11e6 fixes could have been similar.

By debugging it can be seen that resolving parameter types with the bindings in AnnotedClass doesn't produce the correct result.
