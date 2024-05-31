package org.cotato.csquiz.common.interceptor;

import lombok.AllArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;

@AllArgsConstructor
public enum ProcessStatus {


    PROCESSING("현재 해당 요청 처리 중"),
    SUCCESS("요청 완료")

    ;

    private final String description;
}
