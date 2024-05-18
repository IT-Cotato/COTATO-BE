package cotato.csquiz.domain.enums;

import lombok.Getter;

@Getter
public enum ItIssue {

    IT_ON("ISSUE_ON"),
    IT_OFF("ISSUE_OFF");

    private final String issue;

    ItIssue(String issue) {
        this.issue = issue;
    }
}
