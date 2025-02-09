package br.gft.template.demo.shared.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface MapperService {

    String writeValueAsString(Object obj);

    byte[] writeValueAsBytes(Object obj);

    <T> T readValue(byte[] content, Class<T> t);

    <T> T readValue(String content, Class<T> t);

    <T> T readValue(InputStream content, Class<T> t) throws IOException;

    <T> List<T> readValuesList(String content, TypeReference<List<T>> tList);

    <T> T readValuesRef(String content, TypeReference<T> tRef);


    <T> MappingIterator<T> getMappingIterator(File file, Class<T> typeParameterClass) throws IOException;

    Map<String, Object> readValuesAsMap(String value);


}
