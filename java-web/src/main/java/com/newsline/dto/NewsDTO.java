package com.newsline.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Schema(description = "Entity of News")
@Setter
@Getter
@NoArgsConstructor
public final class NewsDTO {
    private UUID id;
    private String title;
    private Date date;
    private String text;
    private List<UUID> imagesID;
    private boolean canUserModifyNews = false;

    public NewsDTO(UUID id, String title, Date date, String text, UUID imageId, boolean canUserModifyNews) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.text = text;
        imagesID = new ArrayList<>();
        if(imageId != null){
            imagesID.add(imageId);
        }
        this.canUserModifyNews = canUserModifyNews;
    }

    public NewsDTO(UUID id, String title, Date date, String text, UUID imageId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.text = text;
        imagesID = new ArrayList<>();
        if(imageId != null){
            imagesID.add(imageId);
        }
    }
}
