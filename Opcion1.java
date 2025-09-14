import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Opcion1 {
    public static void ejecutarOpcion1(String archivoConfig) {

        
        int TP = 0;
        int NPROC = 0;
        String TAMS = "";

        try (BufferedReader br = new BufferedReader(new FileReader(archivoConfig))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("TP=")) {
                    TP = Integer.parseInt(linea.split("=")[1].trim());
                } else if (linea.startsWith("NPROC=")) {
                    NPROC = Integer.parseInt(linea.split("=")[1].trim());
                } else if (linea.startsWith("TAMS=")) {
                    TAMS = linea.split("=")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        


        List<String> TAMSa = Arrays.asList(TAMS.split(","));
        for (int i = 0;i<NPROC;i++)
        {
            
            List<String> dimensions = Arrays.asList(TAMSa.get(i).split("x"));
            int NF = Integer.parseInt(dimensions.get(0));
            int NC = Integer.parseInt(dimensions.get(1));
            int tamañoEnBytesXMatriz = NF*NC*4; //Bytes que ocupa la matriz del proceso i
            int tamañoNecesario = tamañoEnBytesXMatriz*3; //Bytes necesarios para guardar las tres matrices en row-major orden
            int NR = NF*NC*3; //Número de enteros (variables) que usa el proceso; son tres matrices
            int NP = (int) Math.ceil((double) tamañoNecesario/TP); //Número de páginas que necesita el proceso.
            int M1PTR = 0;
            int M2PTR = tamañoEnBytesXMatriz+M1PTR;
            int M3PTR = tamañoEnBytesXMatriz+M2PTR;
            String nombreArchivo = "proc" + i + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
 
                writer.println("TP = "+TP);
                writer.println("NF = "+NF);
                writer.println("NC = "+NC);
                writer.println("NR = "+NR);
                writer.println("NP = "+NP);
                boolean recorrida = false;
                int filActual = 0;
                int colActual = 0;
                while (!recorrida)
                {
                    // System.out.println(M1PTR + filActual*colActual*4);
                    // System.out.println((M2PTR + filActual*colActual*4));
                    // System.out.println((M3PTR + filActual*colActual*4));
                    int PcorrespondienteM1 = (M1PTR + filActual*NC*4+colActual*4)/TP;
                    int PcorrespondienteM2 = (M2PTR + filActual*NC*4+colActual*4)/TP;
                    int PcorrespondienteM3 = (M3PTR + filActual*NC*4+colActual*4)/TP;
                    int DcorrespondienteM1 = (M1PTR + filActual*NC*4+colActual*4)%TP;
                    int DcorrespondienteM2 = (M2PTR + filActual*NC*4+colActual*4)%TP;
                    int DcorrespondienteM3 = (M3PTR + filActual*NC*4+colActual*4)%TP;
                    writer.println("M1 : ["+ String.valueOf(filActual)+" - " +String.valueOf(colActual)+"], "+PcorrespondienteM1+", "+DcorrespondienteM1+", r");
                    writer.println("M2 : ["+ String.valueOf(filActual)+" - " +String.valueOf(colActual)+"], "+PcorrespondienteM2+", "+DcorrespondienteM2+", r");
                    writer.println("M3 : ["+ String.valueOf(filActual)+" - " +String.valueOf(colActual)+"], "+PcorrespondienteM3+", "+DcorrespondienteM3+", w");
                    if (colActual+1 >= NC)
                    {
                        if (!((filActual+1)>=NF))
                        {
                            filActual++;
                            colActual=0;
                        }
                        else
                        {
                            recorrida=true;
                        }
                    }
                    else{
                        colActual++;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        
        System.out.println("Opción 1 ejecutada.");
    }

    // public void sumarMatrices(int pnf, int pnc) {
    // int filas = pnf;
    // int columnas = pnc;

    // for (int i = 0; i < filas; i++) {
    //     for (int j = 0; j < columnas; j++) {
    //         matriz3[i][j] = matriz1[i][j] + matriz2[i][j];
    //     }
    // }
    //}
    
}


