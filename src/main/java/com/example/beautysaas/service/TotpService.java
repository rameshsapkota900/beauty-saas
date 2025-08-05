package com.example.beautysaas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
@Slf4j
public class TotpService {
    
    @Value("${security.totp.time-step-seconds:30}")
    private int timeStepSeconds;
    
    @Value("${security.totp.code-digits:6}")
    private int codeDigits;
    
    /**
     * Verify a TOTP code
     */
    public boolean verifyCode(String secret, String code) {
        try {
            long currentTime = Instant.now().getEpochSecond();
            long timeStep = currentTime / timeStepSeconds;
            
            // Check current time window and previous time window
            return generateCode(secret, timeStep).equals(code) ||
                   generateCode(secret, timeStep - 1).equals(code);
                   
        } catch (Exception e) {
            log.error("Error verifying TOTP code", e);
            return false;
        }
    }
    
    /**
     * Generate a TOTP code for a given secret and time step
     */
    private String generateCode(String secret, long timeStep) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        
        byte[] secretBytes = Base64.getUrlDecoder().decode(secret);
        byte[] timeBytes = ByteBuffer.allocate(8).putLong(timeStep).array();
        
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(secretBytes, "HmacSHA1"));
        
        byte[] hash = mac.doFinal(timeBytes);
        int offset = hash[hash.length - 1] & 0xF;
        
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= Math.pow(10, codeDigits);
        
        return String.format("%0" + codeDigits + "d", truncatedHash);
    }
}
