package com.uir.lostfound.utils;

/**
 * Shared status constants and helpers for the OPEN → CLAIMED → RETURNED lifecycle.
 * Owner: Omar. Consumed by ItemFeedAdapter (Idriss) and ItemDetailFragment (Mona).
 *
 * All methods are static — no instantiation needed.
 */
public final class StatusUtils {

    public static final String OPEN     = "OPEN";
    public static final String CLAIMED  = "CLAIMED";
    public static final String RETURNED = "RETURNED";

    private StatusUtils() {}

    /**
     * Returns a hex colour string suitable for a chip background.
     * Null-safe: unknown/null status falls back to a light grey.
     */
    public static String getChipColor(String status) {
        if (status == null) return "#E5E7EB";
        switch (status) {
            case OPEN:     return "#22C55E";
            case CLAIMED:  return "#F59E0B";
            case RETURNED: return "#94A3B8";
            default:       return "#E5E7EB";
        }
    }

    /**
     * Returns a human-readable label for the given status.
     */
    public static String getLabel(String status) {
        if (status == null) return "Unknown";
        switch (status) {
            case OPEN:     return "Open";
            case CLAIMED:  return "Claimed";
            case RETURNED: return "Returned";
            default:       return "Unknown";
        }
    }
}
