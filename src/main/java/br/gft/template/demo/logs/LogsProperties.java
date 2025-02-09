package br.gft.template.demo.logs;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Logs properties.
 */
@ConfigurationProperties(prefix = "gft.logs")
@Data
public class LogsProperties {


    /**
     * Application properties.
     */
    private Application application = new Application();

    /**
     * Path to the log files.
     */
    private String path = "./logs";

    /**
     * Log levels configuration.
     */
    private Level level = new Level();

    /**
     * Log levels properties.
     */
    @Data
    public static class Level {

        /**
         * Log level for the application.
         */
        private String application = "TRACE";

        /**
         * Log level for the root.
         */
        private String root = "INFO";
    }

    /**
     * Application configuration properties.
     */
    @Data
    public static class Application {

        /**
         * Name of the application.
         */
        private String name = "log";
    }
}