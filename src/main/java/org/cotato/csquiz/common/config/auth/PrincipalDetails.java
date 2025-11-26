package org.cotato.csquiz.common.config.auth;

import java.util.Collection;
import java.util.List;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class PrincipalDetails implements UserDetails {

	private static final String BASE_ROLE = "ROLE_BASE";

	private Member member;

	public PrincipalDetails(Member member) {
		this.member = member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		MemberRole role = member.getRole();
		if (role == null) {
			return List.of(new SimpleGrantedAuthority(BASE_ROLE));
		}
		return List.of(new SimpleGrantedAuthority(role.getKey()));
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
