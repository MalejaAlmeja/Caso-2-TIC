public class Proceso {
    
    private int[] TP;

    public Proceso(int NMARCOS)
    {
        this.TP = new int[NMARCOS];
    }

    public int[] getTP()
    {
        return this.TP;
    }

    public void modifyTP(int[] newTP)
    {
        this.TP = newTP;
    }
}
