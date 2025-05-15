package org.cotato.csquiz.domain.recruitment.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RecruitmentNotificationEmailLogJdbcRepository {

    private static final int BATCH_SIZE = 1000;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllWithBatch(List<RecruitmentNotificationEmailLog> logs) {

        // language=MySQL
        final String SQL = "INSERT INTO recruitment_notification_email_log "
                + "(receiver_id, notification_id, send_success, created_at, modified_at) "
                + "VALUES (?, ?, ?, now(), now())";

        for (int start = 0; start < logs.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, logs.size());
            List<RecruitmentNotificationEmailLog> chunk = logs.subList(start, end);

            jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    var log = chunk.get(i);
                    ps.setLong(1, log.getReceiver().getId());
                    ps.setLong(2, log.getNotification().getId());
                    ps.setBoolean(3, log.getSendSuccess());
                }

                @Override
                public int getBatchSize() {
                    return chunk.size();
                }
            });
        }
    }
}
