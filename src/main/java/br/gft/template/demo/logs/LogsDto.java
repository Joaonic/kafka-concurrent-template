package br.gft.template.demo.logs.marker;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class LogsDto {

    private LogstashMarker markers = Markers.empty();

    private Map<String, Object> map = new HashMap<>();
}