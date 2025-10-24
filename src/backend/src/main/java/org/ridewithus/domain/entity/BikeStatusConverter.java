package org.ridewithus.domain.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BikeStatusConverter implements AttributeConverter<BikeStatus, String> {

    @Override
    public String convertToDatabaseColumn(BikeStatus status) {
        if (status == null) return null;
        return status.name().toLowerCase(); // store lowercase in DB
    }

    @Override
    public BikeStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return BikeStatus.valueOf(dbData.toUpperCase()); // read lowercase and convert to enum
    }
}
