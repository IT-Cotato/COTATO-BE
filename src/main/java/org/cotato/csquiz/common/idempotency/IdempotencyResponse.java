package org.cotato.csquiz.common.idempotency;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class IdempotencyResponse implements Serializable {

    private ProcessStatus processStatus;
    private Object result;

    public boolean isProcessing() {
        return this.processStatus == ProcessStatus.PROCESSING;
    }

    public boolean isSucceed() {
        return this.processStatus == ProcessStatus.SUCCESS;
    }
}
