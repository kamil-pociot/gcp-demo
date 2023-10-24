package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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

	@Test
	void should_persist_user() {
		var user = new User("id");

		var persistedUser = repository.save(user).block();

		assertNotNull(persistedUser);
	}
}
