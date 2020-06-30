package sd.oms;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sd.oms.model.Customer;
import sd.oms.model.CustomerRepository;
import sd.oms.model.ProductCategory;
import sd.oms.model.ProductCategoryRepository;
import sd.oms.model.SKUItem;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/customer")
public class UserController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ProductCategoryRepository categoryRepository;
	
	@RequestMapping(value = "/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMessage() {
		ResponseEntity<String> response = null;
		response = new ResponseEntity<>("UserService is live", HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<Customer>> findCustomers() {
		ResponseEntity<ArrayList<Customer>> response = null;
		ArrayList<Customer> allCustomers = new ArrayList<>();
		Iterable<Customer> customers = customerRepository.findAll();
		for (Customer cust : customers) {
			allCustomers.add(cust);
		}
		System.out.println("# of users = " + allCustomers.size());
		response = new ResponseEntity<ArrayList<Customer>>(allCustomers, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Customer> findById(@PathVariable(name = "id", required = true) String id) {

		ResponseEntity<Customer> response = null;
		Customer result = null;
		Iterable<Customer> customers = customerRepository.findAll();
		for (Customer cust : customers) {
			if (cust.getId().equalsIgnoreCase(id)) {
				result = cust;
			}
		}
		response = new ResponseEntity<Customer>(result, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer cust) {
		ResponseEntity<Customer> response = null;
		System.out.println("Order Processed. Returning response...");
		return response;
	}

	@GetMapping(path = "/addcustomer") //
	public @ResponseBody String addCustomer(@RequestParam(required = true) String name,
			@RequestParam(required = true) String phone, @RequestParam(required = false) String type,
			@RequestParam(required = false) String seller, @RequestParam(required = false) String password,
			@RequestParam(required = false) String email, @RequestParam(required = false) String address) {

		String id = phone;

		if (type != null) {
			type = "SELLER";
			seller = null;
		} else {
			type = "CUSTOMER";
		}

		password = (password == null || password.trim().equals("")) ? "password" : password;

		Customer cust = new Customer(id, type, seller, password, name, phone, email, address, new Date());

		customerRepository.save(cust);

		if (type.equals("CUSTOMER")) {
			System.out.println("Registered customer successfully");
			seller = seller == null? "" : seller;
			Optional<Customer> sellerUser = customerRepository.findById(seller);
			sellerUser.ifPresent(s -> {
				System.out.println("Customer user registered, and mapped to seller "+s.getName());
			});
		
			if (!sellerUser.isPresent()) {
				System.out.println("Customer user registered; but was not mapped to a seller. seller in request was: " + seller);
			}
		}else {
			System.out.println("Registered seller successfully");
		}

		return "User added successfully";
	}

	@GetMapping(path = "/seed") //
	public @ResponseBody String seedUser() {
		customerRepository.deleteAll();
		addCustomer("Mana", "1111", "s", null, null, null, null);
		addCustomer("Bappa", "2222", "s", null, null, null, null);
		
		addCustomer("Arghya", "3333", null, "1111", null, null, null);
		addCustomer("Mehul", "4444", null, "2222", null, null, null); // mapped to seller Bappa
		addCustomer("Dipa", "5555", null, "11", null, null, null); // mapped to absent seller 
		
		return "User data seeded successfully";
	}
	
	@GetMapping(path = "/clean") //
	public @ResponseBody String cleanCustomer() {
		customerRepository.deleteAll();
		return "All users deleted";
	}

}