import java.util.Random;

public class Transmitter extends Thread{
    private int messageCounter = 0;

    public Transmitter() {
        start();
    }

    /**
     * Esta es la función Send asíncrona.
     * Al ejecutarse esta función, este hilo no se queda bloqueado esperando una confirmación de envío.
     * Se van a seguir creando mensajes con toda la intención de ser enviados en cuanto se pueda.
     * @author Aldo Troccoli
     */
    private void Send() {
        Random rand = new Random();

        // Se genera un número entre 200 y 400 que será usado como pausa (en ms)
        int sleepTime = rand.nextInt(400 - 200 + 1) + 200;

        // Incrementamos el # de mensaje
        this.messageCounter++;

        // hacemos la pausa
        try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // synchronized es un monitor que nos permite controlar el acceso al buffer.
        synchronized (Port.getBuffer()) {
            Port.getBuffer().add(this.messageCounter);
        }
        System.out.println("-------------------------------------------------------------------" );
        System.out.println("Transmitter: Se ha transmitido el mensaje # " + this.messageCounter);
        System.out.println("-------------------------------------------------------------------" );
    }

    /**
     * Va a intentar colocar nuevos mensajes en el buffer siempre que este no esté lleno y va a intentar despertar al
     * Receiver si este se encuentra dormido esperando a tener mensajes que consumir.
     * @author Aldo Troccoli
     */
    @Override
    public void run() {
        while (true) {
            if (Port.getBuffer().size() == Port.getBufferSize()) {
                //
                System.out.println("----------------------------------------------------------------" );
                System.out.println("Transmitter: El buffer se encuentra lleno. Transmitter en espera" );
                System.out.println("----------------------------------------------------------------" );
            }

            try {
                // Aquí intenamos restar 1 al semáforo para indicar que queremos escribir en el buffer.
                // Si el buffer está lleno, el valor de este semáforo debe ser igual a 0, por tanto
                // el hilo quedaría bloqueado en este semáforo, hasta que el Receiver le haga un Release,
                // que será cuando el Receiver consuma el próximo mensaje.

                // Contrariamente, si el valor de este semáforo es mayor que 0 nos indica que el buffer
                // aun no está lleno y por tanto el Transmitter puede escribir en él.
                Port.getSemBufferAvailable().acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Se envía un nuevo mensaje
            Send();

            // Este semáforo solo sirve para que el Receiver se bloquee en él cuando no hay mensajes que consumir en el buffer.
            // Tras haber hecho un Send el Trasnmitter despierta de esta manera al Receiver, en caso de que este se
            // encuentre dormido esperando a tener mensajes que consumir.
            Port.getSemMessagesAvailable().release();
        }
    }
}
