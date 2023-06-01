package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;

@Repository
public class OrdersRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static final String MONGO_COLLECTION_NAME="orders";

	// TODO: Task 3
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for add()

	// db.orders.insert({
	// 	_id: '1',
	// 	date: '2023-06-01',
	// 	total: 50.0,
	// 	name: 'Fred',
	// 	email: 'fred@gmail.com',
	// 	sauce: 'classic',
	// 	size: 1,
	// 	comments: 'Nil',
	// 	crust: 'thick',
	// 	toppings: ['cheese', 'seafood']
	// });

	public void add(PizzaOrder order) {
		Document doc = new Document();
		doc.put("_id", order.getOrderId());
		doc.put("date", order.getDate().getTime());
		doc.put("total", order.getTotal());
		doc.put("name", order.getName());
		doc.put("email", order.getEmail());
		doc.put("sauce", order.getSauce());
		doc.put("size", order.getSize());
		doc.put("comments", order.getComments());
		doc.put("crust", order.getThickCrust() ? "thick" : "thin");
		doc.put("toppings", order.getTopplings());
		mongoTemplate.insert(doc, MONGO_COLLECTION_NAME);
	}
	
	// TODO: Task 6
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for getPendingOrdersByEmail()

	// db.orders.find({ email: 'fred@gmail.com' ,  delivered: { $exists : false} }, { _id: 1, total: 1 , date: 1 } ).sort({date: -1});

	public List<PizzaOrder> getPendingOrdersByEmail(String email) {

		List<PizzaOrder> pizzaOrders = new LinkedList<>();

		Query query = Query.query(
			Criteria.where("delivered").exists(false).and("email").is(email)
			)
			.with(
				Sort.by(Sort.Direction.DESC, "date")
				);
		query.fields().include("_id", "total", "date");
		List<Document> documents = mongoTemplate.find(query, Document.class, MONGO_COLLECTION_NAME);

		for (Document doc: documents) {
			PizzaOrder pizzaOrder = new PizzaOrder();
			pizzaOrder.setOrderId(doc.getString("_id"));
			pizzaOrder.setTotal(doc.getDouble("total").floatValue());
			pizzaOrder.setDate(new Date(doc.getLong("date")));
			pizzaOrders.add(pizzaOrder);
		}
		return pizzaOrders;
	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for markOrderDelivered()

	// db.orders.updateOne(
	// 	{ _id: '279c9b36f5' },
	// 	{ $set: { delivered: true } }
	// );
	public boolean markOrderDelivered(String orderId) {
		Query query = Query.query(Criteria.where("_id").is(orderId));
		Update updateOps = new Update().set("delivered", true);

		UpdateResult updateResult = mongoTemplate.updateFirst(query, updateOps, Document.class, MONGO_COLLECTION_NAME);

		return updateResult.getModifiedCount() > 0;
	}


}
