package se.ff.bs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Bus not found")
public class BusNotFoundException extends RuntimeException{

}
