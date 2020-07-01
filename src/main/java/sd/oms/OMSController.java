package sd.oms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sd.oms.dataobject.*;
import sd.oms.model.*;
import sd.oms.service.CatalogService;
import sd.oms.util.OMSUtil;

@Controller
public class OMSController {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CatalogService catalogService;

	@PostMapping("/login")
	public String login(@RequestParam(name = "phone", required = true) String phone,
			Model model, HttpSession httpSession) {
		
		System.out.println("login :: "+phone);
		List<Customer> customers = customerRepository.findByPhone(phone);
		
		Customer customer = null;
		if(customers != null && customers.size() > 0) {
			for (int i = 0; i < customers.size(); i++) {
				if(customers.get(i).getPhone().equals(phone)) {
					customer = customers.get(i);
				}
			}
		}
		
		System.out.println("Customer record :: "+customer);
		
		if(customer != null) {
			httpSession.setAttribute("customer", customer);
			
			try {
				ProductCategory root = catalogService.findCatalog(customer.getSeller()); // getJson(); Test.getDummyCatalog();
				
				String productsJson = root.toJSON();
				
				httpSession.setAttribute("products", productsJson);
				
				System.out.println("Catalog saved in session: "+productsJson);
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			model.addAttribute("message", "Invalid login, please contact Admin");
			return "error";
		}
		
		return "home";//showOrder(model, httpSession);
	}

	@GetMapping("/order")
    public String showOrder(Model model, HttpSession httpSession) {
		Customer cust = (Customer)httpSession.getAttribute("customer");
		ArrayList<Order> orders =  getOrders(cust.getId());
		model.addAttribute("orders", orders);
		
		System.out.println("Going to orders");
        return "orders";
    }
	
	@GetMapping("/home")
    public String showProducts(Model model, HttpSession httpSession) {
		System.out.println("Going to products");
        return "home";
    }
	
	@GetMapping("/cart")
    public String showCart(Model model, HttpSession httpSession) {
		System.out.println("Going to cart");
        return "cart";
    }
	
	@GetMapping("/seller")
    public String showSeller(Model model, HttpSession httpSession) {
		Customer cust = (Customer)httpSession.getAttribute("customer");
		Optional<Customer> seller = customerRepository.findById(cust.getSeller());
		
		if(seller.isPresent()) {
			model.addAttribute("seller", seller.get()); 
			System.out.println("Going to seller");
		}else {
			System.out.println("WARNING::: No seller mapped to buyer");
			model.addAttribute("message", "You have not selected a seller yet. Please contact admin.");
			return "error"; 
		}
        return "seller";
    }
	
	@PostMapping("/order")
	public String submitOrder(@RequestParam(name = "order", required = true) String orderPayload,
			Model model, HttpSession httpSession) {
		
		System.out.println("Received order :: "+orderPayload);
		
		Customer cust = (Customer)httpSession.getAttribute("customer");
		
		
		final GsonBuilder gsonBuilder = new GsonBuilder();
		final Gson gson = gsonBuilder.create();

		//ItemDTO[] items = gson.fromJson(orderPayload, ItemDTO[].class);
		OrderDTO inputOrder = gson.fromJson(orderPayload, OrderDTO.class);
		ItemDTO[] items = inputOrder.getItems();
		System.out.println("Order has = "+items.length+" items; inst = "+inputOrder.getInstruction());
		for (int i = 0; i < items.length; i++) {
			System.out.println(items[i].getDescription());
		}
		
		double totalPrice = 0;
		
		Order order = new Order(null, inputOrder.getInstruction(), OMSUtil.ORDER_STATUS_NEW, null, cust.getId(), cust.getSeller(), null); 	
	  
    	for (ItemDTO item : items) {   
    		totalPrice = totalPrice +  item.getPrice() * item.getQuantity();
    		order.getItems().add(new Item(null, item.getCode(), item.getName(), item.getDescription(), item.getQuantity(), item.getPrice(),item.getUnit(), null,null, OMSUtil.ITEM_STATUS_ORDERED, order));
 		}
    	
    	order.setTotalPrice(new Double(totalPrice));
    	
	    orderRepository.save(order);	
		System.out.println("Order saved successfully.");
		
		ArrayList<Order> orders =  getOrders(cust.getId());
		model.addAttribute("orders", orders);
		
		model.addAttribute("clearCart", true);
		
		model.addAttribute("message", "Thank you for placing Order# "+order.getId()+", Seller is informed and will process it.");
		
		return "orders";
	}
	
	@GetMapping("/logout")
    public String logout(Model model, HttpSession httpSession) {
		httpSession.removeAttribute("customer");
		httpSession.invalidate();
		System.out.println("Logging off.");
        return "redirect:/";
    }
	
	@GetMapping("/signup")
    public String signup(Model model, HttpSession httpSession) {
		System.out.println("Sign Up");
        return "signup";
    }
	
	public ArrayList<Order> getOrders(Long user){
		ArrayList<Order> orders = new ArrayList<>();
		long now = new Date().getTime();
		Iterable<Order>	allOrders = orderRepository.findByCustomerId(user);
		for (Order order : allOrders) {
			//Order status new or processing  
			if(OMSUtil.ORDER_STATUS_NEW.equals(order.getStatus()) || OMSUtil.ORDER_STATUS_PROCESSING.equals(order.getStatus())) {
				orders.add(order);
			}else {
				//If order is cancelled or completed, check date
				//if(OMSUtil.ORDER_STATUS_COMPLETED.equals(order.getStatus()) || OMSUtil.ORDER_STATUS_CANCELLED.equals(order.getStatus())) {	
				
				long age = TimeUnit.DAYS.convert(Math.abs(now - order.getCreated().getTime()), TimeUnit.MILLISECONDS);
				if(age < 90) { // Only within last 90 days
					orders.add(order);
				}
			}
		}
		return orders;
	}
	
	@GetMapping("/order/view/{id}")
    public String viewOrder(
    		@PathVariable(name = "id", required = true) String id,
    		Order order, Model model, HttpSession httpSession) {
		
		System.out.println("viewOrder :: order_id = "+id);
        Optional<Order> thisOrder = orderRepository.findById(new Long(id));

        if(thisOrder.isPresent()) {
            System.out.println("Order retrieved");
        	order = thisOrder.get();
        }
        
        httpSession.setAttribute("order", order);   
        model.addAttribute("order", order);
		return "order";
    }
	
	@GetMapping("/order/delete/{id}")
    public String deleteOrder(
    		@PathVariable(name = "id", required = true) String id,
    		Model model, HttpSession httpSession) {
		
		System.out.println("deleteOrder :: order_id = "+id);
		Customer customer = (Customer)httpSession.getAttribute("customer");
		ArrayList<Order> orders =  null;
        Optional<Order> order = orderRepository.findById(new Long(id));

        if(order.isPresent()) {
        	Order ord = order.get();
        	if(ord.getStatus() != null && !ord.getStatus().equals(OMSUtil.ORDER_STATUS_NEW)) {
        		//Only order in NEW state can be cancelled i.e. seller has not started processing
        		model.addAttribute("message", "Order is already under processing or processed; please contact your seller for any change.");
        		
        		orders =  getOrders(customer.getId());
        		model.addAttribute("orders", orders);
        		
        		return "home";
        	}
        	orderRepository.delete(ord);
        }
        System.out.println("Order deleted");
        
        model.addAttribute("message", "Order deleted successfully.");
        
        
        orders =  getOrders(customer.getId());
		model.addAttribute("orders", orders);
        
		return "orders";
    }
}
