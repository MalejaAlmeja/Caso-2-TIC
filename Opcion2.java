//Juan José Cortes - 20232
// Maria Alejandra Carrillo - 202321854
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
        ArrayList<ArrayList<Long>> contadoresLRU = new ArrayList<>(); 
        long tickActual = 0;
        ArrayList<Proceso> procesosTerminados = new ArrayList<>();
        int TP = 0;
        int marcoActual = 0;
        int numMarcosPorProceso = NMARCOS / NPROC;
        int procesoConMasFallosAtras = -1;
        boolean matricesIguales = false;
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

                Proceso proceso = new Proceso(NC, NF, i, NR, marcoInicial, marcoFinal-1, listaDireccionesDV);
                procesos.add(proceso);
                ArrayList<Long> usoInicial = new ArrayList<>();
                for (int j = 0; j < proceso.tablaPaginas.size(); j++) {
                    usoInicial.add(0L);
                }
                contadoresLRU.add(usoInicial);
                
            } catch (IOException e) {
                System.out.print("No se ha realizado la opción 1; no hay archivos existentes.");
            }
        }

        int numColumnasAnterior = procesos.get(0).NC;
        int numFilasAnterior = procesos.get(0).NF;
        for (Proceso p:procesos){
            if (p.NC != numColumnasAnterior || p.NF != numFilasAnterior){
                matricesIguales = false;
                break;
            }
            else{
                matricesIguales = true;
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
            else{
            for (Proceso p:procesos){
                if (procesoConMasFallosAtras==p.numeroProceso){
                    for (int k=0; k<numMarcosPorProceso; k++){
                        p.tablaPaginas.add(-1);
                    }
                    procesoConMasFallosAtras = -1;
                }
                if (p.listaDireccionesDV.isEmpty()) {
                    if (!procesosTerminados.contains(p)) {
                        System.out.println("=========================");
                        System.out.println("Termino proc:" + p.numeroProceso);
                        System.out.println("=========================");
                        procesosTerminados.add(p);
                    }
                    // Liberar marcos
                    int marcoInicialLiberado = p.marcoInicial;
                    int marcoFinalLiberado = p.marcoFinal;
                    for (int i=p.marcoInicial; i <= p.marcoFinal; i++) {
                        System.out.println("PROC "+p.numeroProceso+": removiendo marco "+i);
                    }
                    procesos.remove(p);
                    if (!procesos.isEmpty()) {
                        Proceso procesoMasFallos = procesos.get(0);
                        for (Proceso p2 : procesos) {
                            if (p2.fallas > procesoMasFallos.fallas) {
                                procesoMasFallos = p2;
                            }
                        }
                        for (int i = marcoInicialLiberado; i <= marcoFinalLiberado; i++) {
                            System.out.println("PROC " + procesoMasFallos.numeroProceso + ": asignando marco nuevo " + i);
                            procesoConMasFallosAtras = procesoMasFallos.numeroProceso;
                            contadoresLRU.get(procesoMasFallos.numeroProceso).add(0L);
                        }
                        System.out.println("tabla paginas");
                        System.out.println(p.tablaPaginas);
                    }
                    break; 
                }
                else{
                tickActual++;
                ArrayList<String> direccion = p.listaDireccionesDV.remove(0);
                int pagina = Integer.parseInt(direccion.get(0));
                int desplazamiento = Integer.parseInt(direccion.get(1));
                String rw = direccion.get(2);
                
                //ArrayList<Long> usoProceso = contadoresLRU.get(p.numeroProceso);
                System.out.println("PROC "+p.numeroProceso+" envejecimiento");
                // for (int i = 0; i < contadoresLRU.get(p.numeroProceso).size(); i++) {
                //     contadoresLRU.get(p.numeroProceso).set(i, contadoresLRU.get(p.numeroProceso).get(i) >> 1);
                // }
                // Verificar si la página está en la tabla de páginas
                if (p.tablaPaginas.contains(pagina)) {
                    p.registrarHit();
                    System.out.println("PROC "+p.numeroProceso+" hits: "+p.hits);
                    int indicePagina = p.tablaPaginas.indexOf(pagina);
                    contadoresLRU.get(p.numeroProceso).set(indicePagina, tickActual);
                    //contadoresLRU.get(p.numeroProceso).set(indicePagina, contadoresLRU.get(p.numeroProceso).get(indicePagina) | (1L<<63));
                } else {
                    p.registrarFalla();
                    System.out.println("PROC "+p.numeroProceso+" falla de pag: "+pagina);
                    
                    if (p.tablaPaginas.contains(-1)) {
                        int indiceLibre = p.tablaPaginas.indexOf(-1);
                        p.tablaPaginas.set(indiceLibre, pagina);
                        contadoresLRU.get(p.numeroProceso).set(indiceLibre, tickActual);
                        //contadoresLRU.get(p.numeroProceso).set(indiceLibre, 0L);
                        p.registrarSwap();
                    } else {
                        ArrayList<Long> usoProceso = contadoresLRU.get(p.numeroProceso);
                        long minUso = Long.MAX_VALUE;
                        int indiceARemplazar = -1;

                        for (int i = 0; i < p.tablaPaginas.size(); i++) {
                            if (contadoresLRU.get(p.numeroProceso).get(i) < minUso) {
                                minUso = contadoresLRU.get(p.numeroProceso).get(i);
                                indiceARemplazar = i;
                            }
                        }
                        System.out.println("PROC "+p.numeroProceso+" reemplazando página "+p.tablaPaginas.get(indiceARemplazar)+" por "+pagina);
                        p.tablaPaginas.set(indiceARemplazar, pagina);
                        usoProceso.set(indiceARemplazar, tickActual);
                        //contadoresLRU.get(p.numeroProceso).set(indiceARemplazar, 0L);
                        p.registrarSwap();
                        p.registrarSwap();
                    }
                    
                }
                }
            
        
            }}

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

                        