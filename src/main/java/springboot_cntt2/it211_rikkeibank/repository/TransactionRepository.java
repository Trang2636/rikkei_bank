package springboot_cntt2.it211_rikkeibank.repository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import springboot_cntt2.it211_rikkeibank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            select t from Transaction t
            where t.fromAccount.accountNumber = :accountNumber
               or t.toAccount.accountNumber = :accountNumber
            """)
    Page<Transaction> findStatementByAccountNumber(
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );
}
