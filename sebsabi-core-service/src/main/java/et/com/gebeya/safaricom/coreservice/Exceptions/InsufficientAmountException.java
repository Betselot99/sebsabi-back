package et.com.gebeya.safaricom.coreservice.Exceptions;

public class InsufficientAmountException extends RuntimeException{
    public InsufficientAmountException(String message){
        super(message);
    }
}
