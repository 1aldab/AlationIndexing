package alation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class QueryServerTest {

    @Parameter(0)
    public String query;
    @Parameter(1)
    public int count;
    @Parameter(2)
    public String result;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "sal",     4, "[May_Sales, Sales, Salary_Net, Sep_Salary]" },
                { "Sales",   3, "[May_Sales, Sales, May_Sales]" },
                { "REVENUE", 3, "[Aug_Sep_Revenue_Net, Revenue, May_Revenue]" },
                { "rev",     2, "[Aug_Sep_Revenue_Net, Revenue]"},
                { "s",       4, "[May_Sales, Sales, Salary_Net, Sep_Salary]" },
                { "xyz",     2, "[]"},
                { "",        3, "[May_Sales, Sales, Salary_Net]"}
        });
    }

    String csvFileName = "data.csv";
    Indexer indexer = new Indexer(csvFileName);
    QueryServer qs = new QueryServer(indexer);

    @Test
    public void testGetTopMatches() throws Exception {
        String[] topMatches = qs.getTopMatches("sample_query", 10);
        assertEquals(result, Arrays.toString(qs.getTopMatches(query, count)));
    }

}