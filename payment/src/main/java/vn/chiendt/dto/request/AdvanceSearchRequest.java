package vn.chiendt.dto.request;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;
import vn.chiendt.common.Currency;
import vn.chiendt.common.Operation;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.common.TransactionStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdvanceSearchRequest {

    @NotNull(message = "searchFields must be not blank")
    private List<SearchField<?>> searchFields;
    private String sort;
    private int page;
    private int size;

    public void validate() {
        for (SearchField<?> field : searchFields) {
            String fieldName = field.getField();
            Operation operation = field.getOperation();

            if (!StringUtils.hasText(fieldName)) {
                throw new ValidationException("field must not be blank");
            }
            if (operation == null) {
                throw new ValidationException("operation must not be null");
            }
            if (null == field.getValue()) {
                throw new ValidationException("value must not be null");
            }

            List<String> numbers = List.of("customerId", "amount");
            if (numbers.contains(fieldName)) {
                if (!(field.getValue() instanceof Number)) {
                    throw new ValidationException(fieldName + " must be a number");
                }
            }

            List<String> strings = List.of("paymentId", "description");
            if (strings.contains(fieldName)) {
                if (!(field.getValue() instanceof String)) {
                    throw new ValidationException(fieldName + " must be a String");
                }
            }

            if (fieldName.equals("paymentMethod") || fieldName.equals("currency") || fieldName.equals("status")) {
                try {
                    PaymentMethod.valueOf(String.valueOf(field.getValue()).toUpperCase());
                    Currency.valueOf(String.valueOf(field.getValue()).toUpperCase());
                    TransactionStatus.valueOf(String.valueOf(field.getValue()).toUpperCase());
                } catch (Exception e) {
                    throw new ValidationException(fieldName + " invalid");
                }
            }

            List<String> dates = List.of("createdAt", "updatedAt");
            if (dates.contains(fieldName)) {
                String dateFormat = "dd/MM/yyyy";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                try {
                    LocalDate date = LocalDate.parse(field.getValue().toString(), formatter);
                } catch (Exception e) {
                    throw new ValidationException(fieldName + " invalid format");
                }
            }
        }
    }
}
