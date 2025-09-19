package com.payment.repository;

import com.payment.entity.Payment;
import com.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Buscar apenas pagamentos ativos
    List<Payment> findByAtivoTrue();

    // Buscar pagamento ativo por ID
    Optional<Payment> findByIdAndAtivoTrue(Long id);

    // Filtros de busca
    @Query("SELECT p FROM Payment p WHERE p.ativo = true " +
           "AND (:codigoDebito IS NULL OR p.codigoDebito = :codigoDebito) " +
           "AND (:cpfCnpj IS NULL OR p.cpfCnpj = :cpfCnpj) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Payment> findByFilters(@Param("codigoDebito") Integer codigoDebito,
                               @Param("cpfCnpj") String cpfCnpj,
                               @Param("status") PaymentStatus status);

    // Buscar por código de débito
    List<Payment> findByCodigoDebitoAndAtivoTrue(Integer codigoDebito);

    // Buscar por CPF/CNPJ
    List<Payment> findByCpfCnpjAndAtivoTrue(String cpfCnpj);

    // Buscar por status
    List<Payment> findByStatusAndAtivoTrue(PaymentStatus status);
}
