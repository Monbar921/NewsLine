package com.newsline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentForUpdate {
    @NotNull
    @NotEmpty
    @NotBlank
    private String text;
}
