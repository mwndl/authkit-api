package com.authkit.backend.domain.enums;

public enum NotificationCode {
    SESSION_CREATED("notification.session.created"),
    SESSION_REVOKED("notification.session.revoked"),
    ACCOUNT_VERIFIED("notification.account.verified"),
    PASSWORD_CHANGED("notification.password.changed"),
    TWO_FACTOR_ENABLED("notification.2fa.enabled"),
    TWO_FACTOR_DISABLED("notification.2fa.disabled"),
    TEST_NOTIFICATION("notification.test");

    private final String code;

    NotificationCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static NotificationCode fromCode(String code) {
        for (NotificationCode notificationCode : values()) {
            if (notificationCode.getCode().equals(code)) {
                return notificationCode;
            }
        }
        throw new IllegalArgumentException("Invalid notification code: " + code);
    }
} 