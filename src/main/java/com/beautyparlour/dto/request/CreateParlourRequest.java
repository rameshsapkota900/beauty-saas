package com.beautyparlour.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateParlourRequest {
    @NotBlank(message = "Parlour name is required")
    private String parlourName;

    private String logoUrl;

    private String contactInfo;

    @NotBlank(message = "Admin name is required")
    private String adminName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Admin email is required")
    private String adminEmail;

    @NotBlank(message = "Admin password is required")
    private String adminPassword;

    // Constructors
    public CreateParlourRequest() {}

    // Getters and Setters
    public String getParlourName() { return parlourName; }
    public void setParlourName(String parlourName) { this.parlourName = parlourName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
}
