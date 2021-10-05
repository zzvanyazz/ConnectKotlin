import com.connect.core.impl.auth.basic.BasicAuthenticator
import com.connect.core.impl.basic.ConnectionProviderImpl
import com.connect.core.impl.basic.ProcessorProvider
import com.connect.core.impl.basic.receivers.ObjectReceiver
import com.connect.core.impl.ip.IPConnectionCreator
import com.connect.core.impl.ip.finder.ConsoleInputConnectionFinder
import com.connect.core.impl.ip.finder.ServerSocketConnectionListener
import com.connect.core.processors.ProcessorsFactory
import com.connect.core.processors.clipboard.ClipboardProcessorFactory
import processors.KeyListenerFactory
import processors.KeyPressingSharer
import java.io.IOException


fun main(args: Array<String>) {
    Main.main();
}

object Main {

    private val factories: MutableList<ProcessorsFactory> = mutableListOf(
        KeyListenerFactory(),
        ClipboardProcessorFactory(),
        )

    @Throws(IOException::class)
    @JvmStatic
    fun main() {
        val authenticator = BasicAuthenticator()
        authenticator.initCredentials()
        val receiver = ObjectReceiver()
        val processorProvider = ProcessorProvider(receiver)
        val ipConnectionCreator = IPConnectionCreator(receiver, authenticator)
        val consoleFinder = ConsoleInputConnectionFinder(ipConnectionCreator)
        val serverSocketListener = ServerSocketConnectionListener(ipConnectionCreator)
        val provider = ConnectionProviderImpl(consoleFinder, serverSocketListener)


        factories.forEach {
            it.messageProcessors.forEach(processorProvider::addMessageProcessor)
            it.requestProcessors.forEach(processorProvider::addRequestProcessor)
            it.connectionReceivers.forEach(provider::addConnectionListener)
        }
    }

}
