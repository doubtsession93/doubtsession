package com.hardwaremartapi.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.hardwaremartapi.FileUtility;
import com.hardwaremartapi.bean.OrderItems;

@Service
public class OrderItemService {
	
	
	
	public OrderItems getItems(MultipartFile file, OrderItems items, String orderId) throws IOException {
		Firestore fireStore = FirestoreClient.getFirestore();
		String imageUrl = new FileUtility().uploadFile(file);
	    items.setImageUrl(imageUrl);
   	    String productId = fireStore.collection("OrderHistory").document().getId().toString();
	    items.setProductId(productId);
	    fireStore.collection("OrderHistory").document(orderId).collection("OrderItems").document(items.getProductId()).set(items); 
	    return items;
	}
}
