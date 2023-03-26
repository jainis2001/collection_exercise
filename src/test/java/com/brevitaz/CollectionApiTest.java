package com.brevitaz;

import com.brevitaz.models.Customer;
import com.brevitaz.models.Order;
import com.brevitaz.models.Product;
import com.brevitaz.repos.CustomerRepo;
import com.brevitaz.repos.OrderRepo;
import com.brevitaz.repos.ProductRepo;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import static java.util.stream.Collectors.toList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@DataJpaTest
public class CollectionApiTest {

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private ProductRepo productRepo;
	List<Order> orderList;
	List<Product> productsList;



	@Test
	@DisplayName("Obtain a list of product with category = \"Books\" and price > 100")
	public void exercise1() {
		productsList=productRepo.findAll();

		for(Product product: productsList){
			if((product.getCategory()).equals("Books") && ((product.getPrice())>100))
				System.out.println(product);
		}
	}

	@Test
	@DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using Predicate chaining for filter)")
	public void exercise1a() {
		productsList=productRepo.findAll();
		Predicate<Product> predicate =p1->p1.getCategory().equals("Books");
		Predicate<Product> predicate2 = p2 -> p2.getPrice()>100;
		productsList.stream().filter(predicate.and(predicate2)).forEach(System.out::println);
	}


	@Test
	@DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using BiPredicate for filter)")
	public void exercise1b() {
		productsList=productRepo.findAll();
		BiPredicate<String, Double> biPredicate = (p1, p2) -> p1.equals("Books") && p2>100;
		productsList.stream().filter(product->biPredicate.test(product.getCategory(),product.getPrice())).forEach(System.out::println);
	}

	@Test
	@DisplayName("Obtain a list of order with product category = \"Baby\"")
	public void exercise2() {
		orderList=orderRepo.findAll();
		Set<Order> orderSet=new HashSet<>();
		for(Order order: orderList){
			for(Product product:order.getProducts()){
				if((product.getCategory()).equals("Baby")){
					orderSet.add(order);
				}
			}
		}
		List<Order> tempList = new ArrayList<>(orderSet);
		tempList.sort(Comparator.comparing(Order::getId));

		for (Order order:tempList){
			System.out.println(order);
		}

	}

	@Test
	@DisplayName("Obtain a list of order with product category = \"Baby\"")
	public void exercise2Stream() {
		orderList=orderRepo.findAll();
		List<Order> orderStream=orderList.stream()
						.filter(order->order.getProducts().stream()
								.anyMatch(product -> product.getCategory().equals("Baby")))
								.collect(Collectors.toList());
		orderStream.forEach(System.out::println);
	}


	@Test
	@DisplayName("Obtain a list of product with category = “Toys” and then apply 10% discount\"")
	public void exercise3() {
		List<Product> productsList=productRepo.findAll();
		double discount;
		for(Product product: productsList){
			if((product.getCategory()).equals("Toys")){
				discount=  (product.getPrice()*10)/100;
				product.setPrice((product.getPrice())-discount);
				System.out.println(product);

			}
		}
	}
	@Test
	@DisplayName("Obtain a list of product with category = “Toys” and then apply 10% discount\"")
	public void exercise3WithStream() {

		List<Product> productsList=productRepo.findAll();
		List<Product> list=productsList.stream().filter(product -> product.getCategory().equals("Toys"))
				.map(p->new Product(p.getId(),p.getName(),p.getCategory(),p.getPrice(), p.getOrders()))
				.collect(Collectors.toList());
		list.forEach(product -> product.setPrice(product.getPrice()-product.getPrice()/10));
		list.forEach(System.out::println);
		System.out.println("---------------");
		productsList.forEach(System.out::println);
	}

	@Test
	@DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
	public void exercise4() {
		LocalDate firstDate = LocalDate.parse("2021-02-01");
		LocalDate LastDate = LocalDate.parse("2021-04-01");
		orderList=orderRepo.findAll();

		Set<Product> productSet=new HashSet<>();
		for(Order order: orderList){
			if((order.getCustomer().getTier()==2)){
				if(((!(order.getOrderDate()).isBefore(firstDate)) && (!(order.getOrderDate()).isAfter(LastDate)))){
					productSet.addAll(order.getProducts());
				}
			}
		}

		for (Product product:productSet){
			System.out.println(product);
		}

	}
	@Test
	@DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
	public void exercise4WithStream() {
		LocalDate firstDate = LocalDate.parse("2021-02-01");
		LocalDate LastDate = LocalDate.parse("2021-04-01");
		orderList=orderRepo.findAll();
		Set<Product> filterProducts=  orderList.stream()
				.filter(order->order.getOrderDate().isBefore(LastDate))
				.filter(order->order.getOrderDate().isAfter(firstDate))
				.filter(order->order.getCustomer().getTier()==2)
				.flatMap(order -> order.getProducts().stream())
				.collect(Collectors.toSet());
		filterProducts.forEach(System.out::println);



	}

	@Test
	@DisplayName("Get the 3 cheapest products of \"Books\" category")
	public void exercise5() {
		int i=0;
		productsList=productRepo.findAll();
		productsList.sort((product, t1) -> (int) (product.getPrice() - t1.getPrice()));
		for(Product product:productsList){
			if((product.getCategory().equals("Books")) && (i<3)){
				System.out.println(product);
				i++;
				System.out.println("-----------------");
			}
		}
	}

	@Test
	@DisplayName("Get the 3 cheapest products of \"Books\" category")
	public void exercise5WithStream() {
		List<Product> productsStream=productRepo.findAll().stream()
				.filter(product -> product.getCategory().equals("Books"))
				.sorted(Comparator.comparing(Product::getPrice))
				.limit(3)
				.collect(Collectors.toList());
		productsStream.forEach(System.out::println);
	}


	@Test
	@DisplayName("Get the 3 most recent placed order")
	public void exercise6() {
		orderList=orderRepo.findAll();
		orderList.sort(Comparator.comparing(Order::getOrderDate));
		System.out.println("\n--------------------");
		System.out.println(orderList.get(orderList.size()-1));
		System.out.println(orderList.get(orderList.size()-2));
		System.out.println(orderList.get(orderList.size()-3));
		System.out.println("--------------------\n\n");

	}
	@Test
	@DisplayName("Get the 3 most recent placed order")
	public void exercise6WithStream() {
		List<Order> orderStream=orderRepo.findAll().stream()
				.sorted(Comparator.comparing(Order::getOrderDate).reversed())
				.limit(3)
				.collect(Collectors.toList());
		orderStream.forEach(System.out::println);

	}

	@Test
	@DisplayName("Get a list of products which was ordered on 15-Mar-2021")
	public void exercise7() {
		orderList=orderRepo.findAll();
		Set<Product> productSet=new HashSet<>();
		for(Order order:orderList){
			if(String.valueOf(order.getOrderDate()).equals("2021-03-15")){
				System.out.println(order);
				productSet.addAll(order.getProducts());
			}
		}
		for(Product product:productSet)
		{
			System.out.println(product);
		}

	}
	@Test
	@DisplayName("Get a list of products which was ordered on 15-Mar-2021")
	public void exercise7WithStream() {
		Set<Product> productStream=orderRepo.findAll().stream()
				.filter(order -> order.getOrderDate().equals(LocalDate.parse("2021-03-15")))
				.peek(System.out::println)
				.flatMap(order1->order1.getProducts().stream())
				.collect(Collectors.toSet());
		productStream.forEach(System.out::println);
	}

	@Test
	@DisplayName("Calculate the total lump of all orders placed in Feb 2021")
	public void exercise8() {

		double price=0;
		orderList=orderRepo.findAll();
		for(Order order:orderList){
			for(Product product:order.getProducts()) {
				if (String.valueOf(order.getOrderDate().getYear()).equals("2021") && String.valueOf(order.getOrderDate().getMonthValue()).equals("2")) {
					price+=product.getPrice();

				}
			}

		}
		System.out.println(price);
	}

	@Test
	@DisplayName("Calculate the total lump of all orders placed in Feb 2021")
	public void exercise8WithStream() {
		double result=orderRepo.findAll().stream()
				.filter(order -> order.getOrderDate().getYear()==2021)
				.filter(order -> order.getOrderDate().getMonthValue()==2)
				.flatMap(order -> order.getProducts().stream())
				.mapToDouble(Product::getPrice).sum();
		System.out.println("result "+result);

	}


	@Test
	@DisplayName("Calculate the total lump of all orders placed in Feb 2021 (using reduce with BiFunction)")
	public void exercise8aWithStream() {
		double result=orderRepo.findAll().stream()
				.filter(order -> order.getOrderDate().getYear()==2021 &&  order.getOrderDate().getMonthValue()==2)
				.flatMap(order -> order.getProducts().stream())
				.reduce(0.0,(p1,p2) -> (p1+p2.getPrice()),Double::sum);
		System.out.println("result "+result);
		double result2=orderRepo.findAll().stream()
				.filter(order -> order.getOrderDate().getYear()==2021 &&  order.getOrderDate().getMonthValue()==2)
				.flatMap(order -> order.getProducts().stream())
				.map(Product::getPrice)
				.reduce(0.0, Double::sum);
		System.out.println("result "+result2);



	}

	@Test
	@DisplayName("Calculate the average price of all orders placed on 15-Mar-2021")
	public void exercise9() {
		double sum=0;
		int count=0;
		double avg;
		orderList=orderRepo.findAll();
		for(Order order:orderList){
			for(Product product:order.getProducts()){
				if(String.valueOf(order.getOrderDate()).equals("2021-03-15")){
					sum= (sum+product.getPrice());
					count++;
				}
			}
		}
		avg=sum/count;
		System.out.println("Average: "+avg);
		System.out.println("---------------------------\n\n");
	}

	@Test
	@DisplayName("Calculate the average price of all orders placed on 15-Mar-2021")
	public void exercise9WithStream() {
		OptionalDouble average = orderRepo.findAll().stream()
				.filter(order -> order.getOrderDate().equals(LocalDate.parse("2021-03-15")))
				.flatMap(order -> order.getProducts().stream())
				.mapToDouble(Product::getPrice).average();


		System.out.println("result "+average);

	}

	@Test
	@DisplayName("Obtain statistics summary of all products belong to \"Books\" category")
	public void exercise10() {
		int count=0;
		double sum=0;
		double avg;
		//double max=0;
		double min=0;
		productsList=productRepo.findAll();
		List<Product> max=new ArrayList<>();
		productsList.sort(Comparator.comparing(Product::getPrice));
		for(Product product:productsList){

			if(product.getCategory().equals("Books")){
				if(min==0){
					min=product.getPrice();
				}
				count++;
				sum+=product.getPrice();
				System.out.println(product);
				max.add(product);
				//max=product.getPrice();
			}

		}
		max=Collections.singletonList(Collections.max(max, Comparator.comparing(Product::getPrice)));

		avg=sum/count;
		System.out.println("Count= "+count);
		System.out.println("average= "+avg);
		System.out.println("max= "+max.get(0).getPrice());
		System.out.println("min= "+min);
		System.out.println("sum= "+sum);

	}

	@Test
	@DisplayName("Obtain statistics summary of all products belong to \"Books\" category")
	public void exercise10WithStream() {
		DoubleSummaryStatistics result=productRepo.findAll().stream()
				.filter(product -> product.getCategory().equals("Books"))
				.mapToDouble(Product::getPrice)
				.summaryStatistics();

		System.out.println(result);
	}

	@Test
	@DisplayName("Obtain a mapping of order id and the order's product count")
	public void exercise11() {
		HashMap<Long,Integer> map=new HashMap<>();
		orderList=orderRepo.findAll();
		for(Order order:orderList){
			int count=0;
			for(Product ignored :order.getProducts()){
				count++;
			}
			map.put(order.getId(),count);
		}
		for(Map.Entry<Long, Integer> e:map.entrySet()){
			System.out.println(e);


		}

	}

	@Test
	@DisplayName("Obtain a mapping of order id and the order's product count")
	public void exercise11WithStream() {
		Map<Long,Integer> orderCountMap=orderRepo.findAll().stream()
				.collect(Collectors.toMap(Order::getId, order->order.getProducts().size()));
//		Map<Long, Long> orderCountMap=orderRepo.findAll().stream()
//				.collect(groupingBy(Order::getId,Collectors.mapping(order->order.getProducts(),)))));

		orderCountMap.entrySet().forEach(System.out::println);
	}

	@Test
	@DisplayName("Obtain a data map of customer and list of orders")
	public void exercise12() {
		orderList=orderRepo.findAll();
		HashMap<Customer,List<Order>> map=new HashMap<>();
		for(Order order:orderList){

			if(!map.containsKey(order.getCustomer())){
				List<Order> orders=new ArrayList<>();
				orders.add(order);
				map.put((order.getCustomer()),orders);
			}
			else{
				map.get((order.getCustomer())).add(order);
			}
		}
		for(Map.Entry<Customer, List<Order>> e:map.entrySet()){
			System.out.println(e);

		}
	}
	@Test
	@DisplayName("Obtain a data map of customer and list of orders")
	public void exercise12WithStream() {

		Map<Customer, List<Order>> customerOderMap=orderRepo.findAll().stream()
				.collect(groupingBy(Order::getCustomer,
						Collectors.mapping(o-> o,Collectors.toList())));
		customerOderMap.entrySet().forEach(System.out::println);
	}

	@Test
	@DisplayName("Obtain a data map of customer_id and list of order_id(s)")
	public void exercise12a() {
		orderList=orderRepo.findAll();
		HashMap<Long,List<Long>> map=new HashMap<>();
		for(Order order:orderList){

			if(!map.containsKey(order.getCustomer().getId())){
				List<Long> orders=new ArrayList<>();
				orders.add(order.getId());
				map.put((order.getCustomer().getId()),orders);
			}
			else{
				map.get((order.getCustomer().getId())).add(order.getId());
			}

		}
		for(Map.Entry<Long, List<Long>> e:map.entrySet()){
			System.out.println(e);
			System.out.println("---------------------");

		}
	}
	@Test
	@DisplayName("Obtain a data map of customer_id and list of order_id(s)")
	public void exercise12aWithStream() {
		Map<Long, List<Long>> map = orderRepo.findAll().stream()
				.collect(
						groupingBy(o -> o.getCustomer().getId(),
								Collectors.mapping(Order::getId, toList()))
				);
		map.entrySet().forEach(System.out::println);

	}

	@Test
	@DisplayName("Obtain a data map with order and its total price")
	public void exercise13() {
		//int price=0;
		orderList=orderRepo.findAll();
		HashMap<Order,Double> map=new HashMap<>();
		for(Order order:orderList){
			double price=0;
			for(Product product:order.getProducts())
			{
				price=price+product.getPrice();
			}
			map.put(order,price);
		}
		for(Map.Entry<Order, Double> e:map.entrySet()){
			System.out.println(e);
		}


	}

	@Test
	@DisplayName("Obtain a data map with order and its total price")
	public void exercise13WithStream() {
		Map<Order,Double> map=orderRepo.findAll().stream()
				.collect(Collectors.toMap(order->order,order->order.getProducts()
						.stream()
						.mapToDouble(Product::getPrice)
						.sum()
				));
				map.entrySet().forEach(System.out::println);
	}


	@Test
	@DisplayName("Obtain a data map with order and its total price (using reduce)")
	public void exercise13aWithStream() {
		Map<Order,Double> map=orderRepo.findAll().stream()
				.collect(Collectors.toMap(order->order,order->order.getProducts()
						.stream()
						.reduce(0.0,(p1,p2)->p1+p2.getPrice(),Double::sum)
				));
				map.entrySet().forEach(System.out::println);

	}
	@Test
	@DisplayName("Obtain a data map of product name by category")
	public void exercise14() {
		productsList=productRepo.findAll();
		HashMap<String,List<String>> map=new HashMap<>();

		for(Product product:productsList)
		{
			if(!map.containsKey(product.getCategory())){
				List<String> productNames=new ArrayList<>();
				productNames.add(product.getName());
				map.put(product.getCategory(),productNames);
			}
			else{
				map.get(product.getCategory()).add(product.getName());
			}
		}


		for(Map.Entry<String, List<String>> entry : map.entrySet()){
			System.out.println(entry);

		}
	}
	@Test
	@DisplayName("Obtain a data map of product name by category")
	public void exercise14WithStream() {
		Map<String,List<String>> map=productRepo.findAll().stream()
				.collect(Collectors.groupingBy(Product::getCategory,
						Collectors.mapping(Product::getName,Collectors.toList())));

		map.entrySet().forEach(System.out::println);
	}

	@Test
	@DisplayName("Get the most expensive product per category")
	void exercise15() {
		productsList=productRepo.findAll();
		HashMap<String,List<Product>> map=new HashMap<>();

		for(Product product:productsList)
		{

			if(!map.containsKey(product.getCategory())){
				List<Product> productsList=new ArrayList<>();
				productsList.add(product);
				map.put(product.getCategory(),productsList);
			}
			else{
				map.get(product.getCategory()).add(product);
			}
		}
		for(Map.Entry<String, List<Product>> entry : map.entrySet()){

			entry.getValue().sort(Comparator.comparing(Product::getPrice));

			System.out.println(entry.getKey()+","+entry.getValue().get((entry.getValue()).size()-1));
		}

		System.out.println("-----------------");
	}

	@Test
	@DisplayName("Get the most expensive product per category")
	void exercise15WithStream() {
		Map<String, Optional<Product>> map = productRepo.findAll().stream()
				.collect(groupingBy(Product::getCategory,
						Collectors.maxBy(Comparator.comparing(Product::getPrice))
				));
		map.entrySet().forEach(System.out::println);
		}


	@Test
	@DisplayName("Get the most expensive product (by name) per category")
	void exercise15a() {
		//
		productsList=productRepo.findAll();
		HashMap<String,List<Product>> map=new HashMap<>();

		for(Product product:productsList)
		{
			if(!map.containsKey(product.getCategory())){
				List<Product> productsList=new ArrayList<>();
				productsList.add(product);
				map.put(product.getCategory(),productsList);
			}
			else{
				map.get(product.getCategory()).add(product);
			}
		}
		for (Map.Entry<String, List<Product>> entry : map.entrySet()) {

			entry.getValue().sort(Comparator.comparing(Product::getPrice));

			System.out.println(entry.getKey() + "," + entry.getValue().get((entry.getValue()).size() - 1).getName());

		}

		System.out.println("-----------------");

	}



	@Test
	@DisplayName("Get the most expensive product (by name) per category")
	void exercise15aWithStream() {
		Map<String, String> collect = productRepo.findAll().stream()
				.collect(groupingBy(Product::getCategory,
						Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Product::getPrice)),
								 product -> product.isPresent() ? product.get().getName() : "none")
				));

		collect.entrySet().forEach(System.out::println);
//		map.entrySet().forEach(System.out::println);


	}

	@Test
	@DisplayName(" Obtain list of order having maximum products")
	void exercise16aWithStream() {
		orderRepo.findAll().stream()
				.collect(Collectors.toMap(order -> order.getId(), order -> order.getProducts().size()))
				.entrySet()
				.stream()
//				.sorted(Map.Entry.comparingByValue())
				.max(Comparator.comparing(p->p.getValue()));
//				.stream()
//				.filter(p->p.getValue().equals(Collections.max(Comparator.comparing(p1->p1.))))

//				.;

//		System.out.println(max);

	}

}


