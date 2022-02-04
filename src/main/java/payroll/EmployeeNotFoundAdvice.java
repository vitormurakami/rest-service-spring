package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EmployeeNotFoundAdvice {
	@ResponseBody
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		return ex.getMessage();
	}
	
}

/*
 	@ControllerAdvice -> É um interceptador de exceções lançadas por métodos anotados com @RequestMapping ou um dos atalhos
 	@ResponseBody -> Se um método for anotado com @ResponseBody, o Spring vinculará o valor de retorno ao corpo da resposta HTTP de saída em JSON.
 	@ExceptionHandler -> indica qual tipo de exceção queremos tratar
 */ 
