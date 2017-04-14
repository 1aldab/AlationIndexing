package alation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        IndexerTest.class,
        QueryServerTest.class })

public class AllTests {

}