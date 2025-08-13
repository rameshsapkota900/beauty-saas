package com.example.beautysaas.service;

import com.example.beautysaas.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentParlourResolver {

    /**
     * Get the current parlour ID from the security context
     * @return The UUID of the current parlour, or null if not found
     */
    public UUID getCurrentParlourId() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getParlourId();
        }

        return null;
    }

    /**
     * Check if the current user has access to the specified parlour
     * @param parlourId The parlour ID to check
     * @return true if the user has access to the parlour
     */
    public boolean hasAccessToParlour(UUID parlourId) {
        UUID currentParlourId = getCurrentParlourId();
        return currentParlourId != null && currentParlourId.equals(parlourId);
    }
}
