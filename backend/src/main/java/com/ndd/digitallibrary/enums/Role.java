package com.ndd.digitallibrary.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    LIBRARIAN,
    USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
