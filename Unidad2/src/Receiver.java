import java.util.Random;

public class Receiver extends Thread {

    public Receiver() {
        start();
    }

    /**
     * Esta es la función Receive asíncrona.
     * Al ejecutarse esta función, este hilo no se queda bloqueado esperando una confirmación de recepción.
     * Se va a seguir intentando recibir nuevos mensajes siempre que estos estén disponibles en el buffer
     * @author Aldo Troccoli
     */
    private void receive () {
        Random rand = new Random();
        // Se genera un número entre 1000 y 800 que será usado como pausa (en ms)
        int sleepTime = rand.nextInt(1000 - 800 + 1) + 800;

        try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // synchronized es un monitor que nos permite controlar el acceso al buffer.
        synchronized (Port.getBuffer()) {
            int consumedMessage = Port.getBuffer().poll();
            System.out.println("*********************************************************************" );
            System.out.println("Receiver: Se ha consumido el mensaje # " + consumedMessage);
            System.out.println("*********************************************************************" );
        }
    }

    /**
     * Va a intentar sacar nuevos mensajes del buffer siempre que este no esté vacío.
     * Receiver si este se encuentra dormido esperando a tener mensajes que consumir.
     * @author Aldo Troccoli
     */
    @Override
    public void run() {
        while (true) {
            if (Port.getBuffer().size() == 0) {
                System.out.println("*********************************************************************" );
                System.out.println("Receiver: El buffer está vacío. Receiver en espera de nuevos mensajes");
                System.out.println("*********************************************************************" );
            }

            try {
                // Si este semáforo está a 0 nos indica que no hay más mensajes disponibles en el buffer para poderlos consumir.
                // El hilo del Receiver se duerme en este semáforo, hasta que el Transmitter coloque un nuevo mensaje en el buffer.

                // Si el semáforo es > 0, entonces el Receiver pueder consumir el siguiente mensaje del buffer.
                Port.getSemMessagesAvailable().acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Sacamos del buffer el siguiente mensaje a consumir.
            receive();

            // Tras haber consumido un mensaje, indica al Transmitter que se ha liberado un slot en el buffer, en caso
            // de que el Transmitter estuviese dormido se despierta para poder seguir enviando nuevos mensajes.
            Port.getSemBufferAvailable().release();
        }
    }
}
