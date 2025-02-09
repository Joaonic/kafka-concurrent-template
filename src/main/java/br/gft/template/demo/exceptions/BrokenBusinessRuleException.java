package br.gft.template.demo.exceptions;

public class BrokenBusinessRuleException extends BaseException {

    public static final String KEY = "exceptions.rest.broken_business_rule";

    public BrokenBusinessRuleException() {
        super(KEY);
    }

    public BrokenBusinessRuleException(String message) {
        super(KEY, message);
    }

    public BrokenBusinessRuleException(Throwable cause) {
        super(KEY, cause);
    }

    public BrokenBusinessRuleException(String message, Throwable cause) {
        super(KEY, message, cause);
    }

    public BrokenBusinessRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(KEY, message, cause, enableSuppression, writableStackTrace);
    }

}
