package com.example.demo;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends FirestoreReactiveRepository<User> {
}
