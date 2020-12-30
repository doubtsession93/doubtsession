package com.hardwaremartapi.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.firestore.Firestore;

import com.google.firebase.cloud.FirestoreClient;
import com.hardwaremartapi.FileUtility;
import com.hardwaremartapi.bean.User;

@Service
public class UserService {

	FileUtility fileUtility = new FileUtility();
	
	
	public User saveUser(MultipartFile file, User user) throws Exception {
		
		Firestore firestoredatabase = FirestoreClient.getFirestore();
		String imageUrl = fileUtility.uploadFile(file);
		user.setImageUrl(imageUrl);
		firestoredatabase.collection("User").document(user.getUserId()).set(user);
		return user;
	}

	public User updateUser(User user) throws InterruptedException, ExecutionException {
		Firestore fireStore = FirestoreClient.getFirestore();
		User u = fireStore.collection("User").document(user.getUserId()).get().get().toObject(User.class);
		user.setImageUrl(u.getImageUrl());
		fireStore.collection("User").document(user.getUserId()).set(user);
		return user;
	}

	public User updateUserImage(MultipartFile file, String userId)
			throws InterruptedException, ExecutionException, IOException {
		Firestore fireStore = FirestoreClient.getFirestore();
		User user = fireStore.collection("User").document(userId).get().get().toObject(User.class);
		FileUtility fileUtility = new FileUtility();
		String imageUrl = fileUtility.uploadFile(file);
		user.setImageUrl(imageUrl);
		fireStore.collection("User").document(userId).set(user);
		return user;
	}
	
	public User updateUser(MultipartFile file, User user)
			throws IOException, InterruptedException, Exception {
		Firestore fireStore = FirestoreClient.getFirestore();
		String imageUrl = fileUtility.uploadFile(file);	
		user.setImageUrl(imageUrl);
		user.setUserId(user.getUserId());
		fireStore.collection("User").document(user.getUserId()).set(user);
		return user;
	}
	
	public User updateUserWithoutImage(User user) throws IOException, InterruptedException, Exception {
		Firestore fireStore = FirestoreClient.getFirestore();
		fireStore.collection("User").document(user.getUserId()).set(user);
		return user;
	}

	public User getUserDetails(String currentUserId) throws Exception, Exception {
		Firestore fireStore = FirestoreClient.getFirestore();
	    User user = fireStore.collection("User").document(currentUserId).get().get().toObject(User.class); 	
		return user;
	}
}