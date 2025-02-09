package br.gft.template.demo.exceptions;

public class DataLossException extends BaseException {

    public static final String KEY = "exceptions.rest.data_loss";

    public DataLossException() {
        super(KEY);
    }

    public DataLossException(String message) {
        super(KEY, message);
    }

    public DataLossException(Throwable cause) {
        super(KEY, cause);
    }

    public DataLossException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public DataLossException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
