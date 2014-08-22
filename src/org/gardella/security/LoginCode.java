package org.gardella.security;

public enum LoginCode {
    SUCCESS,
    HEADER_AUTH,
    INACTIVE_USER,
    PENDING_USER,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    PERMISSION_DENIED;
}