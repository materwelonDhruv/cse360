package src.utils.permissions;

import java.util.ArrayList;
import java.util.List;

public class RolesUtil {

    /**
     * Takes a decimal string representing the combined bitwise permissions
     * and converts it to an array/list of Roles.
     * Example: "3" => binary 0b11 => USER and ADMIN bits are set.
     *
     * @param roleString decimal string representing bitwise roles
     * @return an array of Roles that are set in the given string
     */
    public static Roles[] parseRoles(String roleString) {
        if (roleString == null || roleString.isEmpty()) {
            return new Roles[0];
        }

        int permissions;
        try {
            permissions = Integer.parseInt(roleString);
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
    public static String rolesToString(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return "0";
        }

        int permissions = 0;
        for (Roles r : roles) {
            permissions |= r.getBit();
        }
        return String.valueOf(permissions);
    }
}