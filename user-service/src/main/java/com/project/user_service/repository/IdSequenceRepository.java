package com.project.user_service.repository;

import com.project.user_service.entity.IdSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM IdSequence s WHERE s.sequenceName = :name")
    Optional<IdSequence> findBySequenceNameForUpdate(@Param("name") String name);

    @Modifying
    @Query("UPDATE IdSequence s SET s.currentValue = s.currentValue + 1 WHERE s.sequenceName = :name")
    void incrementSequence(@Param("name") String name);
}