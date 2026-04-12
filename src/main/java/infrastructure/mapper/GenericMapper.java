package infrastructure.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic mapper using Jackson ObjectMapper.
 * Converts between Entity ↔ DTO without manual field mapping.
 *
 * Usage:
 *   HoaDonDTO dto = GenericMapper.map(hoaDon, HoaDonDTO.class);
 *   HoaDon entity = GenericMapper.map(dto, HoaDon.class);
 *   List<HoaDonDTO> dtos = GenericMapper.mapList(entities, HoaDonDTO.class);
 */
public class GenericMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private GenericMapper() {}

    /**
     * Map a single object to the target class.
     */
    public static <T> T map(Object source, Class<T> targetClass) {
        if (source == null) return null;
        return MAPPER.convertValue(source, targetClass);
    }

    /**
     * Map a list of objects to a list of the target class.
     */
    public static <T> List<T> mapList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null) return List.of();
        return sourceList.stream()
                .map(item -> map(item, targetClass))
                .collect(Collectors.toList());
    }
}
