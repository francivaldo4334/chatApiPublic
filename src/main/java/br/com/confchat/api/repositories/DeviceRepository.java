package br.com.confchat.api.repositories;

import br.com.confchat.api.models.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<DeviceModel, UUID> {
    Optional<DeviceModel> findByIdAndUserId(UUID id, UUID userid);
    Collection<DeviceModel> findAllByUserId(UUID userid);
}
