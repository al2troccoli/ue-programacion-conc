
public class Bakery extends Thread {

	// Variables para los hilos 
	public int thread_id; // EL id del hilo actual
	public static final int countToThis = 200;
	public static final int numberOfThreads = 5;
	public static volatile int count = 0; // Un contador para hacer tests

	// Variables globales-----------------------------------------------------
	// Un array que asigna valores booleanos para cada hilo, un valor puesto a true significa que el hilo i quiere entrear en la sección crítica
	private static volatile boolean[] choosing = new boolean[numberOfThreads]; 
	// El ticket define la prioridad.																			 
	private static volatile int[] ticket = new int[numberOfThreads];
	
	/*
	 * Thread constructor.
	 */
	public Bakery(int id) {
		thread_id = id;
	}

	// Ejecuta un test para nuestro contador global
	public void run() {
		int scale = 2;

		for (int i = 0; i < countToThis; i++) {

			lock(thread_id);
				// LA SECCIÓN CRÍTICA COMIENZA AQUÍ
				count = count + 1;
				System.out.println("I am " + thread_id + " and count is: " + count);
				
				// Ejecutamos una espera con la intención de que una condición de carrera surja entre los hilos
				try {
					sleep((int) (Math.random() * scale));
				} catch (InterruptedException e) { /* nothing */ }
				// LA SECCIÓN CRÍTICA TERMINA AQUÍ
			unlock(thread_id);
			
		}

	}

	/*
	 * Este método realiza el LOCK
	 */
	public void lock(int id) {
		// Al poner a true la posición id establecemos la intención del hilo asociado de entrar en la sección crítica
		choosing[id] = true;

		// Vamos a generar el próximo ticket disponible.
		ticket[id] = findMax() + 1;
		choosing[id] = false;

		for (int j = 0; j < numberOfThreads; j++) {

			// If the thread j is the current thread go the next thread.
			// Si el hilo j es el hilo actual, pasamos al próximo hilo
			if (j == id)
				continue;
			
			// Esperamos si el hilo j está escojiendo en este instante
			while (choosing[j]) { /* nothing */ }

			
			while (ticket[j] != 0 && (ticket[id] > ticket[j] || (ticket[id] == ticket[j] && id > j))) { /* nothing */ }
						 
		} // for
	}

	/*
	 * Este método quita el LOCK.
	 */
	private void unlock(int id) {
		ticket[id] = 0;
	}

	/*
	 * Encuentra el ticket con el valor más alto dentro del array de tickets.
	 */
	private int findMax() {
		
		int m = ticket[0];

		for (int i = 1; i < ticket.length; i++) {
			if (ticket[i] > m)
				m = ticket[i];
		}
		return m;
	}

	public static void main(String[] args) {

		// Inicialización de las variables globales
		for (int i = 0; i < numberOfThreads; i++) {
			choosing[i] = false;
			ticket[i] = 0;
		}

		Bakery[] threads = new Bakery[numberOfThreads]; // Array de hilos.

		// Inicializamos los hilos
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Bakery(i);
			threads[i].start();
		}

		// Y esperamos ahora a que todos los hilos terminen.
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\nCount is: " + count);
		System.out.println("\nExpected was: " + (countToThis * numberOfThreads));
	} // main

}