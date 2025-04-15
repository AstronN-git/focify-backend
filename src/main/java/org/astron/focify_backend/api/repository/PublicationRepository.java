package org.astron.focify_backend.api.repository;

import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends CrudRepository<Publication, Long> {
    List<Publication> findByAuthor(User author);
}
