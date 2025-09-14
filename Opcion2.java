import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Opcion2 {
    public static void ejecutarOpcion2(int NPROC, int NMARCOS) {

        HashMap<Integer,Proceso> procesos = new HashMap<Integer, Proceso>();
        int marcosProceso = NMARCOS/NPROC;
        Queue<Integer> turnos = new LinkedList<>();
        long[] contadoresLRU = new long[NMARCOS];
        int[] marcosAsignados = new int[NMARCOS];
        int[] dvEnProceso = new int[NPROC];
        int[] referenciasProcesadas = new int[NPROC];
        int[] marcosCargados = new int[NMARCOS];
        Arrays.fill(marcosCargados,-1); //-1 significa que no hay nada cargado ahí
        int TPglobal = 0; 
        
        System.out.println("Inicio:");
        for (int i = 0; i < NPROC ; i++)
        {
            System.out.println("PROC "+i+" == Leyendo archivo de configuración ==");
            int TP = 0;
            int NF = 0;
            int NC = 0;
            int NR = 0;
            int NP = 0;
            ArrayList<Integer> listaDV = new ArrayList<Integer>();
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
                else{
                    List<String> referencia = Arrays.asList(linea.split(","));
                    int dv = Integer.parseInt(referencia.get(1).trim())*TP+Integer.parseInt(referencia.get(2).trim());
                    listaDV.add(dv);
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int j=i*marcosProceso;j<i*marcosProceso+marcosProceso;j++)
            {
                
                marcosAsignados[j] = i;
                System.out.println("Proceso "+i+": recibe marco "+j);
            }
            TPglobal = TP;
            System.out.println("PROC "+i+"== Terminó de leer archivo de configuración ==");
            Proceso proc_i = new Proceso(NMARCOS, NF, NC, NR, NP, listaDV);
            dvEnProceso[i]=listaDV.get(0);
            referenciasProcesadas[i] = dvEnProceso[i]/4; //4 por que son enteros
            System.out.println(dvEnProceso[i]);
            procesos.put(i, proc_i);
            turnos.add(i);

        }
        

        System.out.println("\n Simulación:");
        
        while (!turnos.isEmpty())
        {
            
            int i = turnos.poll();
            System.out.println("Turno proc: "+i);
            Proceso proc_i = procesos.get(i);
            System.out.println("PROC "+i+" analizando linea_: "+referenciasProcesadas[i]);
            int paginaActual = (TPglobal*marcosProceso*i+dvEnProceso[i])/TPglobal;
            //Análisis
            int[] tablaPaginas = proc_i.getTP();
            boolean falloPagina = false;
            boolean hit = false;
            if (tablaPaginas[paginaActual]==-1)
            {
                falloPagina = true;
            }
            else
            {
                hit = true;
            }
            if (falloPagina)
            {
                
            }
            if (hit)
            {
                System.out.println("PROC "+i+" hits: ");
            }

            //LRU
            System.out.println("PROC "+i+" Envejecimiento"); // como se corren los bits de los anteriores 'ticks' se dice que envejece
            for (int j= 0;j<NMARCOS;j++)
            {
                
                contadoresLRU[j] >>= 1;
                if (j==((TPglobal*marcosProceso*i+dvEnProceso[i])/TPglobal))
                {

                    contadoresLRU[j] |= (1L<<63); //Esto lo que hace es básicamente registrar el acceso en este 'tick de reloj'
                }
                
            }
            if ((dvEnProceso[i]+1)<proc_i.getListaDV().size())
            {
                turnos.add(i);
                dvEnProceso[i] ++;

            }
            referenciasProcesadas[i]= dvEnProceso[i];
           

            







        }

        System.out.println("Opción 2 ejecutada.");
    }
    
}
