package com.xiao.users.constants;

public final class UserConstants {

    private UserConstants() {
        // restrict instantiation
    }

    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "User created successfully";

    public static class ActionType {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";

    }

}