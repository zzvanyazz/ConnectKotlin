package processors

import com.connect.core.connection.Connection
import com.connect.core.connection.processors.MessageProcessor
import java.awt.Robot

class KeyPressingProcessor(private val control: KeyListenerControl) :MessageProcessor<KeyPressingMessage> {

    private val robot = Robot();

    override fun getProcessableClass(): Class<KeyPressingMessage> {
        return KeyPressingMessage::class.java;
    }

    override fun process(keyMessage: KeyPressingMessage?, connection: Connection?): Void? {
        keyMessage?.key?.let {
            if (control.skipWrite())
                return null
            robot.keyPress(it)
            control.onWrite()
        }
        return null;
    }

}