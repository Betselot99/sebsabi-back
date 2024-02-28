package et.com.gebeya.safaricom.coreservice.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FormNotFoundException extends RuntimeException{
    public FormNotFoundException(String message){
        super(message);
    }
}
