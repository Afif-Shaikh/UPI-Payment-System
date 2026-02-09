package com.project.vpa_service.repository;

import com.project.vpa_service.entity.Psp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PspRepository extends JpaRepository<Psp, String> {

    Optional<Psp> findByIdAndActiveTrue(String id);

    Optional<Psp> findByPspHandleAndActiveTrue(String pspHandle);

    List<Psp> findAllByActiveTrue();

    boolean existsByPspHandle(String pspHandle);
}