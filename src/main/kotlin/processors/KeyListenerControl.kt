package processors


class KeyListenerControl {

    private val lock = Object();

    @Volatile
    private var readSkip = false

    @Volatile
    private var timeSkip = getTime()

    private val skipMilliseconds = 200

    fun onReading() {
        synchronized(lock) {
            readSkip = false
            timeSkip = getTime(skipMilliseconds)
        }
    }

    fun skipRead(): Boolean {
        synchronized(lock) {
            return readSkip && timeSkip.afterNow()
        }
    }

    fun skipWrite(): Boolean {
        synchronized(lock) {
            return !readSkip && timeSkip.afterNow()
        }
    }


    fun onWrite() {
        synchronized(lock) {
            readSkip = true
            timeSkip = getTime(skipMilliseconds)
        }
    }

    private fun getTime(mil: Int = 0): Long {
        return System.nanoTime() + mil * 1_000_000
    }

    private fun Long.afterNow(): Boolean {
        return this > getTime();
    }

}
