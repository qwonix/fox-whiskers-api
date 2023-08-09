package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import ru.qwonix.foxwhiskersapi.security.NoPasswordAuthentication;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client implements NoPasswordAuthentication {
    private UUID id;
    private String phoneNumber;
    private UserStatus status;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
    public Client(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.CLIENT.getAuthorities();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return status.equals(UserStatus.ACTIVE);
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return status.equals(UserStatus.ACTIVE);
    }
}