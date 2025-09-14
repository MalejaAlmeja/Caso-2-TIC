import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Opcion2 {
    public static void ejecutarOpcion2(int NPROC, int NMARCOS) {


        for (int i = 0; i < NPROC ; i++)
        {
            System.out.println("PROC "+i+" == Leyendo archivo de configuración ==");
            int TP = 0;
            int NF = 0;
            int NC = 0;
            int NR = 0;
            int NP = 0;
            String archivoConfig = "proc"+i+".txt";
            try (BufferedReader br = new BufferedReader(new FileReader(archivoConfig))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("TP")) {
                    TP = Integer.parseInt(linea.split("=")[1].trim());
                    System.out.println("PROC "+i+"leyendo TP. Tam Páginas: "+TP);
                } else if (linea.startsWith("NF")) {
                    NF = Integer.parseInt(linea.split("=")[1].trim());
                    System.out.println("PROC "+i+"leyendo NF. Num Filas: "+NF);
                } else if (linea.startsWith("NC")) {
                    NC = Integer.parseInt(linea.split("=")[1].trim());
                    System.out.println("PROC "+i+"leyendo NC. Num Cols: "+NC);
                } else if (linea.startsWith("NR")) {
                    NR = Integer.parseInt(linea.split("=")[1].trim());
                    System.out.println("PROC "+i+"leyendo NR. Num Referencias: "+NR);
                } else if (linea.startsWith("NP")) {
                    NP = Integer.parseInt(linea.split("=")[1].trim());
                    System.out.println("PROC "+i+"leyendo NP. Num Paginas: "+NP);
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("PROC "+i+"== Terminó de leer archivo de configuración ==");
        }
        
        System.out.println("Opción 2 ejecutada.");
    }
    
}
