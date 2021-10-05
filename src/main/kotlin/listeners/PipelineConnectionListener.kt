package listeners

import com.connect.core.connection.Connection
import com.connect.core.impl.basic.ConnectionFinder
import java.io.RandomAccessFile
import java.util.function.Consumer
import kotlin.io.path.Path

class PipelineConnectionListener : ConnectionFinder {

    var consumer: Consumer<Connection>? = null

    override fun addConnectionConsumer(consumer: Consumer<Connection>?) {
        this.consumer = consumer;
    }

    fun addNewPipeline(pipelineName: String) {
        val path = Path("tmp", pipelineName)
            .toAbsolutePath()
            .toString()

        val pipeline = RandomAccessFile(path, "rw");
    }

    override fun start() {
    }

    override fun pause() {
    }

}