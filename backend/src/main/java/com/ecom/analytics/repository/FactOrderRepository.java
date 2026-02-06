package com.ecom.analytics.repository;

import com.ecom.analytics.model.FactOrder;
import com.ecom.analytics.model.FactOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactOrderRepository extends JpaRepository<FactOrder, FactOrderId> {}
