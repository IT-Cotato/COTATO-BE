package org.cotato.csquiz.common.role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cotato.csquiz.domain.auth.enums.MemberRole;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleAuthority {
	MemberRole value();
}
