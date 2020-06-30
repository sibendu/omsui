package sd.oms.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
	
	List<Order> findByCustomerId(String customerId);
}
