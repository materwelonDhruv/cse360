package tests;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RolesUtil} class.
 * <p>
 * This test class verifies the correct functionality of various role management operations
 * including role conversion, addition, removal, and checking for roles.
 * </p>
 *
 * @author Dhruv
 * @see RolesUtil
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolesUtilTest {

    @Test
    @Order(1)
    public void testIntToRoles_Zero() {
        Roles[] roles = RolesUtil.intToRoles(0);
        assertEquals(0, roles.length, "Empty string should return no roles");
    }

    @Test
    @Order(2)
    public void testIntToRoles_NegativeNum() {
        Roles[] roles = RolesUtil.intToRoles(-1);
        assertEquals(0, roles.length, "Null string should return no roles");
    }

    @Test
    @Order(3)
    public void testIntToRoles_SingleRole_Admin() {
        // Admin is 1 << 1 => decimal 2
        Roles[] roles = RolesUtil.intToRoles(2);
        assertEquals(1, roles.length);
        assertEquals(Roles.ADMIN, roles[0]);
    }

    @Test
    @Order(4)
    public void testIntToRoles_AllRoles() {
        // Combine all bits for [USER(1), ADMIN(2), INSTRUCTOR(4), STUDENT(8), REVIEWER(16), STAFF(32)]
        // Sum => 1+2+4+8+16+32 = 63
        Roles[] roles = RolesUtil.intToRoles(63);
        assertEquals(6, roles.length, "All roles should be present");
    }

    @Test
    @Order(5)
    public void testRolesToString_NoRoles() {
        int result = RolesUtil.rolesToInt(new Roles[0]);
        assertEquals(0, result, "No roles should produce '0'");
    }

    @Test
    @Order(6)
    public void testRolesToString_Null() {
        int result = RolesUtil.rolesToInt(null);
        assertEquals(0, result, "Null array should produce '0'");
    }

    @Test
    @Order(7)
    public void testRolesToString_UserInstructor() {
        // user => 1, instructor => 4 => combined => 5
        Roles[] roles = {Roles.USER, Roles.INSTRUCTOR};
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(5, result, "Should produce 5 for USER+INSTRUCTOR");
    }

    @Test
    @Order(8)
    public void testRolesToString_ReviewerStaff() {
        // reviewer => 16, staff => 32 => combined => 48
        Roles[] roles = {Roles.REVIEWER, Roles.STAFF};
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(48, result, "Should produce 48 for REVIEWER+STAFF");
    }

    @Test
    @Order(9)
    public void testRolesToString_AllRoles() {
        // All roles => 1+2+4+8+16+32 = 63
        Roles[] roles = Roles.values();
        int result = RolesUtil.rolesToInt(roles);
        assertEquals(63, result, "All roles combined is decimal 63");
    }

    @Test
    @Order(10)
    public void testHasRole_RoleFound() {
        Roles[] roles = {Roles.USER, Roles.ADMIN};
        assertTrue(RolesUtil.hasRole(roles, Roles.ADMIN), "Should return true when the role is present");
    }

    @Test
    @Order(11)
    public void testHasRole_RoleNotFound() {
        Roles[] roles = {Roles.USER, Roles.INSTRUCTOR};
        assertFalse(RolesUtil.hasRole(roles, Roles.STAFF), "Should return false when the role is not present");
    }

    @Test
    @Order(12)
    public void testHasRole_SingleRole() {
        Roles role = Roles.STUDENT;
        assertTrue(RolesUtil.hasRole(role, Roles.STUDENT), "Should return true when the role is present");
    }

    @Test
    @Order(13)
    public void testHasAllRoles_AllPresent() {
        Roles[] roles = {Roles.USER, Roles.ADMIN, Roles.INSTRUCTOR};
        Roles[] required = {Roles.USER, Roles.ADMIN};
        assertTrue(RolesUtil.hasAllRoles(roles, required), "Should return true when all required roles are present");
    }

    @Test
    @Order(14)
    public void testHasAllRoles_NotAllPresent() {
        Roles[] roles = {Roles.USER, Roles.STUDENT};
        Roles[] required = {Roles.USER, Roles.ADMIN};
        assertFalse(RolesUtil.hasAllRoles(roles, required), "Should return false if any required role is missing");
    }

    @Test
    @Order(15)
    public void testHasAllRoles_EmptyRequired() {
        Roles[] roles = {Roles.ADMIN, Roles.STAFF};
        Roles[] required = new Roles[0];
        assertTrue(RolesUtil.hasAllRoles(roles, required), "Empty required roles should return true");
    }

    @Test
    @Order(16)
    public void testHasAnyRole_OnePresent() {
        Roles[] roles = {Roles.USER, Roles.REVIEWER};
        Roles[] required = {Roles.ADMIN, Roles.REVIEWER};
        assertTrue(RolesUtil.hasAnyRole(roles, required), "Should return true when at least one required role is present");
    }

    @Test
    @Order(17)
    public void testHasAnyRole_NonePresent() {
        Roles[] roles = {Roles.USER, Roles.INSTRUCTOR};
        Roles[] required = {Roles.ADMIN, Roles.STUDENT};
        assertFalse(RolesUtil.hasAnyRole(roles, required), "Should return false when none of the required roles are present");
    }

    @Test
    @Order(18)
    public void testHasAnyRole_EmptyRequired() {
        Roles[] roles = {Roles.ADMIN, Roles.STAFF};
        Roles[] required = new Roles[0];
        assertFalse(RolesUtil.hasAnyRole(roles, required), "Empty required roles should return false");
    }

    @Test
    @Order(19)
    public void testRoleNameAsString() {
        assertEquals("User", RolesUtil.roleName(Roles.USER), "User role should be 'User'");
        assertEquals("Admin", RolesUtil.roleName(Roles.ADMIN), "Admin role should be 'Admin'");
        assertEquals("Instructor", RolesUtil.roleName(Roles.INSTRUCTOR), "Instructor role should be 'Instructor'");
        assertEquals("Student", RolesUtil.roleName(Roles.STUDENT), "Student role should be 'Student'");
        assertEquals("Reviewer", RolesUtil.roleName(Roles.REVIEWER), "Reviewer role should be 'Reviewer'");
        assertEquals("Staff", RolesUtil.roleName(Roles.STAFF), "Staff role should be 'Staff'");
    }

    @Test
    @Order(20)
    public void testAddRole_NewRole() {
        int rolesInt = 0; // No roles initially
        int result = RolesUtil.addRole(rolesInt, Roles.ADMIN);
        assertEquals(2, result, "Adding ADMIN should result in bitwise value 2");
    }

    @Test
    @Order(21)
    public void testAddRole_ExistingRole() {
        int rolesInt = 2; // Already has ADMIN
        int result = RolesUtil.addRole(rolesInt, Roles.ADMIN);
        assertEquals(2, result, "Adding ADMIN again should not change the value");
    }

    @Test
    @Order(22)
    public void testAddRole_MultipleRoles() {
        int rolesInt = RolesUtil.addRole(0, Roles.USER); // Add USER
        rolesInt = RolesUtil.addRole(rolesInt, Roles.INSTRUCTOR); // Add INSTRUCTOR
        assertEquals(5, rolesInt, "Adding USER (1) and INSTRUCTOR (4) should result in 5");
    }

    @Test
    @Order(23)
    public void testRemoveRole_ExistingRole() {
        int rolesInt = 3; // USER (1) + ADMIN (2) = 3
        int result = RolesUtil.removeRole(rolesInt, Roles.ADMIN);
        assertEquals(1, result, "Removing ADMIN should leave only USER");
    }

    @Test
    @Order(24)
    public void testRemoveRole_NonExistingRole() {
        int rolesInt = 1; // Only USER (1)
        int result = RolesUtil.removeRole(rolesInt, Roles.ADMIN);
        assertEquals(1, result, "Removing ADMIN from USER-only roles should not change value");
    }

    @Test
    @Order(25)
    public void testRemoveRole_AllRoles() {
        int rolesInt = 63; // All roles combined
        rolesInt = RolesUtil.removeRole(rolesInt, Roles.REVIEWER); // Remove REVIEWER (16)
        assertEquals(47, rolesInt, "Removing REVIEWER from all roles should result in 47");
    }
}