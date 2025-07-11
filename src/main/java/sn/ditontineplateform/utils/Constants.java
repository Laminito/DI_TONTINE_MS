package sn.ditontineplateform.utils;

public class Constants {
    private Constants () {
        throw new IllegalStateException("Constants class");
    }

    // Status Constants
    public static class Status {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int LOCKED = 423;
        public static final int CONFLICT = 409;
        public static final int SERVICE_UNAVAILABLE = 503;
    }

    // Message Constants
    public static class Message {
        public static final String SUCCESS_BODY = "Success";
        public static final String DELETE_BODY = "Delete Success";
        public static final String CREATED_BODY = "Created Success";
        public static final String BAD_REQUEST_BODY = "Invalid input data";
        public static final String NOT_FOUND_BODY = "Not Found";
        public static final String SERVER_ERROR_BODY = "Internal server error";
        public static final String UNAUTHORIZED_BODY = "Unauthorized access";
        public static final String VALIDATION_ERROR_BODY = "Validation error";
        public static final String FORBIDDEN_BODY = "Forbidden";
        public static final String LOCKED_BODY = "Locked";
        public static final String CONFLICT_BODY = "Conflict";
        public static final String SERVICE_UNAVAILABLE_BODY = "Service Unavailable";
    }
}
