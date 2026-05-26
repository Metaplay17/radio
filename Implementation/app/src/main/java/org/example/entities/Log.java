package org.example.entities;

import java.time.LocalDateTime;

import org.example.entities.dto.LogDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "logs")
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "initiator_username")
    private String initatorUsername = "SYSTEM";

    @Column(name = "message")
    private String message;

    @Column(name = "datetime")
    private LocalDateTime datetime = LocalDateTime.now();

    @Column(name = "type")
    private String type;

    public Log(String message, String initiatorUsername, String type) {
        if (initiatorUsername != null) {
            this.initatorUsername = initiatorUsername;
        }
        this.message = message;
        this.type = type;
    }

    public LogDto toDto() {
        return new LogDto(message, datetime, initatorUsername);
    }
}
