import java.util.concurrent.Semaphore;

public class Presos {

    private static final int N_PRESOS = 10;

    static Semaphore sem = new Semaphore(1);

    public static volatile boolean fin = false;

    public static void main(String[] args) {

	Switch light = new Switch();

	Jefe jefe = new Jefe(light);
	Preso [] presos = new Preso [N_PRESOS];

	for (int i = 0; i < N_PRESOS; i++) {
	    presos[i] = new Preso(light,i);
	}

	jefe.start();
	for (int i = 0; i < N_PRESOS; i++) {
	    presos[i].start();
	}
	
	try {
	    for (int i = 0; i < N_PRESOS; i++) {
		presos[i].join();
	    }
	    jefe.join();
	} catch (Exception ex) {
	    System.out.println(":(");
	}	
    }

        static class Switch {
	boolean status = false;

	public Switch () {
	    this.status = false;
	}

	public void throw_off(){
	    this.status = false;
	    System.out.println("Apagado");

	}

	public void throw_on(){
	    this.status = true;
	    System.out.println("Encendido");

	}

	public boolean get(){
	    return this.status;
	}
    }

    static class Jefe extends Thread {
	private Switch light;
	private int contador = N_PRESOS;

	public Jefe (Switch light) {
	    this.light = light;
	}

	public void run(){
	    while (contador > 0){
		try {
		    System.out.println("BOSS Acquire");
		    sem.acquire(); //Acceso a sección crítica
		    System.out.println("BOSS Getted");
		}
		catch (Throwable e) {
		    System.out.println("Error " + e.getMessage());
		    e.printStackTrace();
		}
		
		if (!light.get()){ //Si el switch está apagado
		    light.throw_on();
		    contador--;
		    System.out.println("Quedan " + contador + " presos");

		}
		
		try {
		    sem.release(); //Salida de sección crítica
		    System.out.println("BOSS OUT");

		}
		catch (Throwable e) {
		    System.out.println("Error " + e.getMessage());
		    e.printStackTrace();
		}
	    
	    }
	    Presos.fin = true;
	    System.out.println("Ya lo hemos visitado todos");
	}

    }
    
    static class Preso extends Thread {
	private Switch light;
	private boolean done = false;
	private int id;
	
	public Preso (Switch light, int id) {
	    this.light = light;
	    this.id = id;

	}

	public void run() {
	    while (!Presos.fin){
		try {
		    System.out.println("[" + id + "]" + " Acquire");
		    sem.acquire(); //Acceso a sección crítica
		    System.out.println("[" + id + "]" + " Getted");
		}
		catch (Throwable e) {
		    System.out.println("Error " + e.getMessage());
		    e.printStackTrace();
		}

		if (light.get() && !done){
		    light.throw_off();
		    done = true;
		}

		try {
		    sem.release();		
		}
		catch (Throwable e) {
		    System.out.println("Error " + e.getMessage());
		    e.printStackTrace();
		}
	    }
	}
    }
}
    
