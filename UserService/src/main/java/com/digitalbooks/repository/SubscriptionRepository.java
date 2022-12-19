package com.digitalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalbooks.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	
	List<Subscription> findByBookIdAndUserId(Long bookId, Long userId);
	
	Boolean existsByBookIdAndUserId(Long bookId, Long userId);

}
