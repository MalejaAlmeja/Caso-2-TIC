import java.util.Scanner;

public class Consola {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Selecciona una opción:");
        System.out.println("1. Ejecutar Opcion1");
        System.out.println("2. Ejecutar Opcion2");
        System.out.print("Tu elección: ");

        int opcion = sc.nextInt();

        if (opcion == 1) {
            System.out.print("Tamaños de página: ");
            int tp = sc.nextInt();
            System.out.print("Número de procesos: ");
            int nproc = sc.nextInt();
            System.out.print("Tamaño matrices separados por coma: ");
            String tams = sc.next();

            Opcion1.ejecutarOpcion1(tp,nproc,tams);
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
