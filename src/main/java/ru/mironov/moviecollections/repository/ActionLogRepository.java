package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mironov.moviecollections.entity.ActionLog;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}