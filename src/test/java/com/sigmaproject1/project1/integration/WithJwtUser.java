package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithJwtUserSecurityContextFactory.class)
public @interface WithJwtUser {
    long userId() default 1L;
    Role role() default Role.ADMIN;
}
