package br.gft.template.demo.events;

public interface HandlerAfterInsert<T> {

    void handle(T document, Object... objects);
}
