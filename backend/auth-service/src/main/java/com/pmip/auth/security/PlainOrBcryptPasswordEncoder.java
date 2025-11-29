package com.pmip.auth.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlainOrBcryptPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
    @Override public String encode(CharSequence rawPassword) { return delegate.encode(rawPassword); }
    @Override public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try { if (delegate.matches(rawPassword, encodedPassword)) return true; } catch (Exception ignored) {}
        return encodedPassword != null && encodedPassword.contentEquals(rawPassword);
    }
}
