package com.Ulises.BlackJackAPI.security;

import com.Ulises.BlackJackAPI.domain.entity.PlayerEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for player authentication.
 * Wraps PlayerEntity for Spring Security authentication.
 *
 * @author Ulises Lafuente
 */
public class PlayerUserDetails implements UserDetails {

    private final Long playerId;
    private final String username;
    private final String password;

    public PlayerUserDetails(PlayerEntity player) {
        this.playerId = player.getId();
        this.username = player.getUsername();
        this.password = player.getPassword();
    }

    public Long getPlayerId() {
        return playerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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