package payroll;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



@RestController 
public class EmployeeController {
	//Não foi utilizado a anotação @Autowired, porque a injeção foi feita através do construtor
	private final EmployeeRepository repository;
	private final EmployeeModelAssembler assembler;
		
	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler){
		this.repository = repository;
		this.assembler = assembler;
	}

	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all(){
		
		  List<EntityModel<Employee>> employees = repository.findAll().stream()
			      .map(employee -> assembler.toModel(employee))
			      .collect(Collectors.toList());
		  /*  
		  List<EntityModel<Employee>> employees = repository.findAll().stream() //
			      .map(assembler::toModel) //
			      .collect(Collectors.toList());
		   */
		  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

	  EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

	  return ResponseEntity //
	      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
	      .body(entityModel);
	}
	
	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {
		Employee employee = repository.findById(id).orElseThrow(()-> new EmployeeNotFoundException(id));
		return assembler.toModel(employee);
		
		//Sem o EmployeeModelAssembler
		//return EntityModel.of(employee, linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(), linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}

	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id){
		 Employee updatedEmployee = repository.findById(id) //
			      .map(employee -> {
			        employee.setName(newEmployee.getName());
			        employee.setRole(newEmployee.getRole());
			        return repository.save(employee);
			      }).orElseGet(() -> {
			    	  newEmployee.setId(id);
			    	  return repository.save(newEmployee);
			      });
		 
		 EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);
		 return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	
	}
	
	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
}

/*
	@RestController -> indica que os dados retornados por cada método serão gravados diretamente no corpo da resposta em vez de renderizar um modelo./ O objeto e os dados do objeto são gravados diretamente na resposta HTTP como JSON ou XML.
	@GetMapping -> Essa anotação é usada para mapear solicitações HTTP GET em métodos manipuladores específicos.
	@RequestBody → O parâmetro do método deve estar vinculada ao corpo da solicitação HTTP. Recebe um objeto no corpo da requisição através de um JSON
	@PathVariable → Anotação que indica que o parâmetro de um método deve ser vinculado a uma variável de modelo de URI.
	.orElse -> caso não encontre, faça tal coisa 
	linkTo -> Monta um link
	EntityModel -> Entrega o(s) objeto(s) + os links
	EntityModel.of() -> Monta o EntityModel com o objeto + o mapeamento dos links 
 */
