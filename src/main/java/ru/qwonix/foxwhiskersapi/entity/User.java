package ru.qwonix.foxwhiskersapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "`user`")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_details_id")
    private ClientDetails clientDetails;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.equals(UserStatus.ACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(UserStatus.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.equals(UserStatus.ACTIVE);
    }

    @Override
    public boolean isEnabled() {
        return status.equals(UserStatus.ACTIVE);
    }
}