package com.example.beautysaas.dto.security;

import lombok.Data;

@Data
public class DeviceInfoRequest {
    private String deviceFingerprint;
    private String deviceType;
    private String operatingSystem;
    private String browser;
    private String geolocation;
    private String ipAddress;
    private Boolean trustDevice;
}
