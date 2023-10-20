package com.broker.dto;

import java.io.Serializable;

import java.io.Serializable;
import java.util.UUID;

public record TokenDTO(UUID id, String value, String email) implements Serializable {
}