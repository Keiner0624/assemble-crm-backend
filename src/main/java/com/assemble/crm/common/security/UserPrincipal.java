package com.assemble.crm.common.security;

import com.assemble.crm.role.entity.Permission;
import com.assemble.crm.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security principal. Carries the user id and company id so the
 * service layer can enforce tenant isolation. Authorities include both the
 * role (ROLE_*) and granular permissions.
 */
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final Long companyId;
    private final String email;
    private final String password;
    private final boolean active;
    private final List<GrantedAuthority> authorities;

    public UserPrincipal(Long id, Long companyId, String email, String password,
                         boolean active, List<GrantedAuthority> authorities) {
        this.id = id;
        this.companyId = companyId;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal from(User user) {
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
        for (Permission p : user.getRole().getPermissions()) {
            auths.add(new SimpleGrantedAuthority(p.name()));
        }
        return new UserPrincipal(
                user.getId(),
                user.getCompany().getId(),
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                auths
        );
    }

    public Long getId() { return id; }
    public Long getCompanyId() { return companyId; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
