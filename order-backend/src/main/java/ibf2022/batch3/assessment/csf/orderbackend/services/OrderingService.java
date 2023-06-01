package ibf2022.batch3.assessment.csf.orderbackend.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.PendingOrdersRepository;

@Service
public class OrderingService {

	@Value("${pizza.service.api.url}")
	private String fullApiUrl; // https://pizza-pricing-production.up.railway.app/order
	
	@Autowired
	private OrdersRepository ordersRepo;

	@Autowired
	private PendingOrdersRepository pendingOrdersRepo;
	
	// TODO: Task 5
	// WARNING: DO NOT CHANGE THE METHOD'S SIGNATURE
	public PizzaOrder placeOrder(PizzaOrder order) throws OrderException {
		
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("name", order.getName());
		form.add("email", order.getEmail());
		form.add("sauce", order.getSauce());
		form.add("size", order.getSize().toString());
		form.add("thickCrust", order.getThickCrust().toString());
		form.add("toppings", String.join(",", order.getTopplings()));
		form.add("comments", order.getComments());
		RequestEntity<MultiValueMap<String, String>> req = RequestEntity.post(fullApiUrl).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_PLAIN).body(form, MultiValueMap.class);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

		String[] respBody = resp.getBody().split(",");
		order.setOrderId(respBody[0]);
		order.setDate(new Date(Long.parseLong(respBody[1])));
		order.setTotal(Float.parseFloat(respBody[2]));

		ordersRepo.add(order);
		pendingOrdersRepo.add(order);
		return order;
	}

	// For Task 6
	// WARNING: Do not change the method's signature or its implemenation
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
		return ordersRepo.getPendingOrdersByEmail(email);
	}

	// For Task 7
	// WARNING: Do not change the method's signature or its implemenation
	public boolean markOrderDelivered(String orderId) {
		return ordersRepo.markOrderDelivered(orderId) && pendingOrdersRepo.delete(orderId);
	}


}
