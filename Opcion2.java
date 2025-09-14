import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Opcion2 {
    public static void ejecutarOpcion2(int NPROC, int NMARCOS) {
        //Acá se guardarán los procesos y sus datos
        ArrayList<Proceso> procesos = new ArrayList<>();
        int TP = 0;
        int marcoActual = 0;
        int numMarcosPorProceso = NMARCOS / NPROC;
        System.out.println("Inicio:");
        //Cargar archivos
        for (int i = 0; i < NPROC; i++) {
            String nombreArchivo = "proc"+i+".txt";
            System.out.println("PROC "+i+" == Leyendo archivo de configuración ==");
            int NF = 0;
            int NC = 0;
            int NR = 0;
            int NP = 0;

            int paginaVirtualM1=0;
            int paginaVirtualM2=0;
            int paginaVirtualM3=0;
            int desplazamientoM1=0;
            int desplazamientoM2=0;
            int desplazamientoM3=0;
            
            try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
                String linea;
                int lineaActual = 0;
                while ((linea = br.readLine()) != null) {
                    
                    if (linea.startsWith("TP =")) {
                        TP = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC "+i+ " leyendo TP. Tam Páginas: "+TP);
                    } else if (linea.startsWith("NF =")) {
                        NF = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC "+i+ " leyendo NF. Num Filas: "+NF);
                    } else if (linea.startsWith("NC =")) {
                        NC = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC "+i+ " leyendo NC. Num Columnas: "+NC);
                    } else if (linea.startsWith("NR =")) {
                        NR = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC "+i+ " leyendo NR. Num Referencias: "+NR);
                    }else if (linea.startsWith("NP =")) {
                        NP = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC "+i+ " leyendo NP. Num Páginas: "+NP);
                    }
                    
                    if (lineaActual == 5) {
                        paginaVirtualM1 = Integer.parseInt(linea.split(",")[1].trim());
                        desplazamientoM1 = Integer.parseInt(linea.split(",")[2].trim());
                    } else if (lineaActual == 6) {
                        paginaVirtualM2 = Integer.parseInt(linea.split(",")[1].trim());
                        desplazamientoM2 = Integer.parseInt(linea.split(",")[2].trim());
                    } else if (lineaActual == 7){
                        paginaVirtualM3 = Integer.parseInt(linea.split(",")[1].trim());
                        desplazamientoM3 = Integer.parseInt(linea.split(",")[2].trim());
                    }
                    lineaActual++;
                }
                System.out.println("PROC "+i+" == Terminó de leer archivo de configuración ==");

            int marcoInicial = marcoActual;
            int marcoFinal = marcoInicial + numMarcosPorProceso;
            for (; marcoActual < marcoFinal; marcoActual++) {
                System.out.println("Proceso "+i+": recibe marco "+marcoActual);
            }

                Proceso proceso = new Proceso(NR, paginaVirtualM1, paginaVirtualM2, paginaVirtualM3, desplazamientoM1, desplazamientoM2, desplazamientoM3, marcoInicial, marcoFinal-1);
                procesos.add(proceso);
                
            } catch (IOException e) {
                System.out.print("No se ha realizado la opción 1; no hay archivos existentes.");
            }
        }

        //Imprimir resultados
        try (PrintWriter writer = new PrintWriter(new FileWriter("Opcion2Salida.txt"))) {
            for (int i = 0; i < NPROC; i++) {
                Proceso p = procesos.get(i);
                writer.println("Proceso: "+i);
                writer.println("- Num referencias: "+p.numReferencias);
                writer.println("- Fallas: "+p.fallas);
                writer.println("- Hits: "+p.hits);
                writer.println("- SWAP: "+p.swap);
                writer.println("- Tasa fallas: "+p.calcularTasaFallas());
                writer.println("- Tasa éxito: "+p.calcularTasaExito());
            }
        } catch (IOException e) {
                e.printStackTrace();
                }
        


        
        System.out.println("Opción 2 ejecutada.");
    }
    
}
