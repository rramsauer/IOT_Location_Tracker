package com.rramsauer.iotlocatiotrackerapp.environment;

/**
 * This class implements Permission Object.
 * Which serves as a container for an Permission.
 *
 * @author Ramsauer René
 * @version V1.9
 */
public class AppPermission {
    private String permission;
    private int category;
    private boolean state;

    /* Constructor */
    public AppPermission(String permission, @AppPermissionsManager.PermissionTyp int category) {
        this.permission = permission;
        this.category = category;
    }

    /* GETTER */
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getCategory() {
        return category;
    }

    /* SETTER */
    public void setCategory(@AppPermissionsManager.PermissionTyp int category) {
        this.category = category;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
