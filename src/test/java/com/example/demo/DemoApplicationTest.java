package com.example.demo;

import com.google.api.core.ApiFuture;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
class DemoApplicationTest {

	@Container
	private static final FirestoreEmulatorContainer firestoreEmulator =
			new FirestoreEmulatorContainer(
					DockerImageName.parse(
							"gcr.io/google.com/cloudsdktool/google-cloud-cli:emulators"));

	@DynamicPropertySource
	static void emulatorProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.gcp.firestore.host-port",
				firestoreEmulator::getEmulatorEndpoint);
	}

	@Autowired
	private UserRepository repository;

	@Autowired
	private Firestore firestore;

	@SneakyThrows
	@Test
	void should_persist_user_with_sdk() {
		FirestoreOptions options = FirestoreOptions
				.getDefaultInstance()
				.toBuilder()
				.setHost(firestoreEmulator.getEmulatorEndpoint())
				.setCredentials(NoCredentials.getInstance())
				.setProjectId("test-project")
				.build();
		Firestore firestoreModified = options.getService();

		CollectionReference users = firestoreModified.collection("users");
		DocumentReference docRef = users.document("id-1");
		Map<String, Object> data = new HashMap<>();
		data.put("first", "Ada");
		data.put("last", "Lovelace");
		ApiFuture<WriteResult> result = docRef.set(data);
		result.get();

		ApiFuture<QuerySnapshot> query = users.get();
		QuerySnapshot querySnapshot = query.get();

		assertThat(querySnapshot.getDocuments().get(0).getData()).containsEntry("first", "Ada");
	}

	@SneakyThrows
	@Test
	void should_persist_user_with_sdk_from_context() {
		CollectionReference users = firestore.collection("users");
		DocumentReference docRef = users.document("id-1");
		Map<String, Object> data = new HashMap<>();
		data.put("first", "Ada");
		data.put("last", "Lovelace");
		ApiFuture<WriteResult> result = docRef.set(data);
		result.get();

		ApiFuture<QuerySnapshot> query = users.get();
		QuerySnapshot querySnapshot = query.get();

		assertThat(querySnapshot.getDocuments().get(0).getData()).containsEntry("first", "Ada");
	}

	@Test
	void should_persist_user() {
		var user = new User("id-2", "Ada", "Lovelace");

		var persistedUser = repository.save(user).block();

		assertNotNull(persistedUser);
	}
}
