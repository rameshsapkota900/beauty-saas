package com.beautyparlour.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations in the Beauty Parlour SaaS application.
 * Provides common date formatting, calculation, and validation methods.
 */
public final class DateTimeUtils {
    
    // Common date time formatters
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    private DateTimeUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Formats a LocalDateTime to the default format.
     * @param dateTime the date time to format
     * @return formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_DATE_TIME_FORMAT) : "";
    }
    
    /**
     * Formats a LocalDateTime for display purposes.
     * @param dateTime the date time to format
     * @return formatted date time string for display
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMAT) : "";
    }
    
    /**
     * Gets the start of the current day.
     * @return LocalDateTime representing start of today
     */
    public static LocalDateTime getStartOfDay() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }
    
    /**
     * Gets the end of the current day.
     * @return LocalDateTime representing end of today
     */
    public static LocalDateTime getEndOfDay() {
        return getStartOfDay().plusDays(1).minusNanos(1);
    }
    
    /**
     * Gets the start of the current week (Monday).
     * @return LocalDateTime representing start of current week
     */
    public static LocalDateTime getStartOfWeek() {
        LocalDateTime now = LocalDateTime.now();
        return now.minusDays(now.getDayOfWeek().getValue() - 1).truncatedTo(ChronoUnit.DAYS);
    }
    
    /**
     * Gets the start of the current month.
     * @return LocalDateTime representing start of current month
     */
    public static LocalDateTime getStartOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
    }
    
    /**
     * Gets the start of the current year.
     * @return LocalDateTime representing start of current year
     */
    public static LocalDateTime getStartOfYear() {
        LocalDateTime now = LocalDateTime.now();
        return now.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
    }
    
    /**
     * Calculates the number of days between two dates.
     * @param startDate the start date
     * @param endDate the end date
     * @return number of days between the dates
     */
    public static long daysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
    }
    
    /**
     * Checks if a date is within a specific range.
     * @param dateToCheck the date to check
     * @param startDate the start of the range
     * @param endDate the end of the range
     * @return true if the date is within the range
     */
    public static boolean isWithinRange(LocalDateTime dateToCheck, LocalDateTime startDate, LocalDateTime endDate) {
        return dateToCheck != null && startDate != null && endDate != null &&
               !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }
    
    /**
     * Checks if a date is today.
     * @param dateTime the date to check
     * @return true if the date is today
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime != null && dateTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    /**
     * Gets a date that is N days ago from now.
     * @param days number of days to subtract
     * @return LocalDateTime representing N days ago
     */
    public static LocalDateTime getDaysAgo(int days) {
        return LocalDateTime.now().minusDays(days);
    }
}
