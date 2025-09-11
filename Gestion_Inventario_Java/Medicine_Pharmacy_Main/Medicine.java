import java.util.Date;
import java.text.SimpleDateFormat;

public class Medicine{


    private String name;
    private String lot;
    private Date expirationDate; 

    public Medicine(String name, String lot, Date expirationDate){

        this.name = name;
        this.lot= lot; 
        this.expirationDate = expirationDate;
    
    }

    public String getName(){

        return name;
    }

    public String getLot(){

        return lot;
    }

    public Date getExpirationDate(){
        return expirationDate;

    }

    @Override 
    public String toString(){

        SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");
        return "Nombre: " + name  + " Lote: " + lot + " Fecha de vencimiento: " + sdf.format(expirationDate);
    }
}