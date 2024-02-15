package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByLabel(String hashtag);

    Optional<Hashtag> findByLabelIgnoreCase(String label);
    
    List<Hashtag> findByTweets_Id(Long id);

}
