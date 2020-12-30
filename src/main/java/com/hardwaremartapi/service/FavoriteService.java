package com.hardwaremartapi.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.firebase.cloud.FirestoreClient;
import com.hardwaremartapi.bean.Favorite;
import com.hardwaremartapi.exception.ResourceNotFoundException;

@Service
public class FavoriteService {


	public Favorite addFavorite(Favorite favorite) throws IOException {
		Firestore firestore = FirestoreClient.getFirestore();
		String favoriteId = firestore.collection("Favorite").document().getId().toString();
		favorite.setFavoriteId(favoriteId);
		firestore.collection("Favorite").document(favoriteId).set(favorite);
		return favorite;
	}

	public Favorite removeFavorite(String id)
			throws InterruptedException, ExecutionException, ResourceNotFoundException {
		Firestore firestore = FirestoreClient.getFirestore();
		DocumentReference documentReference = firestore.collection("Favorite").document(id);
		Favorite favorite = documentReference.get().get().toObject(Favorite.class);
		if (favorite != null)
			documentReference.delete();
		return favorite;
	}

	public List<Favorite> getFavorite(String userId) throws InterruptedException, ExecutionException {
		Firestore firestore = FirestoreClient.getFirestore();
		List<Favorite> list;
		Firestore fireStore = FirestoreClient.getFirestore();
		CollectionReference collectionReference = fireStore.collection("Favorite");
		Query queryi = collectionReference.whereEqualTo("userId", userId);
		list = queryi.get().get().toObjects(Favorite.class);
		return list;
	}
}
