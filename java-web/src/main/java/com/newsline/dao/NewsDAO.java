package com.newsline.dao;

import com.newsline.dto.NewsDTO;
import com.newsline.models.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface NewsDAO extends JpaRepository<News, UUID> {
    @Query(value = "SELECT new com.newsline.dto.NewsDTO (n.id, n.title, n.date, n.text, (select i.id from NewsImage as i where i.news.id = n.id), (n.createdBy = ?1 or ?2 = true)) FROM News as n where n.deleted = false order by n.date DESC"
            , countQuery = "select count(*) FROM News where deleted = false and (?1 is null or ?1 is not null or ?2 is not null)")
    Page<NewsDTO> getActualDTONewsOrderByDateAddModifiedMark(UUID userId, boolean isSuperuser, Pageable pageable);

    News findByIdAndDeletedFalse(UUID id);

    @Query(value = "SELECT new com.newsline.dto.NewsDTO (n.id, n.title, n.date, n.text, (select i.id from NewsImage as i where i.news.id = n.id), (n.createdBy = ?2 or ?3 = true)) FROM News as n where n.deleted = false and n.id = ?1 order by n.date")
    NewsDTO getNewsByIdAndDeletedFalseAddModifiedMark(UUID id, UUID userId, boolean isSuperuser);

    List<News> findByTitleAndDateAndDeletedFalse(String title, Date date);


    //    @Query(value = "SELECT n.id, n.title, n.date, n.text, i.id as imagesID, (n.created_by = ?1 or ?2 = true) as canUserModifyNews FROM news as n " +
//            "left join images i on n.id = i.news_id" +
//            " where n.deleted = false and (to_tsvector(n.text) @@ to_tsquery(?3)) order by n.date", nativeQuery = true)
//    List<News> findNewsWithFullTextSearch(UUID userId, boolean isSuperuser, String query);
    @Query(nativeQuery = true)
    List<NewsDTO> findNewsWithFullTextSearch(@Param("query") String query);
//    List<NewsDTO> findNewsWithFullTextSearch(@Param("userId") UUID userId, @Param("isSuperuser") boolean isSuperuser, @Param("query") String query);
}

//    SELECT n.id, n.title, n.date, n.text, i.id as imagesID, true as canUserModifyNews FROM news as n
//        join images i on n.id = i.news_id
//         where n.deleted = false and (to_tsvector(n.text) @@ to_tsquery('Alicia behind and Jay')) order by n.date;

