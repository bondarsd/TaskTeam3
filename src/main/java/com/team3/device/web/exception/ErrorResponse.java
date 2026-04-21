package com.team3.device.web.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        LocalDateTime timestamp
) {}
