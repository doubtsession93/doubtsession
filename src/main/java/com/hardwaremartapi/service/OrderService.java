package com.hardwaremartapi.service;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import com.hardwaremartapi.FileUtility;
import com.hardwaremartapi.bean.Order;
import com.hardwaremartapi.bean.OrderItems;
import com.hardwaremartapi.bean.PurchaseOrder;

@Service
public class OrderService {

	
	OrderItems orderItem = new OrderItems();

	public Order placeOrders(Order order) {
		Firestore fireStore = FirestoreClient.getFirestore();
		String orderId = fireStore.collection("Order").document().getId().toString();
		order.setTimestamp(System.currentTimeMillis());
		order.setOrderId(orderId);
		fireStore.collection("Order").document(order.getOrderId()).set(order);
		return order;
	}

	public Order getOrderById(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Order order = fireStore.collection("Order").document(id).get().get().toObject(Order.class);
		return order;
	}

	public Order cancelOrder(String orderId) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Order order = fireStore.collection("Order").document(orderId).get().get().toObject(Order.class);
		if (order != null) {
			fireStore.collection("Order").document(orderId).delete();
		}
		return order;
	}

	public ArrayList<Order> getOrders(String userId) throws Exception, Exception {
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Order> list = new ArrayList<Order>();
		ApiFuture<QuerySnapshot> apiFuture = fireStore.collection("Order").whereEqualTo("userId", userId).get();
		QuerySnapshot querySnapshot = apiFuture.get();
		List<QueryDocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
		for (QueryDocumentSnapshot document : documentSnapshotList) {
			Order order = document.toObject(Order.class);
			String ship = order.getShippingStatus();
			if (ship.equals("Delivered") || ship.equals("Cancelled"))
				list.add(order);
		}
		return list;
	}

	public ArrayList<Order> getActiveOrders(String userId) throws Exception, Exception {
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Order> list = new ArrayList<Order>();
		ApiFuture<QuerySnapshot> apiFuture = fireStore.collection("Order").whereEqualTo("userId", userId).get();
		QuerySnapshot querySnapshot = apiFuture.get();
		List<QueryDocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
		for (QueryDocumentSnapshot document : documentSnapshotList) {
			Order order = document.toObject(Order.class);
			String ship = order.getShippingStatus();
			long time = order.getTimestamp();
			if (ship.equals("Onway")) {
				list.add(order);
			}
		}
		return list;
	}

	public ArrayList<Order> getOrderOfCurrentUser(String currentUserId) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<Order> list = new ArrayList<>();
		ApiFuture<QuerySnapshot> apiFuture = fireStore.collection("Order").whereEqualTo("userId", currentUserId).get();
		QuerySnapshot snapshot = apiFuture.get();
		List<QueryDocumentSnapshot> documentSnapshotsList = snapshot.getDocuments();
		for (QueryDocumentSnapshot document : documentSnapshotsList) {
			Order order = document.toObject(Order.class);
			list.add(order);
		}
		return list;
	}

	public ArrayList<PurchaseOrder> getPurchaseOrder(String shopKeeperId)
			throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<PurchaseOrder> purchaseOrderList = new ArrayList<>();

		ApiFuture<QuerySnapshot> apiFuture = fireStore.collection("Order").whereIn("shippingStatus", Arrays.asList("Delivered", "Cancelled")).get();

		QuerySnapshot querySnapshot = apiFuture.get();

		List<QueryDocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();

		for (QueryDocumentSnapshot document : documentSnapshotList) {
			double totalAmount = 0;
			boolean status = false;

			Order order = document.toObject(Order.class);
			System.out.println(order.getOrderId());

			ArrayList<OrderItems> orderItemList = order.getItemList();
			ArrayList<OrderItems> itemList = new ArrayList<>(3);

			for (OrderItems orderItems : orderItemList) {
				if (orderItems.getShopkeeperId().equals(shopKeeperId)) {
					status = true;
					totalAmount = totalAmount + (orderItems.getPrice() * orderItems.getQuantity());
					itemList.add(orderItems);
				}
			}

			if (status) {
				PurchaseOrder pOrder = new PurchaseOrder();
				pOrder.setOrderDate(order.getDate());
				pOrder.setOrderId(order.getOrderId());
				pOrder.setOrderStatus(order.getShippingStatus());
				pOrder.setTotalAmount(totalAmount);
				pOrder.setItemList(itemList);
				purchaseOrderList.add(pOrder);
				status = false;
			}
		}
		return purchaseOrderList;
	}

	public ArrayList<PurchaseOrder> getOnGoingOrder(String shopKeeperId)
			throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		ArrayList<PurchaseOrder> purchaseOrderList = new ArrayList<>();

		ApiFuture<QuerySnapshot> apiFuture = fireStore.collection("Order")
				.whereIn("shippingStatus", Arrays.asList("Dispatched", "Onway"))
				.orderBy("timestamp", Direction.DESCENDING).get();

		QuerySnapshot querySnapshot = apiFuture.get();

		List<QueryDocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();

		for (QueryDocumentSnapshot document : documentSnapshotList) {
			double totalAmount = 0;
			boolean status = false;

			Order order = document.toObject(Order.class);
			System.out.println(order.getOrderId());

			ArrayList<OrderItems> orderItemList = order.getItemList();
			ArrayList<OrderItems> itemList = new ArrayList<>(3);

			for (OrderItems orderItems : orderItemList) {
				if (orderItems.getShopkeeperId().equals(shopKeeperId)) {
					status = true;
					totalAmount = totalAmount + (orderItems.getPrice() * orderItems.getQuantity());
					itemList.add(orderItems);
				}
			}

			if (status) {
				PurchaseOrder pOrder = new PurchaseOrder();
				pOrder.setOrderDate(order.getDate());
				pOrder.setOrderId(order.getOrderId());
				pOrder.setOrderStatus(order.getShippingStatus());
				pOrder.setTotalAmount(totalAmount);
				pOrder.setItemList(itemList);
				purchaseOrderList.add(pOrder);
				status = false;
			}
		}
		return purchaseOrderList;
	}

}
