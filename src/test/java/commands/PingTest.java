package commands;
import commands.ping.Ping;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PingTest {

    @Test
    public void testPong() {
        assertEquals("Pong", Ping.run());
    }
}
