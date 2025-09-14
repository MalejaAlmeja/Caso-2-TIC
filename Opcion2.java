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
        Queue<Integer> matrices = new LinkedList<>();
        long[] contadoresLRU = new long[NMARCOS]; //sirve para hacer LRU cada índice es un marco, entonces los valores son los contadores de cada marco
        int[] marcosAsignados = new int[NMARCOS]; //sirve para saber que procesos tiene asignado un marco, el índice es el marco
        int[] dvEnProceso = new int[NPROC]; //sirve para saber que dv esta procesando cada proceso, el indice es el proceso
        int[] referenciasProcesadas = new int[NPROC]; //sirve para saber el número de línea
        int[] marcosCargados = new int[NMARCOS]; //sirve para saber si un marco esta lleno o no
        int[] paginasEnMarcos = new int[NMARCOS];
        Arrays.fill(marcosCargados,-1); //-1 significa que no hay nada cargado ahí
        int TPglobal = 0; 
       
        int[] offset = new int[NPROC];
        offset[0]=0;
        matrices.add(1);
        
        
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
                    //System.out.println(linea);
                    List<String> referencia = Arrays.asList(linea.split(","));
                    int pagina = Integer.parseInt(referencia.get(1).trim());
                    int desplazamiento = Integer.parseInt(referencia.get(2).trim());
                    listaDV.add(pagina*TP+desplazamiento);
                    // for (int d = pagina*TP;d<=(pagina*TP+desplazamiento);d++)
                    // {
                    //     listaDV.add(d);
                    // }
                    
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // for (int d=0;d<NC*NF*4*3;d++)
            // {
            //     listaDV.add(d);
            // }
            for (int j=i*marcosProceso;j<i*marcosProceso+marcosProceso;j++)
            {
                
                marcosAsignados[j] = i;
                System.out.println("Proceso "+i+": recibe marco "+j);
                
            }
            if (i>1)
            {
                offset[i]=offset[i-1];
            }
            else if(i==1)
            {
                offset[i] = procesos.get(0).getNP()*TP;
            }
            
            TPglobal = TP;
            System.out.println("PROC "+i+"== Terminó de leer archivo de configuración ==");
            Proceso proc_i = new Proceso( NF, NC, NR, NP, listaDV);
            dvEnProceso[i]=listaDV.get(0);
            referenciasProcesadas[i] = dvEnProceso[i]/4; //4 por que son enteros
            //System.out.println(dvEnProceso[i]);
            procesos.put(i, proc_i);
            turnos.add(i);

        }
        

        System.out.println("\n Simulación:");
        boolean[] huboFallosPagina = new boolean[NPROC];
        Arrays.fill(huboFallosPagina,false);
        int dvLocalEnProceso= 0;
        int ciclo = 0;
        int indice = 0;
        while (!turnos.isEmpty())
        {
            int i = turnos.poll();
            if (ciclo<6)
            {
                if (ciclo%2==0)
                {
                    indice++;
                }
                ciclo+=1;
                
            }
            else
            {
                ciclo=0;
                indice=0;
                dvLocalEnProceso++;
            }
            
            System.out.println("Turno proc: "+i);
            Proceso proc_i = procesos.get(i);
            System.out.println("PROC "+i+" analizando linea_: "+referenciasProcesadas[i]);
            //int marcoActual = (TPglobal*marcosProceso*i+dvEnProceso[i])/TPglobal;
            int paginaActual = (dvEnProceso[i])/(TPglobal);
            //Análisis
            int[] tablaPaginas = proc_i.getTP();
            boolean falloPagina = false;
            boolean hit = false;
            
            //if (tablaPaginas[paginaActual]==-1)
            if (tablaPaginas[paginaActual]==-1)
            {
                falloPagina = true;
                ciclo=ciclo-1;
                if (ciclo%2==0)
                {
                    indice = indice-1;
                }
                
                
            }
            else
            {
                if (!huboFallosPagina[i])
                {
                    proc_i.hits+=1;
                    hit = true;
                }
                else{
                    huboFallosPagina[i]=false;
                }
                
                
            }
            if (falloPagina)
            {
                huboFallosPagina[i]=true;
                proc_i.fallas++;
                System.out.println("PROC "+i+" falla de pag: "+paginaActual);
                int marcoLibre = -1;
                for (int j=0;j<NMARCOS;j++)
                {
                    if (marcosAsignados[j] == i && marcosCargados[j]==-1)
                    {
                        marcoLibre = j;
                    }
                }
                if (marcoLibre!=-1)
                {
                    //fallo de página sin reemplazo
                    
                    //tablaPaginas[paginaActual] =marcoLibre;
                    tablaPaginas[paginaActual] = marcoLibre;
                    paginasEnMarcos[marcoLibre] = (offset[i]/TPglobal)+paginaActual;
                    marcosCargados[marcoLibre] =i;
                    proc_i.updateTP(tablaPaginas);
                    turnos.add(i);
                    proc_i.swaps+=1;
                
                }
                else
                {
                    //fallo de página con reemplazo
                    
                    long minimo = Long.MAX_VALUE;
                    int marcoAReemplazar = -1;
                    for (int j = 0; j < contadoresLRU.length; j++) {
                        if (contadoresLRU[j] < minimo) {
                            minimo = contadoresLRU[j];
                            marcoAReemplazar = j;
                        }
                    }

                    // tablaPaginas[paginaActual] = tablaPaginas[marcoAReemplazar];
                    // tablaPaginas[marcoAReemplazar] = -1;
                    
                    //tablaPaginas[(offset[i]/TPglobal)+paginaActual] = tablaPaginas[paginasEnMarcos[marcoAReemplazar]];
                    tablaPaginas[paginaActual] = marcoAReemplazar;
                    tablaPaginas[paginasEnMarcos[marcoAReemplazar] - offset[i]/TPglobal] = -1;
                    paginasEnMarcos[marcoAReemplazar] = (offset[i]/TPglobal)+paginaActual;
                    //tablaPaginas[paginasEnMarcos[marcoAReemplazar]] = -1;
                    marcosCargados[marcoAReemplazar] =i;
                    proc_i.updateTP(tablaPaginas);
                    turnos.add(i);
                    proc_i.swaps+=2;

                }
            }
            if (hit)
            {
                System.out.println("PROC "+i+" hits: "+proc_i.hits);
            }

            //LRU
            System.out.println("PROC "+i+" Envejecimiento"); // como se corren los bits de los anteriores 'ticks' se dice que envejece
            for (int j= 0;j<NMARCOS;j++)
            {
                
                contadoresLRU[j] >>= 1;
                
                //if (j==((TPglobal*marcosProceso*i+dvEnProceso[i])/TPglobal)) //Esta opción no sirve si más adelante se asignan los marcos a procesos con más fallas.
                if (j==(tablaPaginas[paginaActual]))
                {
                    contadoresLRU[j] |= (1L<<63); //Esto lo que hace es básicamente registrar el acceso en este 'tick de reloj'
                }
            }
            
            if (referenciasProcesadas[i] < proc_i.getNR() - 1) {
                if (!falloPagina) {
                    turnos.add(i);
                    referenciasProcesadas[i]++;
                    dvEnProceso[i] = proc_i.getListaDV().get(referenciasProcesadas[i]);
                }
            } else {
                System.out.println("=====================================");
                System.out.println("Termino PROC " + i);
                System.out.println("=====================================");
                Integer maxFallas = Integer.MIN_VALUE;
                Integer procesoConMasFallas = -1;
                for (int p = 0; p<NPROC ;p++)
                {
                    if (procesos.get(p).fallas>maxFallas)
                    {
                        procesoConMasFallas = p;
                        maxFallas = procesos.get(p).fallas;
                        
                    }
                }
                for (int marco = 0; marco<NMARCOS;marco++)
                {
                    
                    if (marcosAsignados[marco]==i)
                    {
                        System.out.println("PROC "+i+" removiendo marco: "+marco);
                        System.out.println("PROC "+i+" asignando marco nuevo: "+marco);
                        marcosAsignados[marco] = procesoConMasFallas;
                        
                    }

                }
            }
           

            







        }

        for (int i = 0; i<NPROC;i++)
        {
            System.out.println("-----------------------------");
            System.out.println("PROCESO: "+i);
            Proceso proc_i = procesos.get(i);
            System.out.println("- Num referencias: "+proc_i.getNR());
            System.out.println("- Fallas : "+proc_i.fallas);
            System.out.println("- Hits : "+(proc_i.hits));
            System.out.println("- SWAPS : "+(proc_i.swaps));
            double tasaFallas = (double) proc_i.fallas / proc_i.getNR();
            System.out.println("- Tasa fallas: "+String.format("%.4f", tasaFallas));
            double tasaExitos = (double) (proc_i.hits) / proc_i.getNR();
            System.out.println("- Tasa éxito: "+String.format("%.4f", tasaExitos));
        }

        System.out.println("Opción 2 ejecutada.");
    }
    
}
