package commands;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PingTest {

    @Test
    public void testPong() {
        assertEquals("Pong", commands.Ping.run());
    }
}
