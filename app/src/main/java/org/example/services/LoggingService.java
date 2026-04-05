package org.example.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.entities.Log;
import org.example.entities.dto.LogDto;
import org.example.repositories.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    private final static Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final LogRepository logRepository;

    public LoggingService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void info(String message, String initiatorUsername) {
        logger.info(message);
        Log log = new Log(message, initiatorUsername, "INFO");
        logRepository.save(log);
    }

    public void error(String message, String initiatorUsername) {
        logger.error(message);
        Log log = new Log(message, initiatorUsername, "ERROR");
        logRepository.save(log);
    }

    public void debug(String message, String initiatorUsername) {
        logger.debug(message);
    }

    public List<LogDto> getLogs(LocalDateTime from, LocalDateTime to, String type, String initiatorUsername, Integer count) {
        List<Log> logs = logRepository.findByInitiatorUsernameDatetimeTypeAndCount(initiatorUsername, from, to, type, count);
        return logs.stream().map((Log l) -> l.toDto()).collect(Collectors.toList());
    }
}
