package io.pivotal.spring.gemfire.async;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * Created by lei_xu on 7/17/16.
 */
public interface RawRecordRepository extends GemfireRepository<RawRecord, String> {
    RawRecord findByUuid(String uuid);
}
