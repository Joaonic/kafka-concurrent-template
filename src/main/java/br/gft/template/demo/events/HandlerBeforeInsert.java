package br.gft.template.demo.events;

public interface HandlerBeforeInsert<T> {

    void handle(T document, Object... objects);
}
