import java.util.ArrayList;
import java.util.Arrays;

public class Proceso {
    
    private int[] TP;
    private int NF;
    private int NC;
    private int NR;
    private int NP;
    private ArrayList<Integer> listaDV;

    public Proceso(int NMARCOS, int NF, int NC, int NR, int NP, ArrayList<Integer> listaDV)
    {
        this.TP = new int[NMARCOS];
        Arrays.fill(this.TP,-1); //-1 significa que no tiene ningúna marco de página asignado
        this.NF = NF;
        this.NC = NC;
        this.NR = NR;
        this.NP = NP;
        this.listaDV = listaDV;
    }

    public int[] getTP()
    {
        return this.TP;
    }

    public int getNF()
    {
        return this.NF;
    }
    public int getNC()
    {
        return this.NC;
    }
    public int getNR()
    {
        return this.NR;
    }
    public int getNP()
    {
        return this.NP;
    }
    public ArrayList<Integer> getListaDV()
    {
        return this.listaDV;
    }

    public void updateTP(int[] newTP)
    {
        this.TP = newTP;
    }
}
