public class Proceso {
    int numReferencias;
    int fallas;
    int hits;
    int swap;
    int paginaVirtualM1;
    int paginaVirtualM2;
    int paginaVirtualM3;
    int desplazamientoM1;
    int desplazamientoM2;
    int desplazamientoM3;
    int marcoInicial;
    int marcoFinal;

    
    // Constructor
    public Proceso(int nr, int pvm1, int pvm2, int pvm3, int dm1, int dm2, int dm3, int mi, int mf) {
        this.numReferencias = nr;
        this.paginaVirtualM1 = pvm1;
        this.paginaVirtualM2 = pvm2;
        this.paginaVirtualM3 = pvm3;
        this.desplazamientoM1 = dm1;
        this.desplazamientoM2 = dm2;
        this.desplazamientoM3 = dm3;
        this.marcoInicial = mi;
        this.marcoFinal = mf;
    }

    // Métodos para actualizar estadísticas
    public void registrarFalla() {
        this.fallas++;
        this.swap++;
    }
    public void registrarHit() {
        this.hits++;
    }
    public double calcularTasaFallas() {
        double tasaFallas = fallas / numReferencias;
        return Math.round(tasaFallas * 100.0) / 100.0;
    }
    public double calcularTasaExito() {
        double tasaExito = hits / numReferencias;
        return Math.round(tasaExito * 100.0) / 100.0;
    }


	
}
