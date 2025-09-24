//Juan José Cortes - 20232
// Maria Alejandra Carrillo - 202321854

import java.io.File;
import java.util.Scanner;

public class Consola {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        while (true)
        {
            System.out.println("Selecciona una opción:");
            System.out.println("1. Ejecutar opción 1");
            System.out.println("2. Ejecutar opcion 2");
            System.out.println("3. Borrar archivos proc_i.txt");
            System.out.println("4. Salir");
            System.out.print("Seleccionar: ");

            int opcion = sc.nextInt();

            if (opcion == 1) {
                System.out.print("nombreArchivo: ");
                String archivoConfig = sc.next();
                Opcion1.ejecutarOpcion1(archivoConfig);
            } else if (opcion == 2) {
                System.out.print("Número de procesos: ");
                int nproc = sc.nextInt();
                
                System.out.print("Número de marcos: ");
                int nmarcos = sc.nextInt();
                
                Opcion2.ejecutarOpcion2(nproc, nmarcos); 
            } 
            else if (opcion == 3) 
            {
                borrarArchivosProc();

            }else if (opcion == 4) {
                System.out.println("Saliendo del programa...");
                break; 

            }

            else {
                System.out.println("Opción inválida.");
            }
        }
        

        sc.close();
    }

    private static void borrarArchivosProc() {
        File carpeta = new File("."); 
        File[] archivos = carpeta.listFiles();

        if (archivos != null) {
            boolean borrado = false;
            for (File f : archivos) {
                if (f.isFile() && f.getName().matches("proc\\d+\\.txt")) {
                    if (f.delete()) {
                        System.out.println("Eliminado: " + f.getName());
                        borrado = true;
                    } else {
                        System.out.println("No se pudo eliminar: " + f.getName());
                    }
                }
            }
            if (!borrado) {
                System.out.println("No se encontraron archivos proc_i.txt");
            }
        } else {
            System.out.println("No se pudo acceder a la carpeta actual.");
        }
    }
    
}
