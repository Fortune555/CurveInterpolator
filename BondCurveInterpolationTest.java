
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BondCurveInterpolationTest {

    private BondCurveInterpolation bondCurveInterpolation;
    private Table mockTable;

    @Before
    public void setUp() throws Exception {
        // Create mock data
        StringColumn dateColumn = StringColumn.create("Date", new String[]{"2023-01-01", "2023-06-01", "2023-12-01"});
        IntColumn numDaysColumn = IntColumn.create("Num Days", new int[]{0, 151, 334});
        DoubleColumn bidRateColumn = DoubleColumn.create("Bid Rate", new double[]{0.01, 0.015, 0.02});
        DoubleColumn askRateColumn = DoubleColumn.create("Ask Rate", new double[]{0.02, 0.025, 0.03});
        DoubleColumn midRateColumn = DoubleColumn.create("Mid Rate", new double[]{0.015, 0.02, 0.025});

        mockTable = Table.create("Mock Table", dateColumn, numDaysColumn, bidRateColumn, askRateColumn, midRateColumn);

        // Initialize BondCurveInterpolation with mock data
        bondCurveInterpolation = new BondCurveInterpolation("2023-04-01", "Bid", "") {
            @Override
            public Table getData() {
                return mockTable;
            }
        };
    }

    @Test
    public void testGetData() {
        Table table = bondCurveInterpolation.getData();
        assertNotNull(table);
        assertEquals(3, table.rowCount());
    }

    @Test
    public void testGetRateWithinRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2023-04-01", "Bid", "") {
            @Override
            public Table getData() {
                return mockTable;
            }
        };
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.012, rate, 0.001);
    }

    @Test
    public void testGetRateAboveRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2024-01-01", "Bid", "") {
            @Override
            public Table getData() {
                return mockTable;
            }
        };
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.02, rate, 0.001);
    }

    @Test
    public void testGetRateBelowRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2022-01-01", "Bid", "") {
            @Override
            public Table getData() {
                return mockTable;
            }
        };
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0, rate, 0.001);
    }

    @Test
    public void testGetRateWithDifferentRateType() {
        bondCurveInterpolation = new BondCurveInterpolation("2023-04-01", "Ask", "") {
            @Override
            public Table getData() {
                return mockTable;
            }
        };
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.022, rate, 0.001);
    }
}
