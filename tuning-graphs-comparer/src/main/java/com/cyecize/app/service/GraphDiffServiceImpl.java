package com.cyecize.app.service;

import com.cyecize.app.dto.CreateDiffDto;
import com.cyecize.app.dto.Graph;
import com.cyecize.app.dto.GraphSize;
import com.cyecize.ioc.annotations.Service;
import com.cyecize.solet.SoletConfig;
import com.cyecize.solet.SoletConstants;
import com.cyecize.summer.common.annotations.Configuration;
import com.cyecize.summer.utils.PathUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GraphDiffServiceImpl implements GraphDiffService {
    private final SoletConfig soletConfig;

    private final String uploadedFilesLocation;

    public GraphDiffServiceImpl(SoletConfig soletConfig,
                                @Configuration("uploaded.files.location") String uploadedFilesLocation) {
        this.soletConfig = soletConfig;
        this.uploadedFilesLocation = uploadedFilesLocation;
    }

    @Override
    public String generateDiffFile(CreateDiffDto dto) {
        final GraphSize graphSize = new GraphSize(dto.getGraphWidth(), dto.getGraphHeight());

        final Graph graph1 = this.readGraph(graphSize, "First graph " + dto.getGraph1Name(), dto.getGraph1());
        final Graph graph2 = this.readGraph(graphSize, "Second graph " + dto.getGraph2Name(), dto.getGraph2());

        final Graph diffGraph = graph1.createDiffGraph(graph2, dto.getIncludeDiffValues());

        final Workbook workbook = this.createSpreadsheet(
                List.of(graph1, graph2, diffGraph),
                Objects.requireNonNullElse(dto.getVerticalHeader(), "").split("\\s+"),
                Objects.requireNonNullElse(dto.getHorizontalHeader(), "").split("\\s+")
        );

        try (workbook) {
            final String fileName = String.format("%s_%s.xlsx",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss")),
                    Objects.requireNonNullElse(dto.getFileName(), "_graph_diff_results")
            );

            final String filePath = PathUtils.appendPath(this.uploadedFilesLocation, fileName);
            final File physicalFile = new File(PathUtils.appendPath(this.getAssetsDir(), filePath));

            physicalFile.getParentFile().mkdirs();
            physicalFile.createNewFile();
            try (OutputStream stream = new FileOutputStream(physicalFile)) {
                workbook.write(stream);
            }

            return fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error " + ex.getMessage();
        }
    }

    private Graph readGraph(GraphSize size, String graphName, String graphValue) {
        final String[][] graphContents = new String[size.getRows()][];

        final String[] rows = graphValue.split("\n");
        if (rows.length != size.getRows()) {
            throw new IllegalArgumentException(String.format("Expecting %s to have %d rows", graphName, size.getRows()));
        }

        for (int row = 0; row < rows.length; row++) {
            final String[] rowContents = rows[row].split("\\s+");
            if (rowContents.length != size.getCols()) {
                throw new IllegalArgumentException(String.format(
                        "Expecting %s at row %d to have with of exactly %d columns", graphName, row + 1, size.getCols()
                ));
            }
            graphContents[row] = rowContents;
        }

        return new Graph(graphContents, size, graphName);
    }

    private Workbook createSpreadsheet(List<Graph> graphs, String[] verticalHeader, String[] horizontalHeader) {
        final SXSSFWorkbook workbook = new SXSSFWorkbook();
        final SXSSFSheet sheet = workbook.createSheet();
        sheet.trackAllColumnsForAutoSizing();

        final AtomicInteger rowCounter = new AtomicInteger(0);
        int maxCol = 0;

        final Row titleRow = sheet.createRow(rowCounter.incrementAndGet());
        titleRow.createCell(0).setCellValue(String.format("Displaying %d graphs", graphs.size()));

        for (Graph graph : graphs) {
            final Row graphTitleRow = sheet.createRow(rowCounter.incrementAndGet());
            graphTitleRow.createCell(0).setCellValue(graph.getGraphName());

            //Add horizontal header
            if (horizontalHeader.length > 0) {
                final Row horizontalHeaderRow = sheet.createRow(rowCounter.incrementAndGet());
                for (int cellId = 0; cellId < horizontalHeader.length; cellId++) {
                    horizontalHeaderRow.createCell(cellId + 2).setCellValue(horizontalHeader[cellId]);
                }
            }

            final GraphSize graphSize = graph.getGraphSize();
            for (int rowId = 0; rowId < graphSize.getRows(); rowId++) {
                final Row graphRow = sheet.createRow(rowCounter.incrementAndGet());
                if (verticalHeader.length >= rowId + 1) {
                    graphRow.createCell(1).setCellValue(verticalHeader[rowId]);
                }

                for (int colId = 0; colId < graphSize.getCols(); colId++) {
                    final String cellValue = graph.get(rowId, colId);
                    graphRow.createCell(colId + 2).setCellValue(cellValue);
                    maxCol = Math.max(maxCol, colId + 2);
                }
            }
        }

        for (int i = 0; i < maxCol; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private String getAssetsDir() {
        return this.soletConfig.getAttribute(SoletConstants.SOLET_CONFIG_ASSETS_DIR).toString();
    }
}
