package src.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

public class RolesUtilTest {

    @Test
    public void testParseRoles_EmptyString() {
        Roles[] roles = RolesUtil.parseRoles("");
        assertEquals(0, roles.length, "Empty string should return no roles");
    }

    @Test
    public void testParseRoles_NullString() {
        Roles[] roles = RolesUtil.parseRoles(null);
        assertEquals(0, roles.length, "Null string should return no roles");
    }

    @Test
    public void testParseRoles_NonNumeric() {
        Roles[] roles = RolesUtil.parseRoles("abc");
        assertEquals(0, roles.length, "Non-numeric string should return no roles");
    }

    @Test
    public void testParseRoles_SingleRole_Admin() {
        // Admin is 1 << 1 => decimal 2
        Roles[] roles = RolesUtil.parseRoles("2");
        assertEquals(1, roles.length);
        assertEquals(Roles.ADMIN, roles[0]);
    }

    @Test
    public void testParseRoles_MultipleRoles() {
        // Suppose "3" => binary 0011 => USER(1<<0) + ADMIN(1<<1)
        Roles[] roles = RolesUtil.parseRoles("3");
        assertTrue(containsRole(roles, Roles.USER), "Should contain USER");
        assertTrue(containsRole(roles, Roles.ADMIN), "Should contain ADMIN");
        assertEquals(2, roles.length, "Expected exactly two roles");
    }

    @Test
    public void testParseRoles_AllRoles() {
        // Combine all bits for [USER(1), ADMIN(2), INSTRUCTOR(4), STUDENT(8), REVIEWER(16), STAFF(32)]
        // Sum => 1+2+4+8+16+32 = 63
        Roles[] roles = RolesUtil.parseRoles("63");
        assertEquals(6, roles.length, "All roles should be present");
    }

    @Test
    public void testRolesToString_NoRoles() {
        String result = RolesUtil.rolesToString(new Roles[0]);
        assertEquals("0", result, "No roles should produce '0'");
    }

    @Test
    public void testRolesToString_Null() {
        String result = RolesUtil.rolesToString(null);
        assertEquals("0", result, "Null array should produce '0'");
    }

    @Test
    public void testRolesToString_UserInstructor() {
        // user => 1, instructor => 4 => combined => 5
        Roles[] roles = { Roles.USER, Roles.INSTRUCTOR };
        String result = RolesUtil.rolesToString(roles);
        assertEquals("5", result, "Should produce 5 for USER+INSTRUCTOR");
    }

    @Test
    public void testRolesToString_AllRoles() {
        // All roles => 1+2+4+8+16+32 = 63
        Roles[] roles = Roles.values();
        String result = RolesUtil.rolesToString(roles);
        assertEquals("63", result, "All roles combined is decimal 63");
    }

    private boolean containsRole(Roles[] arr, Roles role) {
        for (Roles r : arr) {
            if (r == role) return true;
        }
        return false;
    }
}