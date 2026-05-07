package com.Ulises.BlackJackAPI.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic API response wrapper.
 * Provides a standardized format for all API responses.
 *
 * @param <T> The data type of the response
 * @author Ulises Lafuente
 */
@Setter
@Getter
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

}