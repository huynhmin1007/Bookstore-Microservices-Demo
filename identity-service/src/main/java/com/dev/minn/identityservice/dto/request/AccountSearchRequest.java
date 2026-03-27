package com.dev.minn.identityservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AccountSearchRequest {

    String keyword;
    String role;

    @Pattern(regexp = "(?i)^(ACTIVE|INACTIVE|BANNED)$", message = "Status invalid")
    String status;

    @Min(value = 1, message = "Page number must be at least 1")
    int page = 1;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    int size = 10;

    @Pattern(regexp = "(?i)^(createdAt|email|id)$", message = "Sort field must be 'createdAt', 'email' or 'id'")
    String sortBy = "createdAt";

    @Pattern(regexp = "(?i)^(asc|desc)$", message = "Order must be 'asc' or 'desc'")
    String order = "desc";

    public Pageable toPageable() {
        Sort.Direction direction = Sort.Direction.fromString(order.toUpperCase());
        return PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
    }
}
