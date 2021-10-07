package processors

import com.connect.core.connection.Connection
import com.connect.core.connection.processors.MessageProcessor
import com.connect.core.connection.processors.RequestProcessor
import com.connect.core.processors.ProcessorsFactory
import java.util.function.Consumer

class KeyListenerFactory : ProcessorsFactory {

    private val control = KeyListenerControl();

    override fun getConnectionReceivers(): MutableList<Consumer<Connection>> {
        val keyPressingSharer = KeyPressingSharer(control)
        return mutableListOf(Consumer<Connection> {
            keyPressingSharer.onNewConnection(it)
        } )
    }

    override fun getMessageProcessors(): MutableList<MessageProcessor<*>> {
        return mutableListOf(KeyPressingProcessor(control))
    }

    override fun getRequestProcessors(): MutableList<RequestProcessor<*, *>> {
        return emptyList<RequestProcessor<*, *>>().toMutableList()
    }
}