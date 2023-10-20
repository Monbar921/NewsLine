package com.newsline.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newsline.dto.NewsDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.sql.Timestamp;

import java.util.Date;
import java.util.UUID;


@Entity
@NamedNativeQuery(
        name = "News.findNewsWithFullTextSearch",
        query = "SELECT n.id as id, n.title as title, n.date as date, n.text as text, i.id as image_id FROM news as n" +
                " left join (select id, news_id from images) i on n.id = i.news_id" +
                " where (n.textsearchable_index_col @@ to_tsquery(:query)) and n.deleted = false order by n.date DESC;",
        resultSetMapping = "Mapping.NewsDTOMapping"
)
@SqlResultSetMapping(
        name = "Mapping.NewsDTOMapping",
        classes = @ConstructorResult(
                targetClass = NewsDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = UUID.class),
                        @ColumnResult(name = "title", type = String.class),
                        @ColumnResult(name = "date", type = Date.class),
                        @ColumnResult(name = "text", type = String.class),
                        @ColumnResult(name = "image_id", type = UUID.class)
                }
        )
)

@Table(name = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
//@Indexed
public final class News {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @NotNull
    @NotEmpty
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date;
    @NotNull
    @NotEmpty
    private String text;

    @OneToOne(mappedBy = "news")
    private NewsImage newsImage;

    private boolean deleted;

    @UpdateTimestamp
    private Timestamp updatedOn;

    @CreationTimestamp
    private Timestamp createdOn;
    @NotNull
    private UUID createdBy;

    @NotNull
    private UUID updatedBy;

    public News(NewsDTO newsDTO, @NotNull UUID updatedBy){
        title = newsDTO.getTitle();
        date = newsDTO.getDate();
        text = newsDTO.getText();
        this.updatedBy = updatedBy;
    }

    public News(){

    }
}

