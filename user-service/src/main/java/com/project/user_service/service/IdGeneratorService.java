package com.project.user_service.service;

import com.project.user_service.entity.IdSequence;
import com.project.user_service.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdGeneratorService {

    private final IdSequenceRepository idSequenceRepository;

    private static final String USER_SEQUENCE = "USER_SEQ";
    private static final long USER_START_VALUE = 100000L;

    /**
     * Generate User ID like U100001, U100002
     */
    @Transactional
    public String generateUserId() {
        long nextValue = getNextSequenceValue(USER_SEQUENCE, USER_START_VALUE);
        String userId = "U" + nextValue;
        log.debug("Generated User ID: {}", userId);
        return userId;
    }

    /**
     * Get next sequence value (thread-safe)
     */
    private long getNextSequenceValue(String sequenceName, long startValue) {
        IdSequence sequence = idSequenceRepository.findBySequenceNameForUpdate(sequenceName)
                .orElseGet(() -> {
                    // Initialize sequence if not exists
                    IdSequence newSeq = new IdSequence();
                    newSeq.setSequenceName(sequenceName);
                    newSeq.setCurrentValue(startValue);
                    return idSequenceRepository.save(newSeq);
                });

        long currentValue = sequence.getCurrentValue();
        sequence.setCurrentValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return currentValue;
    }

    /**
     * Extract sequence number from User ID
     * Example: U100001 -> 100001
     */
    public Long extractUserSequence(String userId) {
        if (userId == null || !userId.startsWith("U")) {
            return null;
        }
        try {
            return Long.parseLong(userId.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}