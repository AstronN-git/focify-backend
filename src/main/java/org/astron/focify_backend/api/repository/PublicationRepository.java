package org.astron.focify_backend.api.repository;

import org.astron.focify_backend.api.entity.Publication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends CrudRepository<Publication, Long> {
}
