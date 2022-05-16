import java.util.Objects;

public class GraphSize {
    private final int cols;
    private final int rows;

    public GraphSize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public int getCols() {
        return this.cols;
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GraphSize graphSize = (GraphSize) o;
        return cols == graphSize.cols && rows == graphSize.rows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cols, rows);
    }
}
