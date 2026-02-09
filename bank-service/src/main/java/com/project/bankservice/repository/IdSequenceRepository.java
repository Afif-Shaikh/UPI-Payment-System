package com.project.bank_service.repository;

import com.project.bank_service.entity.IdSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM IdSequence s WHERE s.sequenceName = :name")
    Optional<IdSequence> findBySequenceNameForUpdate(@Param("name") String name);
}