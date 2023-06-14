package com.crisado.delivery.repository;

import com.crisado.delivery.model.Rider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface RiderRepository extends Repository<Rider, Integer> {

    Optional<Rider> findById(int id);

    List<Rider> findAll();
}
