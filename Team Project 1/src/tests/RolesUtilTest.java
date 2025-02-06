package src.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import src.utils.permissions.Roles;
import src.utils.permissions.RolesUtil;

public class RolesUtilTest {

    @Test
    public void testParseRoles_Zero() {
        Roles[] roles = RolesUtil.parseRoles(0);
        assertEquals(0, roles.length, "Empty string should return no roles");
    }

    @Test
    public void testParseRoles_NegativeNum() {
        Roles[] roles = RolesUtil.parseRoles(-1);
        assertEquals(0, roles.length, "Null string should return no roles");
    }

    @Test
    public void testParseRoles_SingleRole_Admin() {
        // Admin is 1 << 1 => decimal 2
        Roles[] roles = RolesUtil.parseRoles(2);
        assertEquals(1, roles.length);
        assertEquals(Roles.ADMIN, roles[0]);
    }

    @Test
    public void testParseRoles_AllRoles() {
        // Combine all bits for [USER(1), ADMIN(2), INSTRUCTOR(4), STUDENT(8), REVIEWER(16), STAFF(32)]
        // Sum => 1+2+4+8+16+32 = 63
        Roles[] roles = RolesUtil.parseRoles(63);
        assertEquals(6, roles.length, "All roles should be present");
    }

    @Test
    public void testRolesToString_NoRoles() {
        int result = RolesUtil.rolesToInt(new Roles[0]);
        assertEquals(0, result, "No roles should produce '0'");
    }

    @Test
    public void testRolesToString_Null() {
        int result = RolesUtil.rolesToInt(null);
        assertEquals(0, result, "Null array should produce '0'");
    }

    @Test
    public void testRolesToString_UserInstructor() {
        // user => 1, instructor => 4 => combined => 5
        Roles[] roles = { Roles.USER, Roles.INSTRUCTOR };
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(5, result, "Should produce 5 for USER+INSTRUCTOR");
    }

    @Test
    public void testRolesToString_ReviewerStaff() {
        // reviewer => 16, staff => 32 => combined => 48
        Roles[] roles = { Roles.REVIEWER, Roles.STAFF };
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(48, result, "Should produce 48 for REVIEWER+STAFF");
    }

    @Test
    public void testRolesToString_AllRoles() {
        // All roles => 1+2+4+8+16+32 = 63
        Roles[] roles = Roles.values();
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(63, result, "All roles combined is decimal 63");
    }

    @Test
    public void testHasRole_RoleFound() {
        Roles[] roles = { Roles.USER, Roles.ADMIN };
        assertTrue(RolesUtil.hasRole(roles, Roles.ADMIN), "Should return true when the role is present");
    }

    @Test
    public void testHasRole_RoleNotFound() {
        Roles[] roles = { Roles.USER, Roles.INSTRUCTOR };
        assertFalse(RolesUtil.hasRole(roles, Roles.STAFF), "Should return false when the role is not present");
    }

    @Test
    public void testHasAllRoles_AllPresent() {
        Roles[] roles = { Roles.USER, Roles.ADMIN, Roles.INSTRUCTOR };
        Roles[] required = { Roles.USER, Roles.ADMIN };
        assertTrue(RolesUtil.hasAllRoles(roles, required), "Should return true when all required roles are present");
    }

    @Test
    public void testHasAllRoles_NotAllPresent() {
        Roles[] roles = { Roles.USER, Roles.STUDENT };
        Roles[] required = { Roles.USER, Roles.ADMIN };
        assertFalse(RolesUtil.hasAllRoles(roles, required), "Should return false if any required role is missing");
    }

    @Test
    public void testHasAllRoles_EmptyRequired() {
        Roles[] roles = { Roles.ADMIN, Roles.STAFF };
        Roles[] required = new Roles[0];
        assertTrue(RolesUtil.hasAllRoles(roles, required), "Empty required roles should return true");
    }

    @Test
    public void testHasAnyRole_OnePresent() {
        Roles[] roles = { Roles.USER, Roles.REVIEWER };
        Roles[] required = { Roles.ADMIN, Roles.REVIEWER };
        assertTrue(RolesUtil.hasAnyRole(roles, required), "Should return true when at least one required role is present");
    }

    @Test
    public void testHasAnyRole_NonePresent() {
        Roles[] roles = { Roles.USER, Roles.INSTRUCTOR };
        Roles[] required = { Roles.ADMIN, Roles.STUDENT };
        assertFalse(RolesUtil.hasAnyRole(roles, required), "Should return false when none of the required roles are present");
    }

    @Test
    public void testHasAnyRole_EmptyRequired() {
        Roles[] roles = { Roles.ADMIN, Roles.STAFF };
        Roles[] required = new Roles[0];
        assertFalse(RolesUtil.hasAnyRole(roles, required), "Empty required roles should return false");
    }

    @Test
    public void testRoleNameAsString() {
        assertEquals("User", RolesUtil.roleName(Roles.USER), "User role should be 'User'");
        assertEquals("Admin", RolesUtil.roleName(Roles.ADMIN), "Admin role should be 'Admin'");
        assertEquals("Instructor", RolesUtil.roleName(Roles.INSTRUCTOR), "Instructor role should be 'Instructor'");
        assertEquals("Student", RolesUtil.roleName(Roles.STUDENT), "Student role should be 'Student'");
        assertEquals("Reviewer", RolesUtil.roleName(Roles.REVIEWER), "Reviewer role should be 'Reviewer'");
        assertEquals("Staff", RolesUtil.roleName(Roles.STAFF), "Staff role should be 'Staff'");
    }

}