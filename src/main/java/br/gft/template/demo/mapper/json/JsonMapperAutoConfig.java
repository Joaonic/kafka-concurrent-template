package br.gft.template.demo.mapper.json;


import br.gft.template.demo.mapper.MapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class JsonMapperAutoConfig {

    @Bean
    @Primary
    public MapperService mapperService(
            ObjectMapper mapper
    ) {
        return new JsonMapperService(mapper);
    }

}
