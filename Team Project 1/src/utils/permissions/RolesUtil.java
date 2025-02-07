package src.utils.permissions;

import java.util.ArrayList;
import java.util.List;

public class RolesUtil {

    /**
     * Takes a decimal string representing the combined bitwise permissions
     * and converts it to an array/list of Roles.
     * Example: "3" => binary 0b11 => USER and ADMIN bits are set.
     *
     * @param rolesInt decimal string representing bitwise roles
     * @return an array of Roles that are set in the given string
     */
    public static Roles[] parseRoles(int rolesInt) {
        if (rolesInt == 0 || rolesInt < 0) {
            return new Roles[0];
        }

        int permissions;
        try {
            permissions = rolesInt;
        } catch (NumberFormatException e) {
            // If the input cannot be parsed as an int, treat as no roles
            return new Roles[0];
        }

        List<Roles> result = new ArrayList<>();
        for (Roles r : Roles.values()) {
            if ((permissions & r.getBit()) == r.getBit()) {
                result.add(r);
            }
        }
        return result.toArray(new Roles[0]);
    }

    /**
     * Takes an array of Roles and converts them to a decimal string
     * representing the combined bitwise roles.
     *
     * @param roles array of Roles
     * @return decimal string of the combined bit value
     */
    public static int rolesToInt(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return 0;
        }

        int permissions = 0;
        for (Roles r : roles) {
            permissions |= r.getBit();
        }
        return permissions;
    }

    /**
     * Checks if the given Roles array contains the specified role.
     * @param roles array of Roles user has
     * @param requiredRole role to check for
     */
    public static boolean hasRole(Roles[] roles, Roles requiredRole) {
        for (Roles r : roles) {
            if (r == requiredRole) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided role is the required role.
     * @param role user's role
     * @param requiredRole role to check for
     */
    public static boolean hasRole(Roles role, Roles requiredRole) {
        return role == requiredRole;
    }

    /**
     * Checks if the given Roles array contains all the specified roles.
     * @param roles array of Roles user has
     * @param requiredRoles array of Roles to check for
     */
    public static boolean hasAllRoles(Roles[] roles, Roles[] requiredRoles) {
        for (Roles r : requiredRoles) {
            if (!hasRole(roles, r)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given Roles array contains any of the specified roles.
     * @param roles array of Roles user has
     * @param requiredRoles array of Roles to check for
     */
    public static boolean hasAnyRole(Roles[] roles, Roles[] requiredRoles) {
        for (Roles r : requiredRoles) {
            if (hasRole(roles, r)) {
                return true;
            }
        }
        return false;
    }

    public static String roleName(Roles role) {
        String fullCapsRole = role.toString();
        String firstLetter = fullCapsRole.substring(0, 1);
        String restOfRole = fullCapsRole.substring(1).toLowerCase();

        return firstLetter + restOfRole;
    }
}