package src.utils.permissions;

public enum Roles {
    USER(1),
    ADMIN(1 << 1),
    INSTRUCTOR(1 << 2),
    STUDENT(1 << 3),
    REVIEWER(1 << 4),
    STAFF(1 << 5);

    private final int bit;

    Roles(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}