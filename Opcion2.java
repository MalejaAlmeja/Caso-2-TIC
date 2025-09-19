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
        ArrayList<Proceso> procesosTerminados = new ArrayList<>();
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
            ArrayList<ArrayList<String>> listaDireccionesDV = new ArrayList<>();
            
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
                    else{
                        if (lineaActual >= 5) { 
                            String[] partes = linea.split(",");
                            ArrayList<String> direccion = new ArrayList<>();
                            direccion.add(partes[1].trim()); // Página
                            direccion.add(partes[2].trim()); // Desplazamiento
                            direccion.add(partes[3].trim()); // R/W
                            listaDireccionesDV.add(direccion);
                        }
                    }
                    
                    lineaActual++;
                }
                System.out.println("PROC "+i+" == Terminó de leer archivo de configuración ==");

            int marcoInicial = marcoActual;
            int marcoFinal = marcoInicial + numMarcosPorProceso;
            for (; marcoActual < marcoFinal; marcoActual++) {
                System.out.println("Proceso "+i+": recibe marco "+marcoActual);
            }

                Proceso proceso = new Proceso(i, NR, marcoInicial, marcoFinal-1, listaDireccionesDV);
                procesos.add(proceso);
                
            } catch (IOException e) {
                System.out.print("No se ha realizado la opción 1; no hay archivos existentes.");
            }
        }

        // Simulación de memoria
        System.out.println(" ");
        System.out.println("Simulación");
        boolean terminado = false;
        while (!terminado){
            if (procesos.isEmpty()){
                terminado = true;
                break;
            }

            for (Proceso p:procesos){
                if (p.listaDireccionesDV.isEmpty()){
                    System.out.println("=========================");
                    System.out.println("Termino proc:" + p.numeroProceso);
                    System.out.println("=========================");
                    procesos.remove(p);
                    procesosTerminados.add(p);
                    int marcoInicialLiberado= p.marcoInicial;
                    int marcoFinalLiberado= p.marcoFinal;
                    for (int i=p.marcoInicial; i < p.marcoFinal; i++) {
                    System.out.println("PROC "+p.numeroProceso+": removiendo marco "+i);
                    }
                    if (procesos.isEmpty()){
                        terminado = true;
                        break;
                    }
                    for (Proceso p2:procesos){
                        Proceso procesoMasFallos = procesos.get(0);
                        if (p2.fallas > procesoMasFallos.fallas){
                            procesoMasFallos = p2;
                        }
                        p2.marcoInicial = marcoInicialLiberado;
                        p2.marcoFinal = marcoFinalLiberado;
                        for (int i=p2.marcoInicial; i < p2.marcoFinal; i++) {
                        System.out.println("PROC "+p.numeroProceso+": asignando marco nuevo "+i);
                        }

                    }
                    
                }
                else{
                ArrayList<String> direccion = p.listaDireccionesDV.remove(0);
                int pagina = Integer.parseInt(direccion.get(0));
                int desplazamiento = Integer.parseInt(direccion.get(1));
                String rw = direccion.get(2);
                
                // Verificar si la página está en la tabla de páginas
                if (p.tablaPaginas.contains(pagina)) {
                    // Hit
                    p.registrarHit();
                    System.out.println("PROC "+p.numeroProceso+" hits: "+p.hits);
                } else {
                    // Falla de página
                    p.registrarFalla();
                    System.out.println("PROC "+p.numeroProceso+" falla de pag: "+pagina);
                    
                    // Buscar un marco libre
                    if (p.tablaPaginas.contains(-1)) {
                        int indiceLibre = p.tablaPaginas.indexOf(-1);
                        p.tablaPaginas.set(indiceLibre, pagina);
                        p.registrarSwap();
                    } else {
                        // No hay marcos libres LRU
                        p.tablaPaginas.add(pagina); 
                        p.registrarSwap();
                    }
                    
                }
                System.out.println("PROC "+p.numeroProceso+" envejecimiento");}
            
        
            }

        }

        //Imprimir resultados
        try (PrintWriter writer = new PrintWriter(new FileWriter("Salida.txt"))) {
            for (int i = 0; i < NPROC; i++) {
                Proceso p = procesosTerminados.get(i);
                p.calcularTasaFallas();
                p.calcularTasaExito();
                writer.println("Proceso: "+i);
                writer.println("- Num referencias: "+p.numReferencias);
                writer.println("- Fallas: "+p.fallas);
                writer.println("- Hits: "+p.hits);
                writer.println("- SWAP: "+p.swap);
                writer.println("- Tasa fallas: "+p.tasaFallas);
                writer.println("- Tasa éxito: "+p.tasaExito);
            }
        } catch (IOException e) {
                e.printStackTrace();
                }
        

    }}

                        