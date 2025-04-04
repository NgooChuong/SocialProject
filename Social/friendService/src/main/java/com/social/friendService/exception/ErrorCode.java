package com.social.friendService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    THREAD_ERROR (1011, "Thread error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FIND_SERVER (1014, "Not find server", HttpStatus.NOT_FOUND),
    NOT_ADD_FRIEND_YOURSELF (1015, "Cannot add yourself as a friend", HttpStatus.BAD_REQUEST),
    ALREADY_FRIEND(1016, "Already friend", HttpStatus.BAD_REQUEST),
    ADD_FRIEND_ERROR(1017, "Add friend error", HttpStatus.BAD_REQUEST),
    NOT_FIND_FRIEND(1018, "Not find friend", HttpStatus.NOT_FOUND),
    NOT_FRIEND_RELATIONSHIP(1019, "Not friend ship", HttpStatus.BAD_REQUEST),
    REMOVE_FRIEND_ERROR(1020, "Remove friend error", HttpStatus.BAD_REQUEST),
    ALREADY_SEND(1021, "Already send request", HttpStatus.BAD_REQUEST),
    ACCEPT_FRIEND_ERROR(1022, "Accept friend error", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_STATUS_NOT_VALID(1023, "Friendship status not valid", HttpStatus.BAD_REQUEST),
    ALREADY_REJECT(1024, "Already reject", HttpStatus.BAD_REQUEST),
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
