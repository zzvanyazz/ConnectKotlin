package processors

import com.connect.core.connection.Connection
import org.slf4j.LoggerFactory
import java.io.RandomAccessFile
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class KeyPressingSharer {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val DISCONNECTED_KEY = -1;
    private val pipeName = "\\\\.\\pipe\\registered_keys"
    private val connections: Queue<Connection> = ConcurrentLinkedQueue()
    private var pipe: RandomAccessFile?
    private val isConnected = AtomicBoolean(false)

    init {
        logger.info("[Key sharing] Starting key listener exe")
        Runtime.getRuntime().exec("KeyListener\\ConsoleListenerBackground.exe")
        logger.info("[Key sharing] Connecting to pipe")
        Thread.sleep(1000)
        pipe = RandomAccessFile(pipeName, "rw")
        Thread(this::read).start()
        logger.info("[Key sharing] Starting reading from pipe")
        tryInitPipe()
    }


    fun onNewConnection(connection: Connection) {
        connections.add(connection)
        connection.connectionStatus.setDisconnectedListener { onDisconnected(connection) }
    }

    private fun onDisconnected(connection: Connection) {
        connections.remove(connection)
    }

    private fun tryInitPipe() {
        try {
            pipe = RandomAccessFile(pipeName, "rw")
            isConnected.set(true)
        } catch (e: Exception) {
            isConnected.set(false)
        }
    }


    private fun read() {
        while (true) {
            ensureConnected();
            try {
                if (!isConnected()) {
                    Thread.sleep(2000)
                    continue
                }
                val key = readKey()
                assertValid(key)
                processNewKey(key)
            } catch (e: Exception) {
                logger.warn("[Key sharing] reading from pipe error", e)
            }
        }
    }

    private fun ensureConnected() {
        if (isConnected())
            return
        tryClose()
        tryInitPipe()
    }

    private fun isConnected(): Boolean {
        return !isConnected.get() && pipe != null
    }

    private fun readKey() : Int {
        Thread.sleep(20)
        return pipe!!.readLine().toInt()
    }


    private fun assertValid(key: Int) {
        if (key == DISCONNECTED_KEY) {
            tryClose()
            throw RuntimeException("Pipe is disconnected")
        }
    }

    private fun processNewKey(key: Int) {
        connections.forEach {
            logger.info("[Key sharing] Sending new key to connection [{%s}]", it)
            try {
                it.sendMessage(KeyPressingMessage(key))
            } catch (e: Exception) {
                logger.error("[Key sharing] Sending new key error", e)
            }
        }
    }

    private fun tryClose() {
        try {
            pipe?.close()
        } catch (e: Exception) {
        }
        pipe = null
        isConnected.set(false)
    }

}