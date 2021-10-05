package processors

import com.connect.core.connection.Connection
import com.connect.core.connection.processors.MessageProcessor
import java.awt.Robot

class KeyPressingProcessor :MessageProcessor<KeyPressingMessage> {

    private val robot = Robot();

    override fun getProcessableClass(): Class<KeyPressingMessage> {
        return KeyPressingMessage::class.java;
    }

    override fun process(keyMessage: KeyPressingMessage?, connection: Connection?): Void? {
        keyMessage?.key?.let { robot.keyPress(it) }
        return null;
    }

}