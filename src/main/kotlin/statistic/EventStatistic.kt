package statistic


interface EventStatistic<T> {
    fun incEvent(name: String)
    fun getEventStatisticByName(name: String): T?
    fun getAllEventStatistic(): Map<String, T>
    fun printStatistic()
}