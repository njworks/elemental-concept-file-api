package com.technical.database.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IpLoggingRepository extends JpaRepository<IpLoggingEntity, UUID> {
}
