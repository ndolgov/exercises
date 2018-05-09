package net.ndolgov.exercise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueuesTest {
    @Test
    public void testStringReversalTransformationRule() throws QueueException {
        final String original = "{\"company\": \"Quixotic, Inc.\", \"_company\": \"Quixotic, Inc.\"}";
        final String expected = "{\"_company\":\"Quixotic, Inc.\",\"company\":\".cnI ,citoxiuQ\"}";
        assertSingularMessage(original, 2, expected);
    }

    @Test
    public void testIntegerNegationTransformationRule() throws QueueException {
        final String original = "{\"_value\": 512, \"value\": 512}";
        final String expected = "{\"_value\":512,\"value\":-513}";
        assertSingularMessage(original, 3, expected);
    }

    @Test
    public void testHashCalculationTransformationRule() throws QueueException {
        final String original = "{\"company\": \"Quixotic, Inc.\",\"_hash\":\"company\"}";
        final String expected = "{\"_hash\":\"company\",\"company\":\".cnI ,citoxiuQ\",\"hash\":\"B4X71K4M660xbRADf0Mecir1VReCGpvo98E+szwEVXs=\"}";
        assertSingularMessage(original, 1, expected);
    }

    @Test (expected = QueueException.class)
    public void testHashCollisionTransformationRule() throws QueueException {
        final String original = "{\"company\": \"Quixotic, Inc.\",\"_hash\":\"company\",\"hash\":\"MISMATCH\"}";
        assertSingularMessage(original, 1, "unused");
    }

    @Test
    public void testSpecialMessageIsDispatchedToQ0() throws QueueException {
        final String original = "{\"_special\":\"\"}";
        assertSingularMessage(original, 0, original);
    }

    @Test
    public void testHashFieldMessageIsDispatchedToQ1() throws QueueException {
        final String original = "{\"hash\":\"ABCDEF\"}";
        assertSingularMessage(original, 1, original);
    }

    @Test
    public void testMagicWordMessageIsDispatchedToQ2() throws QueueException {
        final String original = "{\"agent\":\"007\",\"company\":\".cnI ,citoxiuQ\"}";
        assertSingularMessage(original, 2, original);
    }

    @Test
    public void testPrivateMagicWordIsIgnored() throws QueueException {
        final String original = "{\"agent\":\"007\",\"_company\":\".cnI ,citoxiuQ\"}";
        assertSingularMessage(original, 4, original);
    }

    @Test
    public void testIntFieldMessageIsDispatchedToQ3() throws QueueException {
        final String original = "{\"agent\":\"007\",\"value\": 512}";
        final String expected = "{\"agent\":\"007\",\"value\":-513}";
        assertSingularMessage(original, 3, expected);
    }

    @Test
    public void testPrivateIntFieldIsIgnored() throws QueueException {
        final String original = "{\"agent\":\"007\",\"_value\":512}";
        assertSingularMessage(original, 4, original);
    }

    @Test
    public void testMultiPartMessageOrder() throws QueueException {
        final String original0 = "{\"company\": \"Quixotic, Inc.\",\"_sequence\":\"ABCDEFGH\",\"_part\": 0}";
        final String original1 = "{\"company\": \"Quixotic, Inc.\",\"_sequence\":\"ABCDEFGH\",\"_part\": 1}";
        final String original2 = "{\"company\": \"Quixotic, Inc.\",\"_sequence\":\"ABCDEFGH\",\"_part\": 2}";

        final CodingChallenge broker = new Queues();
        broker.enqueue(original0);
        broker.enqueue(original1);
        broker.enqueue(original2);

        final String retrieved1 = broker.next(2);
        assertTrue(retrieved1.contains("0"));
        assertTrue(retrieved1.contains("citoxiuQ"));

        final String retrieved2 = broker.next(2);
        assertTrue(retrieved2.contains("1"));
        assertTrue(retrieved2.contains("citoxiuQ"));

        final String retrieved3 = broker.next(2);
        assertTrue(retrieved3.contains("2"));
        assertTrue(retrieved3.contains("citoxiuQ"));
    }

    @Test
    public void testMultiPartMessageInterleavingAccess() throws QueueException {
        final String original0 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 0}";
        final String original1 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 1}";
        final String original2 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 2}";

        final CodingChallenge broker = new Queues();

        broker.enqueue(original0);
        assertTrue(broker.next(4).contains("0"));

        broker.enqueue(original1);
        assertTrue(broker.next(4).contains("1"));

        broker.enqueue(original2);
        assertTrue(broker.next(4).contains("2"));
    }

    @Test
    public void testPartsMixedWithOtherMessages() throws QueueException {
        final String original0 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 0}";
        final String original1 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 1}";
        final String original = "{\"agent\":\"ABC\"}";

        final CodingChallenge broker = new Queues();

        broker.enqueue(original0);
        assertTrue(broker.next(4).contains("0"));

        broker.enqueue(original1);
        broker.enqueue(original);
        assertTrue(broker.next(4).contains("1"));
        assertTrue(broker.next(4).contains("ABC"));
    }

    @Test
    public void testMultiPartReordering() throws QueueException {
        final String original0 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 0}";
        final String original1 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 2}";
        final String original2 = "{\"agent\":\"XYZ\",\"_sequence\":\"ABCDEFGH\",\"_part\": 1}";

        final CodingChallenge broker = new Queues();

        broker.enqueue(original0);
        broker.enqueue(original1);
        broker.enqueue(original2);

        assertTrue(broker.next(4).contains("0"));
        assertTrue(broker.next(4).contains("1"));
        assertTrue(broker.next(4).contains("2"));
    }

    private static void assertSingularMessage(String original, int qNumber, String expected) throws QueueException {
        final CodingChallenge broker = new Queues();
        broker.enqueue(original);
        assertEquals(expected, broker.next(qNumber));
    }
}
