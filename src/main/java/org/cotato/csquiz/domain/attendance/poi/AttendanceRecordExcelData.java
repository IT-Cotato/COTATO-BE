package org.cotato.csquiz.domain.attendance.poi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.poi.CellData;
import org.cotato.csquiz.common.poi.ExcelColumnName;
import org.cotato.csquiz.common.poi.ExcelData;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.vo.AttendRecordStatics;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Session;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordExcelData implements ExcelData {

    private static final String NAME_VALUE = "이름";
    private static final String SESSION_COUNTS = "세션 수";
    private static final String OFFLINE = "대면";
    private static final String ONLINE = "비대면";
    private static final String LATE = "지각";
    private static final String ABSENT = "결석";

    @ExcelColumnName(headerName = "이름")
    private String name;

    private List<AttendRecord> records;

    @ExcelColumnName(headerName = "세션 수")
    private String attendanceCounts;

    @ExcelColumnName(headerName = "대면")
    private String offline;

    @ExcelColumnName(headerName = "비대면")
    private String online;

    @ExcelColumnName(headerName = "지각")
    private String late;

    @ExcelColumnName(headerName = "결석")
    private String absent;

    public static AttendanceRecordExcelData of(Member member, int size, List<AttendRecord> records) {
        return AttendanceRecordExcelData.builder()
                .name(member.getName())
                .records(records)
                .attendanceCounts(String.valueOf(size))
                .build();
    }

    @Override
    public List<CellData> headers() {
        List<CellData> headers = new ArrayList<>();
        headers.add(CellData.builder().value(NAME_VALUE).build());

        for (AttendRecord record : records) {
            headers.add(CellData.builder().value(record.sessionName).build());
        }
        headers.add(CellData.builder().value(SESSION_COUNTS).build());
        headers.add(CellData.builder().value(OFFLINE).build());
        headers.add(CellData.builder().value(ONLINE).build());
        headers.add(CellData.builder().value(LATE).build());
        headers.add(CellData.builder().value(ABSENT).build());

        return headers;
    }

    @Override
    public List<CellData> datas() {
        List<CellData> datas = new ArrayList<>();
        datas.add(CellData.builder().value(name).build());

        for (AttendRecord record : records) {
            datas.add(CellData.builder().value(record.result().getDescription()).build());
        }
        datas.add(CellData.builder().value(attendanceCounts).build());

        AttendRecordStatics recordStatics = AttendRecordStatics.from(records);

        datas.add(CellData.builder().value(String.valueOf(recordStatics.offline())).build());
        datas.add(CellData.builder().value(String.valueOf(recordStatics.online())).build());
        datas.add(CellData.builder().value(String.valueOf(recordStatics.late())).build());
        datas.add(CellData.builder().value(String.valueOf(recordStatics.absent())).build());

        return datas;
    }

    public record AttendRecord(
            String sessionName,
            AttendanceResult result
    ) {
        public static AttendRecord of(Session session, AttendanceRecord attendanceRecord) {
            return new AttendRecord(
                    session.getTitle(),
                    attendanceRecord.getAttendanceResult()
            );
        }
    }
}
