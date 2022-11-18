package com.spartan.dc.core.util.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.SheetUtil;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author wxq
 * @create 2022/11/10 18:42
 * @description excel
 */
@Slf4j
public class HutoolExcelUtils {

    public static void writeExcel(String filename, List<?> rows, HttpServletResponse response) {
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        ExcelWriter writer = null;
        ServletOutputStream out = null;
        if (CollectionUtils.isEmpty(rows)) {
            rows = CollUtil.newArrayList(CollUtil.newArrayList("No data"));
        }
        try {
            writer = ExcelUtil.getWriter();
            writer.write(rows, true);

            StyleSet style = writer.getStyleSet();
            Font font = writer.createFont();
            font.setColor(IndexedColors.BLACK.index);
            font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            font.setFontName("Song typeface");
            style.getHeadCellStyle().setFont(font);
            int columnCount = writer.getColumnCount();
            for (int i = 0; i < columnCount; ++i) {
                double width = SheetUtil.getColumnWidth(writer.getSheet(), i, false);
                if (width != -1.0D) {
                    width *= 256.0D;
                    width += 220D;
                    writer.setColumnWidth(i, Math.toIntExact(Math.round(width / 256D)));
                }
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xls");
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (IOException e) {
            log.error("HutoolExcelUtils:", e);
        } finally {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }
}
