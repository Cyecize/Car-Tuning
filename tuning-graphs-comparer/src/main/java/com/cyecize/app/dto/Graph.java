package com.cyecize.app.dto;

public class Graph {
    private final String[][] contents;
    private final GraphSize graphSize;
    private final String graphName;

    public Graph(String[][] contents, GraphSize graphSize, String graphName) {
        this.contents = contents.clone();
        this.graphSize = graphSize;
        this.graphName = graphName;
    }

    public Graph createDiffGraph(Graph graph, boolean includeDiffValues) {
        if (!this.graphSize.equals(graph.graphSize)) {
            throw new IllegalArgumentException("Trying to compare two different graphs.");
        }

        final String[][] diff = new String[this.graphSize.getRows()][this.graphSize.getCols()];
        for (int row = 0; row < this.graphSize.getRows(); row++) {
            final String[] thisRow = this.contents[row];
            final String[] otherRow = graph.contents[row];

            for (int col = 0; col < this.graphSize.getCols(); col++) {
                if (!thisRow[col].equals(otherRow[col])) {
                    if (includeDiffValues) {
                        diff[row][col] = thisRow[col] + "/" + otherRow[col];
                    } else {
                        diff[row][col] = "not eq";
                    }
                } else {
                    diff[row][col] = "eq";
                }
            }
        }

        return new Graph(diff, this.graphSize, String.format("Diff between %s and %s",
                this.getGraphName(),
                graph.getGraphName()
        ));
    }

    public String getGraphName() {
        return this.graphName;
    }

    public String get(int row, int col) {
        return this.contents[row][col];
    }

    public GraphSize getGraphSize() {
        return this.graphSize;
    }
}
