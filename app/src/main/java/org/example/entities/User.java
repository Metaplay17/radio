package org.example.entities;

import java.util.List;

import org.example.entities.dto.UserDto;
import org.example.security.Privileges;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "privilege_level", nullable = false)
    private int privilegeLevel;

    public UserDto toDto() {
        return new UserDto(id, username, email, privilegeLevel);
    }

    @OneToMany(mappedBy = "creator")
    private List<Playlist> playlists;

    public Privileges getStringPrivilegeLevel() {
        if (privilegeLevel == 1) {
            return Privileges.ROLE_CONTENT_MANAGER;
        }
        else if (privilegeLevel == 2) {
            return Privileges.ROLE_LICENCE_MANAGER;
        }
        else if (privilegeLevel == 3) {
            return Privileges.ROLE_REDACTOR;
        }
        else if (privilegeLevel == 4) {
            return Privileges.ROLE_ADMIN;
        }
        else if (privilegeLevel == 5) {
            return Privileges.ROLE_SYSTEM;
        }
        throw new IllegalArgumentException("Некорректный уровень привилегий у пользователя ID = " + id + " PrivilegeLevel = " + privilegeLevel);
    }
}
