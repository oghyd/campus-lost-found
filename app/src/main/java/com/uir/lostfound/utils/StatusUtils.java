package com.uir.lostfound.utils;

import com.uir.lostfound.R;

/**
 * StatusUtils centralizes the status values used across the app.
 *
 * Why this file matters:
 * - avoids hardcoding "OPEN", "CLAIMED", "RETURNED" everywhere
 * - keeps status naming consistent
 * - gives one place to map each status to a color
 *
 * Ownership : Omar
 */
public class StatusUtils {

    // Status constants used for LostItem.status
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLAIMED = "CLAIMED";
    public static final String STATUS_RETURNED = "RETURNED";

    /**
     * Returns the color resource associated with a given item status.
     *
     * OPEN      -> green
     * CLAIMED   -> amber/orange
     * RETURNED  -> gray
     *
     * @param status current item status
     * @return color resource ID
     */
    public static int getChipColorRes(String status) {
        if (STATUS_OPEN.equals(status)) {
            return R.color.status_open;
        } else if (STATUS_CLAIMED.equals(status)) {
            return R.color.status_claimed;
        } else if (STATUS_RETURNED.equals(status)) {
            return R.color.status_returned;
        } else {
            // Fallback color if status is null or unexpected
            return R.color.status_unknown;
        }
    }

    /**
     * Optional helper to validate whether a status is one of the allowed values.
     *
     * @param status input status
     * @return true if valid, false otherwise
     */
    public static boolean isValidStatus(String status) {
        return STATUS_OPEN.equals(status)
                || STATUS_CLAIMED.equals(status)
                || STATUS_RETURNED.equals(status);
    }

    /**
     * Optional fallback helper.
     * If a status is null or invalid, return OPEN as safe default.
     *
     * @param status input status
     * @return valid status string
     */
    public static String normalizeStatus(String status) {
        if (isValidStatus(status)) {
            return status;
        }
        return STATUS_OPEN;
    }

    // Private constructor to prevent instantiation
    private StatusUtils() {
    }
}