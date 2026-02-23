package com.shopwise.repository;

import com.shopwise.entity.PointTransaction;
import com.shopwise.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    List<PointTransaction> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<PointTransaction> findByTransactionType(TransactionType type);

    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt WHERE pt.client.id = :clientId")
    Integer sumPointsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt WHERE pt.client.id = :clientId AND pt.transactionType = :type")
    Integer sumPointsByClientIdAndType(@Param("clientId") Long clientId, @Param("type") TransactionType type);

    boolean existsByAppointmentId(Long appointmentId);
}
