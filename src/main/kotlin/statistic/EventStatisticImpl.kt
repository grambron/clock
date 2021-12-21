package statistic

import clock.Clock
import java.time.Instant
import java.util.*

private const val MINUTES_IN_HOUR = 60

class EventStatisticImpl(private val clock: Clock) : EventStatistic<Double> {
    private val eventsQueue: Queue<Pair<String, Instant>> = ArrayDeque()
    private val counter: MutableMap<String, Int> = HashMap()

    override fun incEvent(name: String) {
        val currentTime = clock.now()

        refreshQueue(currentTime)
        eventsQueue.add(Pair(name, currentTime))
        counter.merge(name, 1, Int::plus)
    }

    override fun getEventStatisticByName(name: String): Double {
        val currentTime = clock.now()

        refreshQueue(currentTime)

        return counter.getOrDefault(name, 0).toDouble() / MINUTES_IN_HOUR
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        val currentTime = clock.now()

        refreshQueue(currentTime)

        return counter.toMap().mapValues { it.value.toDouble() / MINUTES_IN_HOUR }
    }

    override fun printStatistic() = println(getAllEventStatistic())

    private fun refreshQueue(currentTime: Instant) {
        while (eventsQueue.size > 0 && eventsQueue.peek().second.isBefore(currentTime)) {
            val name = eventsQueue.poll().first

            val eventCount = counter[name] ?: return

            if (eventCount >= 2) {
                counter[name] = eventCount - 1
            } else {
                counter.remove(name)
            }
        }
    }
}