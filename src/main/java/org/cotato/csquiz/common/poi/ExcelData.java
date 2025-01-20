package org.cotato.csquiz.common.poi;

import java.util.List;

public interface ExcelData {

    List<CellData> headers();

    List<CellData> datas();
}
