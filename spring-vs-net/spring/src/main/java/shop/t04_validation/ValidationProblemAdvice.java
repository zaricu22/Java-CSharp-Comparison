// VERDICT | T04 Validation & ProblemDetails | BETTER: ASP.NET
// WHY: AddValidation() + AddProblemDetails() return RFC 9457 errors per field out of the box; Spring returns ProblemDetail too but needs an advice to list field errors.

package shop.t04_validation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Spring already answers with an RFC 9457 ProblemDetail (spring.mvc.problemdetails.enabled),
 * but its body only says "Invalid request content." - the per-field errors have to be
 * added by hand, which is what this advice does.
 */
@RestControllerAdvice
public class ValidationProblemAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField, TreeMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        var problem = ex.getBody();
        problem.setTitle("One or more validation errors occurred.");
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }
}
