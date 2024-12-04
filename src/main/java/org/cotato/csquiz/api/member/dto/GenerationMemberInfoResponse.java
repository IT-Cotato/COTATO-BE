package org.cotato.csquiz.api.member.dto;

import java.util.List;

public record GenerationMemberInfoResponse(
        List<GenerationMemberInfo> generationMemberInfos
) {
    public static GenerationMemberInfoResponse from(List<GenerationMemberInfo> generationMemberInfos) {
        return new GenerationMemberInfoResponse(generationMemberInfos);
    }
}
