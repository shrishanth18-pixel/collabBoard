package Workspace;

import db.enums;

public final class AuthorizationUtils {
    private AuthorizationUtils() {}

    public static boolean isOwner(String role) {
        return enums.Role.OWNER.name().equals(role);
    }

    public static boolean canEditBoard(String role) {
        return enums.Role.OWNER.name().equals(role) || enums.Role.EDITOR.name().equals(role);
    }

    public static boolean canViewBoard(String role) {
        return role != null;
    }
}