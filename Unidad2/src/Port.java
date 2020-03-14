import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Port {

    private static Queue<Integer> buffer = new LinkedList<Integer>();
    public static final int bufferSize = 30;

    public static Semaphore semMessagesAvailable = new Semaphore(0, true);

    public static Semaphore semBufferAvailable = new Semaphore(bufferSize, true);

    /**
     * @author Aldo Troccoli
     * @return El buffer con los números de mensaje
     */
    public static Queue<Integer> getBuffer() {
        return buffer;
    }

    /**
     * @author Aldo Troccoli
     * @return El tamaño del buffer
     */
    public static int getBufferSize() {
        return bufferSize;
    }

    /**
     * @author Aldo Troccoli
     * @return El semáforo que controla la lista no vacía
     */
    public static Semaphore getSemMessagesAvailable() {
        return semMessagesAvailable;
    }

    /**
     * @author Aldo Troccoli
     * @return El semáforo que controla la lista no llena
     */
    public static Semaphore getSemBufferAvailable() {
        return semBufferAvailable;
    }
}
