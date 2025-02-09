package br.gft.template.demo.events;

import br.gft.template.demo.exceptions.InternalException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultConverter<T> implements Converter<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public DefaultConverter(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    @Override
    public T convert(String value) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new InternalException("Erro ao converter a mensagem para " + clazz.getName(), e);
        }
    }
}
