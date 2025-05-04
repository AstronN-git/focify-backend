package org.astron.focify_backend.api.repository;

import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends CrudRepository<Publication, Long> {
    List<Publication> findByAuthor(User author);

    @Query("select p from Publication p where p.author in (select u.friends from User u where u = ?1)")
    Page<Publication> buildFeed(User user, Pageable pageable);
}
