package com.example.simple.repo;

import com.example.simple.model.HealthEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface HealthEntryRepository extends JpaRepository<HealthEntry, Long> {
  Optional<HealthEntry> findTopByOrderByCreatedAtDesc();
  List<HealthEntry> findTop10ByOrderByCreatedAtDesc();
}
