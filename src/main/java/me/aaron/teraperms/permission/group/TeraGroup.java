package me.aaron.teraperms.permission.group;

import java.util.ArrayList;
import java.util.List;

public class TeraGroup {

    private String name;
    private String prefix;
    private int weight;
    private boolean standard;
    private List<String> permissions;
    private List<String> inheritance;

    public TeraGroup(String name, String prefix, int weight, boolean standard, List<String> permissions, List<String> inheritance) {
        this.name = name;
        this.prefix = prefix;
        this.weight = weight;
        this.standard = standard;
        this.permissions = permissions;
        this.inheritance = inheritance;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public List<String> getPermission() {
        return permissions;
    }

    public void setPermission(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    public List<String> getInheritance() {
        return inheritance;
    }

    public void setInheritance(List<String> inheritance) {
        this.inheritance = inheritance;
    }

    public void addInheritance(String group) {
        if (!inheritance.contains(group)) {
            inheritance.add(group);
        }
    }

    public void removeInheritance(String group) {
        inheritance.remove(group);
    }
}
