package br.gft.template.demo.events;

public interface Converter<T> {
    T convert(String value);
}
