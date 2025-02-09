package br.gft.template.demo.logs.marker;

import br.gft.template.demo.logs.LogsDto;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.Map;

/**
 * Global (per-thread) provider of Logstash Markers
 * and a Map containing the respective values.
 */
public class LogstashMarkerProvider {

    private LogstashMarkerProvider() {
        throw new UnsupportedOperationException("Class cannot be instantiated");
    }

    private static final ThreadLocal<LogsDto> threadContext = ThreadLocal.withInitial(LogsDto::new);

    public static LogsDto context() {
        return threadContext.get();
    }

    public static LogstashMarker markers() {
        return threadContext.get().getMarkers();
    }

    public static Map<String, Object> values() {
        return threadContext.get().getMap();
    }

    /**
     * Overwrites key of the thread Marker.
     *
     * @param key              the marker key
     * @param value            the marker value
     * @param <T>              the type of the value
     * @return the updated markers of the thread
     */
    public static <T> LogstashMarker add(String key, T value) {
        var map = threadContext.get().getMap();
        map.put(key, value);
        LogstashMarker markers = getMarkers(map);
        threadContext.get().setMap(map);
        threadContext.get().setMarkers(markers);
        return threadContext.get().getMarkers();
    }

    /**
     * Adds markers to the thread markers without overwriting keys.
     *
     * @param markers the markers to be added
     * @return the updated markers of the thread
     */
    public static LogstashMarker add(LogstashMarker markers) {
        return threadContext.get().getMarkers().and(markers);
    }

    /**
     * Adds unique markers to the thread.
     *
     * @param markersMap the map of markers to be added
     * @return the updated markers of the thread
     */
    public static LogstashMarker add(Map<String, ?> markersMap) {
        threadContext.get().getMap().putAll(markersMap);
        threadContext.get().setMarkers(getMarkers(threadContext.get().getMap()));
        return threadContext.get().getMarkers();
    }


    private static LogstashMarker getMarkers(Map<String, Object> map) {
        LogstashMarker markers = Markers.empty();
        for (var pair : map.entrySet()) {
            markers.add(Markers.append(pair.getKey(), pair.getValue()));
        }
        return markers;
    }

    public static void resetLogs() {
        threadContext.remove();
    }

}
