package br.gft.template.demo.validator;

import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public final class BeanValidator {

    private BeanValidator() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanValidator.class);
    private static final String ERROR_VALIDATON = "### Bean com campos inválidos\n\t\t- Campos Inválidos={} \n\t\t- Conteúdo={} \n\t\t- para model {}!";

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> List<String> formatErrors(Set<ConstraintViolation<T>> errors) {
        return errors.stream().map(c -> String.format("{ %s : %s }", c.getPropertyPath(), c.getMessage())).toList();
    }

    public static <T> boolean validateBean(T document) {
        Set<ConstraintViolation<T>> errors = validator.validate(document);
        List<String> formattedErrors = formatErrors(errors);
        if (!errors.isEmpty()) {
            LOGGER.error(ERROR_VALIDATON, formattedErrors, document, document.getClass().getName());
            return false;
        }
        return true;
    }

    public static <T> void validateBeanAndThrow(T document) {
        Set<ConstraintViolation<T>> errors = validator.validate(document);
        List<String> formattedErrors = formatErrors(errors);
        if (!errors.isEmpty()) {
            LOGGER.error(ERROR_VALIDATON, formattedErrors, document, document.getClass().getName());
            throw new ConstraintViolationException("### Campos inválidos!", errors);
        }
    }

    public static <T> List<String> validateBeanAndReturnFormattedErrors(T document) {
        Set<ConstraintViolation<T>> errors = validator.validate(document);
        return formatErrors(errors);
    }

    public static <T> Set<ConstraintViolation<T>> validateBeanAndReturnViolations(T document) {
        return validator.validate(document);
    }
}
