package com.example.beautysaas.security;

public final class Permissions {
    // General permissions
    public static final String VIEW = "VIEW";
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String ADMIN = "ADMIN";
    
    // Specific resource permissions
    public static final String BOOK_SERVICE = "BOOK_SERVICE";
    public static final String BOOK_COURSE = "BOOK_COURSE";
    public static final String MANAGE_STAFF = "MANAGE_STAFF";
    public static final String MANAGE_PRODUCTS = "MANAGE_PRODUCTS";
    public static final String VIEW_ANALYTICS = "VIEW_ANALYTICS";
    public static final String EXPORT_DATA = "EXPORT_DATA";
    
    // Security permissions
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String VIEW_AUDIT_LOGS = "VIEW_AUDIT_LOGS";
    public static final String MANAGE_PERMISSIONS = "MANAGE_PERMISSIONS";
    
    // Resource types
    public static final String RESOURCE_SERVICE = "SERVICE";
    public static final String RESOURCE_COURSE = "COURSE";
    public static final String RESOURCE_PRODUCT = "PRODUCT";
    public static final String RESOURCE_STAFF = "STAFF";
    public static final String RESOURCE_PARLOUR = "PARLOUR";
    public static final String RESOURCE_USER = "USER";
    
    private Permissions() {
        // Private constructor to prevent instantiation
    }
}
