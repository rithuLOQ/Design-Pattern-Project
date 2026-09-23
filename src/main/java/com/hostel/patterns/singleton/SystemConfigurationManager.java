/**
 * Singleton pattern implementation.
 *
 * Intent: ensure a single global configuration source for the hostel system.
 * Participants: Singleton instance, private constructor, thread-safe getInstance.
 * Usage: shared system settings such as the default fee generation configuration.
 */
package com.hostel.patterns.singleton;

public class SystemConfigurationManager {

    private static volatile SystemConfigurationManager instance;

    private final String defaultFeeCurrency;
    private final String defaultRoomType;

    private SystemConfigurationManager() {
        this.defaultFeeCurrency = "INR";
        this.defaultRoomType = "DOUBLE";
    }

    public static SystemConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (SystemConfigurationManager.class) {
                if (instance == null) {
                    instance = new SystemConfigurationManager();
                }
            }
        }
        return instance;
    }

    public String getDefaultFeeCurrency() {
        return defaultFeeCurrency;
    }

    public String getDefaultRoomType() {
        return defaultRoomType;
    }
}
