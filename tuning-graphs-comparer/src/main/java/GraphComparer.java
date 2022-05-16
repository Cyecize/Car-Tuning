import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphComparer {
    private static final String MAKE_SURE_MSG = "Make sure cells are separated by whitespace "
            + "and the graph size matches the provided values.";

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Graph Comparer!");
        final GraphSize graphSize = getGraphSize();

        System.out.println("Paste the first graph");
        System.out.println(MAKE_SURE_MSG);
        final Graph firstGraph = readGraph(graphSize, "First graph");

        System.out.println("Paste the second graph");
        System.out.println(MAKE_SURE_MSG);
        final Graph secondGraph = readGraph(graphSize, "Second graph");

        final Graph diffGraph = firstGraph.createDiffGraph(secondGraph);

        final Workbook spreadsheet = createSpreadsheet(List.of(firstGraph, secondGraph, diffGraph));

        String absolutePath = GraphComparer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        final File resultFile = new File(
                absolutePath
                        + "/"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh_mm_ss"))
                        + "_graph_diff_results.xlsx");
        resultFile.createNewFile();

        try (OutputStream os = new FileOutputStream(resultFile)) {
            spreadsheet.write(os);
        }

        System.out.println(String.format("File saved in %s.", resultFile.getAbsolutePath()));
    }

    private static GraphSize getGraphSize() {
        System.out.println("Enter the width of the graphs:");
        final int cols = Integer.parseInt(SCANNER.nextLine());
        if (cols <= 0) {
            throw new IllegalArgumentException("Invalid graph width");
        }

        System.out.println("Enter the height of the graphs:");
        final int rows = Integer.parseInt(SCANNER.nextLine());
        if (rows <= 0) {
            throw new IllegalArgumentException("Invalid graph height");
        }

        return new GraphSize(cols, rows);
    }

    private static Graph readGraph(GraphSize size, String graphName) {
        final String[][] graphContents = new String[size.getRows()][];
        for (int row = 0; row < size.getRows(); row++) {
            final String[] rowContents = SCANNER.nextLine().split("\\s+");
            if (rowContents.length != size.getCols()) {
                throw new IllegalArgumentException("Invalid graph width at row " + row);
            }

            for (String colValue : rowContents) {
                try {
                    Integer.parseInt(colValue);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid graph data at row " + row);
                }
            }

            graphContents[row] = rowContents;
        }

        return new Graph(graphContents, size, graphName);
    }

    private static Workbook createSpreadsheet(List<Graph> graphs) {
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

            final GraphSize graphSize = graph.getGraphSize();
            for (int rowId = 0; rowId < graphSize.getRows(); rowId++) {
                final Row graphRow = sheet.createRow(rowCounter.incrementAndGet());
                for (int colId = 0; colId < graphSize.getCols(); colId++) {
                    final String cellValue = graph.get(rowId, colId);
                    graphRow.createCell(colId + 1).setCellValue(cellValue);
                    maxCol = Math.max(maxCol, colId + 1);
                }
            }
        }

        for (int i = 0; i < maxCol; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}
