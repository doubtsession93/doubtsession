package com.hardwaremartapi.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.hardwaremartapi.FileUtility;
import com.hardwaremartapi.bean.Product;
import com.hardwaremartapi.bean.Shopkeeper;
import com.hardwaremartapi.bean.User;

@Service
public class ShopkeeperService {

	public Shopkeeper saveShopkeeper(MultipartFile file, Shopkeeper shopkeeper) throws Exception {
		Firestore firestoredatabase = FirestoreClient.getFirestore();
		FileUtility fileUtility = new FileUtility();
		String imageUrl = fileUtility.uploadFile(file);
		shopkeeper.setImageUrl(imageUrl);
		firestoredatabase.collection("Shopkeeper").document(shopkeeper.getShopKeeperId()).set(shopkeeper);
		return shopkeeper;
	}

	public Shopkeeper viewShopkeeper(String id) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Shopkeeper shopkeeper = fireStore.collection("Shopkeeper").document(id).get().get().toObject(Shopkeeper.class);
		return shopkeeper;
	}

	public Shopkeeper updateShopkeeper(Shopkeeper shopkeeper) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Shopkeeper s0 = fireStore.collection("Shopkeeper").document(shopkeeper.getShopKeeperId()).get().get()
				.toObject(Shopkeeper.class);
		shopkeeper.setImageUrl(s0.getImageUrl());
		fireStore.collection("Shopkeeper").document(shopkeeper.getShopKeeperId()).set(shopkeeper);
		return shopkeeper;
	}

	public Shopkeeper updateShopkeeperImage(MultipartFile file, String shopKeeperId)
			throws InterruptedException, ExecutionException, IOException {
		Firestore fireStore = FirestoreClient.getFirestore();
		Shopkeeper shopkeeper = fireStore.collection("Shopkeeper").document(shopKeeperId).get().get()
				.toObject(Shopkeeper.class);
		FileUtility fileUtility = new FileUtility();
		String imageUrl = fileUtility.uploadFile(file);
		shopkeeper.setImageUrl(imageUrl);
		fireStore.collection("Shopkeeper").document(shopKeeperId).set(shopkeeper);
		return shopkeeper;
	}
}
