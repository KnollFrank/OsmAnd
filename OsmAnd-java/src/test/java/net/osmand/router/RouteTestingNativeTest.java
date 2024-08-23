package net.osmand.router;

import org.junit.Ignore;

// FK-TODO: remove @Ignore
@Ignore
public class RouteTestingNativeTest extends RouteTestingTest {
    
    public RouteTestingNativeTest(String name, TestEntry te) {
        super(name, te);
    }
    
    @Override
    boolean isNative() {
        return true;
    }
}