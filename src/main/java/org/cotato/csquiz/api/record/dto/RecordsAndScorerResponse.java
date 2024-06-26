package org.cotato.csquiz.api.record.dto;

import java.util.List;

public record RecordsAndScorerResponse(
        List<RecordResponse> records,
        ScorerResponse scorer
) {

    public static RecordsAndScorerResponse from(List<RecordResponse> records, ScorerResponse scorer) {
        return new RecordsAndScorerResponse(
                records,
                scorer
        );
    }

    public static RecordsAndScorerResponse from(List<RecordResponse> records) {
        return new RecordsAndScorerResponse(
                records, null
        );
    }
}
