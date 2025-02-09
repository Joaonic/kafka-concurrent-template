package br.gft.template.demo.mapper.json;

import br.gft.template.demo.exceptions.InternalException;
import br.gft.template.demo.mapper.MapperService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JsonMapperService implements MapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperService.class);

    private static final String ERROR_MESSAGE = "Can't parse object";

    private final ObjectMapper mapper;

    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }


    public byte[] writeValueAsBytes(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }

    public <T> T readValue(byte[] content, Class<T> t) {
        try {
            return mapper.readValue(content, t);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }

    public <T> T readValue(String content, Class<T> t) {
        try {
            return mapper.readValue(content, t);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }

    public <T> T readValue(InputStream content, Class<T> t) throws IOException {
        return readValue(content.readAllBytes(), t);
    }

    public <T> List<T> readValuesList(String content, TypeReference<List<T>> tList) {
        try {
            return mapper.readValue(content, tList);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }

    public <T> T readValuesRef(String content, TypeReference<T> tRef) {
        try {
            return mapper.readValue(content, tRef);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }


    public <T> MappingIterator<T> getMappingIterator(File file, Class<T> typeParameterClass) throws IOException {
        return mapper
                .readerFor(typeParameterClass)
                .readValues(file);
    }

    public Map<String, Object> readValuesAsMap(String value) {
        try {
            return mapper.readValue(value, Map.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalException(ERROR_MESSAGE);
        }
    }

}
