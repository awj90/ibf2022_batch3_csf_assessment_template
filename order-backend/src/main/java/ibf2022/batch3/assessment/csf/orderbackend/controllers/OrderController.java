package ibf2022.batch3.assessment.csf.orderbackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderException;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderingService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping
public class OrderController {	

	@Autowired
	private OrderingService orderingService;

	// TODO: Task 3 - POST /api/order
	@PostMapping(path="/api/order", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> postOrder(@RequestBody String jsonString) {
		try (InputStream is = new ByteArrayInputStream(jsonString.getBytes())) {
			JsonReader reader = Json.createReader(is);
			JsonObject jsonObject = reader.readObject();
			PizzaOrder pizzaOrder = new PizzaOrder();
			pizzaOrder.setName(jsonObject.getString("name"));
			pizzaOrder.setEmail(jsonObject.getString("email"));
			pizzaOrder.setSauce(jsonObject.getString("sauce"));
			pizzaOrder.setSize(jsonObject.getInt("size"));
			pizzaOrder.setThickCrust(jsonObject.getString("base").equalsIgnoreCase("thick") ? true : false);
			pizzaOrder.setComments(jsonObject.getString("comments"));
			
			List<String> toppings = new LinkedList<>();
			JsonArray jsonArray = jsonObject.getJsonArray("toppings");
			for (int i = 0; i < jsonArray.size(); i++) {
				toppings.add(jsonArray.getString(i));
			}
			pizzaOrder.setTopplings(toppings);
			PizzaOrder updatedPizzaOrder = orderingService.placeOrder(pizzaOrder);

			return ResponseEntity.status(HttpStatus.ACCEPTED)
								.contentType(MediaType.APPLICATION_JSON)
								.body(Json.createObjectBuilder()
											.add("orderId", updatedPizzaOrder.getOrderId())
											.add("date", updatedPizzaOrder.getDate().getTime())
											.add("name", updatedPizzaOrder.getName())
											.add("email", updatedPizzaOrder.getEmail())
											.add("total", updatedPizzaOrder.getTotal())
											.build()
											.toString());
		
		} catch (IOException | OrderException ex){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.contentType(MediaType.APPLICATION_JSON)
								.body(Json.createObjectBuilder().add("error", ex.getMessage())
																.build().toString());
		}
	}

	// TODO: Task 6 - GET /api/orders/<email>
	@GetMapping(path="/api/orders/{email}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> getPendingOrdersByEmail(@PathVariable String email) {
		List<PizzaOrder> pizzaOrders = orderingService.getPendingOrdersByEmail(email);
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for (PizzaOrder o: pizzaOrders) {
			JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
														.add("orderId", o.getOrderId())
														.add("total", o.getTotal())
														.add("date", o.getDate().getTime());
			jsonArrayBuilder.add(jsonObjectBuilder);
		}
		return ResponseEntity.status(HttpStatus.OK)
							.contentType(MediaType.APPLICATION_JSON)
							.body(jsonArrayBuilder.build().toString());
	}

	// TODO: Task 7 - DELETE /api/order/<orderId>
	@DeleteMapping(path="/api/order/{orderId}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> markOrderDelivered(@PathVariable String orderId) {
		boolean success = orderingService.markOrderDelivered(orderId);
		if (!success) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(Json.createObjectBuilder().add("error", "Order id %s not found".formatted(orderId)).build().toString());
		}
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(Json.createObjectBuilder().build().toString());
	}

}
