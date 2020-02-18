package sep.project.utils;

import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(value = {Exception.class})
	 public ResponseEntity<Object> handleGlobalException(Exception exception) {
	 	HttpHeaders responseHeaders=new HttpHeaders();
	 	responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	 	responseHeaders.set("X-ERROR", "true");
	 	
	 	JSONObject responseBody = new JSONObject();
	 	responseBody.put("message", "An error occurred. Please try again.");
	 	
	 	ResponseEntity<JSONObject> response = new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.BAD_REQUEST);
	 	
	 	return (ResponseEntity)response;
	 }

}
