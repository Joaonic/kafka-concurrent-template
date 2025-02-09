package br.gft.template.demo.utils;

public final class DurationFormatUtils {

    private DurationFormatUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Formats a duration in milliseconds to a specified format.
     * <p>
     * Supported format tokens:
     * - HH: Hours (02 digits)
     * - mm: Minutes (02 digits)
     * - ss: Seconds (02 digits)
     * - SSS: Milliseconds (03 digits)
     *
     * @param durationInMillis Duration in milliseconds.
     * @param format           The desired format (e.g., "HH:mm:ss.SSS").
     * @return The formatted duration as a string.
     */
    public static String formatDuration(long durationInMillis, String format) {
        long hours = durationInMillis / (1000 * 60 * 60);
        long remainingMillis = durationInMillis % (1000 * 60 * 60);

        long minutes = remainingMillis / (1000 * 60);
        remainingMillis %= (1000 * 60);

        long seconds = remainingMillis / 1000;
        long milliseconds = remainingMillis % 1000;

        // Replace format tokens with actual values
        format = format.replace("HH", String.format("%02d", hours));
        format = format.replace("mm", String.format("%02d", minutes));
        format = format.replace("ss", String.format("%02d", seconds));
        format = format.replace("SSS", String.format("%03d", milliseconds));

        return format;
    }
}
