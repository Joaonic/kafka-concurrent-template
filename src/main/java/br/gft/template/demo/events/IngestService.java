package br.gft.template.demo.events;


public interface IngestService<T> {

    T ingest(T value);

}
