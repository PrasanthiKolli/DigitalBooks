package com.digitalbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalbooks.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}
