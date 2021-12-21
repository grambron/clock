import clock.SettableClock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import statistic.EventStatisticImpl
import java.time.Duration


private const val EPSILON = 1e-5
private const val MINUTES_IN_HOUR = 60

class EventStatisticTest {

    private fun <K, V> Map<K, V>.getOrThrow(key: K): V =
        this[key] ?: throw NoSuchElementException("$key is not in map")

    @Test
    fun `get statistics by non-existing name`() {
        val statistic = EventStatisticImpl(SettableClock())

        statistic.incEvent("t")

        assertEquals(statistic.getEventStatisticByName("a"), 0.0, EPSILON)
    }

    @Test
    fun `simple get statistics by name`() {
        val statistic = EventStatisticImpl(SettableClock())

        statistic.incEvent("t")
        statistic.incEvent("test")
        statistic.incEvent("test")

        assertEquals(statistic.getEventStatisticByName("test"), 2.0 / MINUTES_IN_HOUR, EPSILON)
        assertEquals(statistic.getEventStatisticByName("t"), 1.0 / MINUTES_IN_HOUR, EPSILON)
    }

    @Test
    fun `get all statistics without events`() {
        val statistic = EventStatisticImpl(SettableClock())

        assertTrue(statistic.getAllEventStatistic().isEmpty())
    }

    @Test
    fun `get statistic with events`() {
        val statistic = EventStatisticImpl(SettableClock())

        statistic.incEvent("a")
        statistic.incEvent("b")
        statistic.incEvent("b")
        statistic.incEvent("b")

        val allStatistic = statistic.getAllEventStatistic()

        assertEquals(2, allStatistic.size)

        assertEquals(allStatistic.getOrThrow("a"), 1.0 / MINUTES_IN_HOUR, EPSILON)
        assertEquals(allStatistic.getOrThrow("b"), 3.0 / MINUTES_IN_HOUR, EPSILON)

        val aStatistic = statistic.getEventStatisticByName("a")
        val bStatistic = statistic.getEventStatisticByName("b")

        assertEquals(aStatistic, 1.0 / MINUTES_IN_HOUR, EPSILON)
        assertEquals(bStatistic, 3.0 / MINUTES_IN_HOUR, EPSILON)
    }

    @Test
    fun `get statistics by name with refreshing`() {
        val clock = SettableClock()
        val statistic = EventStatisticImpl(clock)

        statistic.incEvent("first")
        statistic.incEvent("first")

        clock.add(Duration.ofHours(1))

        statistic.incEvent("second")
        statistic.incEvent("second")

        assertEquals(0.0, statistic.getEventStatisticByName("first"))
        assertEquals(2.0 / MINUTES_IN_HOUR, statistic.getEventStatisticByName("second"))

        val allStatistic = statistic.getAllEventStatistic()

        assertEquals(1, allStatistic.size)
        assertEquals(allStatistic.getOrThrow("second"), 2.0 / MINUTES_IN_HOUR, EPSILON)
    }

}