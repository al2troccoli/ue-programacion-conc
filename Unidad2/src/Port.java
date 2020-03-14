import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Port {
    /**
     * El buffer es la cola donde vamos a almacenar todos los mensajes. Funciona como un buzón.
     */
    private static Queue<Integer> buffer = new LinkedList<Integer>();

    /**
     * Esta constanste establece el tamaño del buffer. En este caso se ha definido para un total de 30 mensajes.
     */
    public static final int bufferSize = 30;

    /**
     * Este semáforo le permite saber al Receiver si hay mensajes para consumir en el buffer. Si no hay mensajes disponibles
     * el hilo del Receiver se va a bloquear en este semáforo hasta que el Transmitter cree un nuevo mensaje y lo
     * deposite en el buffer.
     */
    public static Semaphore semMessagesAvailable = new Semaphore(0, true);

    /**
     * Este semáforo le permite saber al Transmitter si el buffer se encuentra lleno o si aun tiene capacidad para
     * seguir colocando nuevos mensajes. Si el buffer está lleno el hilo del Transmitter se bloquea en este semáforo
     * hasta que el Receiver consuma un mensaje y lo saque del buffer.
     */
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
