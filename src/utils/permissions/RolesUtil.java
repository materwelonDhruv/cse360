package utils.permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class provides methods for managing user roles represented as bitwise integers.
 * Includes functionalities for role conversion, role checking, addition, and removal.
 *
 * @author Dhruv
 */
public class RolesUtil {

    /**
     * Takes a decimal string representing the combined bitwise permissions
     * and converts it to an array/list of Roles.
     * Example: "3" => binary 0b11 => USER and ADMIN bits are set.
     *
     * @param rolesInt decimal string representing bitwise roles
     * @return an array of Roles that are set in the given string
     */
    public static Roles[] intToRoles(int rolesInt) {
        if (rolesInt == 0 || rolesInt < 0) {
            return new Roles[0];
        }

        List<Roles> result = new ArrayList<>();
        for (Roles r : Roles.values()) {
            if ((rolesInt & r.getBit()) == r.getBit()) {
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
     *
     * @param roles        array of Roles user has
     * @param requiredRole role to check for
     */
    public static boolean hasRole(Roles[] roles, Roles requiredRole) {
        return hasRole(rolesToInt(roles), requiredRole);
    }

    /**
     * Checks if the provided role is the required role.
     *
     * @param role         user's role
     * @param requiredRole role to check for
     */
    public static boolean hasRole(Roles role, Roles requiredRole) {
        return role == requiredRole;
    }

    /**
     * Checks if the provided roles integer contains the specified role.
     *
     * @param rolesInt     integer representing the combined bitwise roles.
     * @param requiredRole role to check for.
     */
    public static boolean hasRole(int rolesInt, Roles requiredRole) {
        return (rolesInt & requiredRole.getBit()) == requiredRole.getBit();
    }

    /**
     * Checks if the given Roles array contains all the specified roles.
     *
     * @param roles         array of Roles user has
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
     *
     * @param roles         array of Roles user has
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

    /**
     * Add a role to a given integer representing the combined bitwise roles.
     *
     * @param rolesInt integer representing the combined bitwise roles
     * @param role     role to add
     * @return integer with the added role
     */
    public static int addRole(int rolesInt, Roles role) {
        if (hasRole(rolesInt, role)) {
            return rolesInt;
        }

        return rolesInt | role.getBit();
    }

    /**
     * Remove a role from a given integer representing the combined bitwise roles.
     *
     * @param rolesInt integer representing the combined bitwise roles
     * @param role     role to remove
     * @return integer with the removed role
     */
    public static int removeRole(int rolesInt, Roles role) {
        if (!hasRole(rolesInt, role)) {
            return rolesInt;
        }

        return rolesInt & ~role.getBit();
    }
}