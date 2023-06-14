package com.crisado.delivery.repository;

import com.crisado.delivery.model.BankAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface BankAccountRepository extends Repository<BankAccount, Integer> {

    List<BankAccount> findAll();

    Optional<BankAccount> findById(int id);

}
