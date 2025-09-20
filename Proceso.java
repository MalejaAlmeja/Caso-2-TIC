
import java.util.ArrayList;

public class Proceso {
    int numeroProceso;
    int numReferencias;
    int NF;
    int NC;
    int fallas;
    int hits;
    int swap;
    double tasaFallas;
    double tasaExito;
    int marcoInicial;
    int marcoFinal;

    ArrayList<Integer> tablaPaginas = new ArrayList<>();
    ArrayList<ArrayList<String>> listaDireccionesDV = new ArrayList<>();

    
    // Constructor
    public Proceso(int nc, int nf, int np, int nr, int mi, int mf, ArrayList<ArrayList<String>> listaDirecciones) {
        this.NC = nc;
        this.NF = nf;
        this.numeroProceso = np;
        this.numReferencias = nr;
        this.marcoInicial = mi;
        this.marcoFinal = mf;
        this.listaDireccionesDV = listaDirecciones;

        for (int i = 0; i < (mf - mi + 1); i++) {
            tablaPaginas.add(-1); // Inicializa la tabla de páginas con -1 (indica que no hay página cargada)
        }
    }



    // Métodos para actualizar estadísticas
    public void registrarFalla() {
        this.fallas++;
    }
    public void registrarSwap() {
        this.swap++;
    }
    public void registrarSwapDoble() {
        this.swap += 2;
    }

    public void registrarHit() {
        this.hits++;
    }
    public void calcularTasaFallas() {
        this.tasaFallas = (double) this.fallas / (double) this.numReferencias;
        this.tasaFallas = Math.round(this.tasaFallas * 10000.0) / 10000.0;
    }
    public void calcularTasaExito() {
        this.tasaExito = (double) this.hits / (double) this.numReferencias;
        this.tasaExito = Math.round(this.tasaExito * 10000.0) / 10000.0;
    }


	
}
