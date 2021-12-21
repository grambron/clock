package clock

import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

class SettableClock(initTime: Instant = Instant.now()) : Clock {
    private val currentTime: AtomicReference<Instant> = AtomicReference(initTime)

    override fun now(): Instant {
        return currentTime.get()
    }

    fun add(amount: TemporalAmount): Instant {
        return currentTime.updateAndGet { time -> time.plus(amount) }
    }
}