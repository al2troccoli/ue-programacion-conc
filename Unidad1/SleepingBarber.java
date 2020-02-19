package Barbero;

// Suposiciones: 
        // - El barbero corta el pelo fuera del objeto protegido 
        //   Si lo cortara dentro, sería menos realista la simulación 
        //   Del tiempo en que se tarda en hacer esta operación. Si 
        //   No se hace este retardo, es decir si el tiempo de corte 
        //   De pelo fuera prácticamente 0, no habría casi nunca 
        //   Procesos esperando. 
        // - Se simula la silla del barbero y las sillas de la sala 
        //   De espera. 
  
public class barberia_1 {
        private int nSillasEspera; 
        private int nSillasEsperaOcupadas = 0; 
        private boolean sillaBarberoOcupada = false; 
        private boolean finCorte = false; 
        private boolean barberoDormido = false; 
         
        //JAVA: sólo puede haber N_Sillas_Espera_max hebras  
        //esperando dentro del monitor a que le toque. 
  
          public barberia_1(int nSillasEspera)          { 
                this.nSillasEspera = nSillasEspera;   } 
  
          public synchronized boolean entrar(int clienteId) 
                  throws InterruptedException { 
                  
           if (nSillasEsperaOcupadas == nSillasEspera) { 
                  // Si no hay sillas libres, me voy sin cortar el pelo 
                System.out.println("---- El cliente " + clienteId + " se va sin cortarse el pelo"); 
                  return false; } 
           else {  
                  //Me quedo esperando si la silla del barbero está ocupada 
                  nSillasEsperaOcupadas ++; 
                  System.out.println ("---- El cliente " + clienteId + " se sienta en la silla de espera"); 
                  while (sillaBarberoOcupada) {wait();} 
         
                  //Desocupo la silla de espera  
                  nSillasEsperaOcupadas --; 
                   //Me siento en la silla del barbero  
                  sillaBarberoOcupada = true; 
                  finCorte = false; 
                   
                  //Si el barbero está dormido lo despierto 
                  if (barberoDormido) { 
                          System.out.println 
                               ("---- El cliente " + clienteId + " despierta al barbero"); 
                          notifyAll();} 
                   
                  //Espero a que me corte el pelo 
System.out.println("---- El cliente " + clienteId + " en la silla de barbero"); 
                  while (!finCorte) {wait();} 
                  sillaBarberoOcupada = false; 
                   //Que pase el siguiente 
                  notifyAll();             
System.out.println("---- El cliente " + clienteId + " se va con el pelo cortado"); 
                  return true; 
  
          } 
          }
          public synchronized void esperarCliente() 
              throws InterruptedException { 
                  //El barbero espera a que llegue un cliente 
                  //Se supone que le corta el pelo fuera del monitor 
                  barberoDormido = true; 
                  while(!sillaBarberoOcupada) { 
    System.out.println("++++ Barbero esperando cliente"); 
                          wait(); 
                          } 
                  barberoDormido = false;                
                  System.out.println("++++ Barbero cortando el pelo"); 
           } 
            
           public synchronized void acabarCorte(){ 
                  finCorte = true; 
                   System.out.println("++++ Barbero termina de cortar el pelo"); 
                  notifyAll(); 
           } 
           
           
           public static void main(String[] args) {

      int numSillas = 6;
      int numClientes = 10;
      barberia_1 s = new barberia_1(numSillas);
      //new Barbero(s).start();
      //for(int i = 0; i < numClientes; i++)
        //  new Cliente(s,i).start();
      
  }
}//Checked 
