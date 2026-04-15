package com.uir.lostfound.utils;

import androidx.core.content.ContextCompat;

import android.content.Context;

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

    // Allowed LostItem status values
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLAIMED = "CLAIMED";
    public static final String STATUS_RETURNED = "RETURNED";

    private StatusUtils() {
    }

    /**
     * Checks whether a given status is valid.
     *
     * @param status input status
     * @return true if status is OPEN, CLAIMED, or RETURNED
     */
    public static boolean isValidStatus(String status) {
        return STATUS_OPEN.equals(status)
                || STATUS_CLAIMED.equals(status)
                || STATUS_RETURNED.equals(status);
    }

    /**
     * Returns a safe status value.
     * If the input is null or invalid, OPEN is used as fallback.
     *
     * @param status input status
     * @return normalized valid status
     */
    public static String normalizeStatus(String status) {
        if (isValidStatus(status)) {
            return status;
        }
        return STATUS_OPEN;
    }

    /**
     * Returns the display text shown in the UI chip.
     *
     * @param status item status
     * @return formatted user-facing label
     */
    public static String getDisplayLabel(String status) {
        String normalizedStatus = normalizeStatus(status);

        switch (normalizedStatus) {
            case STATUS_OPEN:
                return "OPEN";
            case STATUS_CLAIMED:
                return "CLAIMED";
            case STATUS_RETURNED:
                return "RETURNED";
            default:
                return "OPEN";
        }
    }

    /**
     * Returns the background color resource for the chip.
     *
     * @param status item status
     * @return color resource id
     */
    public static int getChipColorRes(String status) {
        String normalizedStatus = normalizeStatus(status);

        switch (normalizedStatus) {
            case STATUS_OPEN:
                return R.color.status_open;
            case STATUS_CLAIMED:
                return R.color.status_claimed;
            case STATUS_RETURNED:
                return R.color.status_returned;
            default:
                return R.color.status_unknown;
        }
    }

    /**
     * Returns the text color resource for the chip text.
     * @param status item status
     * @return color resource id
     */
    public static int getChipTextColorRes(String status) {
        return android.R.color.white;
    }

    /**
     * Convenience helper that resolves chip background color to a real color int.
     *
     * @param context Android context
     * @param status item status
     * @return resolved color int
     */
    public static int getChipBackgroundColor(Context context, String status) {
        return ContextCompat.getColor(context, getChipColorRes(status));
    }

    /**
     * Convenience helper that resolves chip text color to a real color int.
     *
     * @param context Android context
     * @param status item status
     * @return resolved color int
     */
    public static int getChipTextColor(Context context, String status) {
        return ContextCompat.getColor(context, getChipTextColorRes(status));
    }


    public static int getChipColor(String status) {
        if (status == null) return 0xFF757575;
        switch (status) {
            case "OPEN":     return 0xFF43A047; // vert
            case "CLAIMED":  return 0xFFFB8C00; // orange
            case "RETURNED": return 0xFF757575; // gris
            default:         return 0xFF757575;
        }
    }
}