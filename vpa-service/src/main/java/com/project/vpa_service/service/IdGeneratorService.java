package com.project.vpa_service.service;

import com.project.vpa_service.entity.IdSequence;
import com.project.vpa_service.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdGeneratorService {

    private final IdSequenceRepository idSequenceRepository;

    private static final String PSP_SEQUENCE = "PSP_SEQ";
    private static final String VPA_SEQUENCE = "VPA_SEQ";
    private static final long PSP_START_VALUE = 1L;
    private static final long VPA_START_VALUE = 100000L;

    /**
     * Generate PSP ID like PSP001, PSP002
     */
    @Transactional
    public String generatePspId() {
        long nextValue = getNextSequenceValue(PSP_SEQUENCE, PSP_START_VALUE);
        String pspId = "PSP" + String.format("%03d", nextValue);
        log.debug("Generated PSP ID: {}", pspId);
        return pspId;
    }

    /**
     * Generate VPA ID like VPA100001, VPA100002
     */
    @Transactional
    public String generateVpaId() {
        long nextValue = getNextSequenceValue(VPA_SEQUENCE, VPA_START_VALUE);
        String vpaId = "VPA" + nextValue;
        log.debug("Generated VPA ID: {}", vpaId);
        return vpaId;
    }

    private long getNextSequenceValue(String sequenceName, long startValue) {
        IdSequence sequence = idSequenceRepository.findBySequenceNameForUpdate(sequenceName)
                .orElseGet(() -> {
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
}