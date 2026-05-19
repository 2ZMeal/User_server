package com.ezmeal.userservice.presentation.user.payload;

import com.ezmeal.userservice.application.user.dto.UserReadResult;
import java.util.List;
import org.springframework.data.domain.Page;

public record UserDetailsPageResponse(
    List<UserDetailsResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {

    public static UserDetailsPageResponse of(Page<UserReadResult> page) {
        return new UserDetailsPageResponse(
            page.getContent().stream().map(UserDetailsResponse::of).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
