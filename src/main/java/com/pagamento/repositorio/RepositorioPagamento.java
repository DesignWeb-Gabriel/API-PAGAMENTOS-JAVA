package com.pagamento.repositorio;

import com.pagamento.entidade.Pagamento;
import com.pagamento.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioPagamento extends JpaRepository<Pagamento, Long> {

    // Buscar apenas pagamentos ativos
    List<Pagamento> findByAtivoTrue();

    // Buscar pagamento ativo por ID
    Optional<Pagamento> findByIdAndAtivoTrue(Long id);

    // Filtros de busca
    @Query("SELECT p FROM Pagamento p WHERE p.ativo = true " +
           "AND (:codigoDebito IS NULL OR p.codigoDebito = :codigoDebito) " +
           "AND (:cpfCnpj IS NULL OR p.cpfCnpj = :cpfCnpj) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Pagamento> encontrarComFiltros(@Param("codigoDebito") Integer codigoDebito,
                                       @Param("cpfCnpj") String cpfCnpj,
                                       @Param("status") StatusPagamento status);

    // Buscar por código de débito
    List<Pagamento> findByCodigoDebitoAndAtivoTrue(Integer codigoDebito);

    // Buscar por CPF/CNPJ
    List<Pagamento> findByCpfCnpjAndAtivoTrue(String cpfCnpj);

    // Buscar por status
    List<Pagamento> findByStatusAndAtivoTrue(StatusPagamento status);
}
