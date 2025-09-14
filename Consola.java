import java.util.Scanner;

public class Consola {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Selecciona una opción:");
        System.out.println("1. Ejecutar opción 1");
        System.out.println("2. Ejecutar opcion 2");
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
        } else {
            System.out.println("Opción inválida.");
        }

        sc.close();
    }
    
}
