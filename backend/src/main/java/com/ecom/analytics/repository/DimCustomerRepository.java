package com.ecom.analytics.repository;

import com.ecom.analytics.model.DimCustomer;
import com.ecom.analytics.model.DimCustomerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimCustomerRepository extends JpaRepository<DimCustomer, DimCustomerId> {}
