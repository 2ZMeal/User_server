package com.ezmeal.userservice.presentation.user;

import com.ezmeal.common.response.CommonApiResponse;
import com.ezmeal.userservice.application.user.service.UserReadService;
import com.ezmeal.userservice.presentation.user.payload.UserDetailsResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserReadService userReadService;

    @GetMapping("/{userId}")
    public ResponseEntity<CommonApiResponse<UserDetailsResponse>> getUserDetails(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(CommonApiResponse.success(
            UserDetailsResponse.of(userReadService.getUser(UUID.fromString(userId)))
        ));
    }
}
