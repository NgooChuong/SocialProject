package com.social.postService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    ATTRIBUTE_EXISTED(1007, "Attribute existed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_PHONE (1008, "Invalid phone number", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL (1009, "Invalid email address", HttpStatus.BAD_REQUEST),
    PROFILE_CREATION_FAILED (1010, "Profile creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    THREAD_ERROR (1011, "Thread error", HttpStatus.INTERNAL_SERVER_ERROR),
    POST_NOT_EXISTED (1012, "Post not existed", HttpStatus.NOT_FOUND),
    INVALID_REACTION_TYPE (1013, "Invalid reaction type", HttpStatus.BAD_REQUEST),
    NOT_FIND_SERVER (1014, "Not find server", HttpStatus.NOT_FOUND),
    INTERACTION_NOT_FOUND (1015, "Interaction not found", HttpStatus.NOT_FOUND),
    CLOUDINARY_DELETE_FAILED(1016, "Cloudinary delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UPDATE_ERROR(1017, "Update is error", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_ERROR(1018, "Delete is error", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_CREATION_FAILED (1019, "Comment creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_NOT_EXISTED(1020, "Comment not existed", HttpStatus.NOT_FOUND),
    COMMENT_UPDATE_FAILED (1021, "Comment update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_DELETE_FAILED (1022, "Comment delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    GET_COLLECTION_NAME_FAIL (1023, "Get collection in qdrant fail", HttpStatus.INTERNAL_SERVER_ERROR),
    SEARCH_VECTOR_FAIL(1024, "Search in qdrant fail", HttpStatus.INTERNAL_SERVER_ERROR),
    GET_CACHE_ERROR(1025, "Get cache in redis fail", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
