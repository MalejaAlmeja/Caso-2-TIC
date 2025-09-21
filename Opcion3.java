import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Opcion3 {
    public static void ejecutarOpcion2(int NPROC, int NMARCOS) {

        // --- Estructuras globales ---
        HashMap<Integer, Proceso> procesos = new HashMap<>();
        int marcosProceso = NMARCOS / NPROC;
        Queue<Integer> turnos = new LinkedList<>();

        long[] contadoresLRU = new long[NMARCOS];     // LRU por marco físico
        int[] marcosAsignados = new int[NMARCOS];     // marco físico -> id de proceso dueño del marco (reemplazo local)
        int[] marcosCargados = new int[NMARCOS];      // -1 libre, o id de proceso si está ocupado
        int[] paginasEnMarcos = new int[NMARCOS];     // marco físico -> página (virtual del proceso)
        int[] referenciasProcesadas = new int[NPROC]; // índice de referencia actual por proceso

        Arrays.fill(contadoresLRU, 0L);
        Arrays.fill(marcosAsignados, -1);
        Arrays.fill(marcosCargados, -1);
        Arrays.fill(paginasEnMarcos, -1);
        Arrays.fill(referenciasProcesadas, 0);

        int TPglobal = 0; // tamaño de página en BYTES (se fija una vez)

        System.out.println("Inicio:");
        System.out.println("Marcos totales = " + NMARCOS + ", procesos = " + NPROC + ", marcos/proceso = " + marcosProceso);

        // --- Carga de configuración por proceso ---
        for (int i = 0; i < NPROC; i++) {
            System.out.println("PROC " + i + " == Leyendo archivo de configuración ==");

            int TP = 0; // bytes
            int NF = 0, NC = 0, NR = 0, NP = 0;

            ArrayList<Integer> listaDV = new ArrayList<>();
            String archivoConfig = "proc" + i + ".txt";

            try (BufferedReader br = new BufferedReader(new FileReader(archivoConfig))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.startsWith("TP")) {
                        TP = Integer.parseInt(linea.split("=")[1].trim()); // BYTES
                        System.out.println("PROC " + i + " leyendo TP (bytes): " + TP);
                    } else if (linea.startsWith("NF")) {
                        NF = Integer.parseInt(linea.split("=")[1].trim());
                    } else if (linea.startsWith("NC")) {
                        NC = Integer.parseInt(linea.split("=")[1].trim());
                    } else if (linea.startsWith("NR")) {
                        NR = Integer.parseInt(linea.split("=")[1].trim());
                        System.out.println("PROC " + i + " leyendo NR: " + NR);
                    } else if (linea.startsWith("NP")) {
                        NP = Integer.parseInt(linea.split("=")[1].trim());
                    } else {
                        // Formato: "... , <pagina> , <desplazamiento_bytes> , <r/w>"
                        List<String> ref = Arrays.asList(linea.split(","));
                        int pagina = Integer.parseInt(ref.get(1).trim());
                        int desplazamiento = Integer.parseInt(ref.get(2).trim()); // BYTES (múltiplos de 4)
                        int dv = pagina * TP + desplazamiento; // DV en BYTES
                        listaDV.add(dv);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Fijar/validar TPglobal una sola vez
            if (i == 0) {
                TPglobal = TP;
            } else if (TP != TPglobal) {
                throw new IllegalArgumentException("Todos los procesos deben tener el mismo TP (bytes). Encontrado: " + TP + " != " + TPglobal);
            }

            // Asignación local de marcos para el proceso i (bloque contiguo):
            int inicio = i * marcosProceso;
            int fin = inicio + marcosProceso;
            for (int j = inicio; j < fin; j++) {
                marcosAsignados[j] = i;
                // marcosCargados[j] y paginasEnMarcos[j] ya están en -1
                System.out.println("Proceso " + i + ": recibe marco " + j);
            }

            // Crear/Inicializar proceso
            Proceso proc_i = new Proceso(NF, NC, NR, NP, listaDV);
            int[] tpInit = proc_i.getTP();
            Arrays.fill(tpInit, -1); // tabla de páginas vacía
            proc_i.updateTP(tpInit);

            procesos.put(i, proc_i);
            turnos.add(i);
        }

        // --- Simulación ---
        System.out.println("\nSimulación:");
        while (!turnos.isEmpty()) {
            int i = turnos.poll();
            Proceso proc_i = procesos.get(i);
            if (proc_i == null) continue;

            int refIdx = referenciasProcesadas[i];
            if (refIdx >= proc_i.getNR()) continue; // nada que procesar

            // DV actual -> página actual
            int dv = proc_i.getListaDV().get(refIdx); // BYTES
            int paginaActual = dv / TPglobal;

            // Tabla de páginas del proceso (page -> marco físico | -1 vacío)
            int[] tablaPaginas = proc_i.getTP();

            boolean fallo = (tablaPaginas[paginaActual] == -1);
            if (fallo) {
                proc_i.fallas++;

                // 1) Buscar marco libre del proceso i
                int marcoLibre = -1;
                for (int j = 0; j < NMARCOS; j++) {
                    if (marcosAsignados[j] == i && marcosCargados[j] == -1) {
                        marcoLibre = j;
                        break; // usa el primer libre
                    }
                }

                if (marcoLibre != -1) {
                    // Carga SIN reemplazo
                    tablaPaginas[paginaActual] = marcoLibre;
                    paginasEnMarcos[marcoLibre] = paginaActual;
                    marcosCargados[marcoLibre] = i;
                    contadoresLRU[marcoLibre] = 0L; // recién usada
                    proc_i.swaps += 1;
                } else {
                    // 2) Reemplazo local con LRU (menor contador)
                    long minimo = Long.MAX_VALUE;
                    int marcoAReemplazar = -1;
                    for (int j = 0; j < NMARCOS; j++) {
                        if (marcosAsignados[j] == i && contadoresLRU[j] < minimo) {
                            minimo = contadoresLRU[j];
                            marcoAReemplazar = j;
                        }
                    }
                    // Invalidar víctima si existía
                    int victima = paginasEnMarcos[marcoAReemplazar];
                    if (victima >= 0) {
                        tablaPaginas[victima] = -1;
                    }

                    // Mapear nueva página
                    tablaPaginas[paginaActual] = marcoAReemplazar;
                    paginasEnMarcos[marcoAReemplazar] = paginaActual;
                    marcosCargados[marcoAReemplazar] = i;
                    contadoresLRU[marcoAReemplazar] = 0L; // recién usada
                    proc_i.swaps += 2;
                }

                // Persistir cambios en TP del proceso
                proc_i.updateTP(tablaPaginas);

            } else {
                // HIT
                proc_i.hits++;
            }

            // Aging SOLO de marcos del proceso i
            for (int j = 0; j < NMARCOS; j++) {
                if (marcosAsignados[j] == i) {
                    contadoresLRU[j] >>= 1;
                }
            }
            // Marca del uso en este tick (MSB=1) si la página está mapeada
            int marcoAccedido = tablaPaginas[paginaActual];
            if (marcoAccedido != -1) {
                contadoresLRU[marcoAccedido] |= (1L << 63);
            }

            // Avanzar SIEMPRE a la siguiente referencia (no reintentar la misma)
            referenciasProcesadas[i] = refIdx + 1;

            // Re-encolar si quedan referencias
            if (referenciasProcesadas[i] < proc_i.getNR()) {
                turnos.add(i);
            } else {
                System.out.println("=====================================");
                System.out.println("Termino PROC " + i);
                System.out.println("=====================================");

                // (Opcional) Reasignación de marcos al proceso con más fallas
                int procesoConMasFallas = -1;
                int maxFallas = Integer.MIN_VALUE;
                for (int p = 0; p < NPROC; p++) {
                    Proceso px = procesos.get(p);
                    if (px != null && px.fallas > maxFallas) {
                        maxFallas = px.fallas;
                        procesoConMasFallas = p;
                    }
                }
                for (int marco = 0; marco < NMARCOS; marco++) {
                    if (marcosAsignados[marco] == i) {
                        System.out.println("PROC " + i + " removiendo marco: " + marco);
                        System.out.println("PROC " + procesoConMasFallas + " asignando marco nuevo: " + marco);
                        marcosAsignados[marco] = procesoConMasFallas;
                        marcosCargados[marco] = -1;
                        paginasEnMarcos[marco] = -1;
                        contadoresLRU[marco] = 0L;
                    }
                }

                // Limpia la TP del proceso (solo para inspección/post-mortem)
                Arrays.fill(tablaPaginas, -1);
                proc_i.updateTP(tablaPaginas);
            }
        }

        // --- Reporte final ---
        try (PrintWriter writer = new PrintWriter(new FileWriter("Salida.txt"))) {
            for (int i = 0; i < NPROC; i++) {
                writer.println("-----------------------------");
                writer.println("PROCESO: " + i);
                Proceso proc_i = procesos.get(i);
                writer.println("- Num referencias: " + proc_i.getNR());
                writer.println("- Fallas : " + proc_i.fallas);
                writer.println("- Hits : " + (proc_i.hits));
                writer.println("- SWAPS : " + (proc_i.swaps));
                double tasaFallas = (double) proc_i.fallas / proc_i.getNR();
                writer.println("- Tasa fallas: " + String.format("%.4f", tasaFallas));
                double tasaExitos = (double) (proc_i.hits) / proc_i.getNR();
                writer.println("- Tasa éxito: " + String.format("%.4f", tasaExitos));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
