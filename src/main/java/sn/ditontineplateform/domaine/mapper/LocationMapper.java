package sn.ditontineplateform.domaine.mapper;

import org.mapstruct.Mapper;
import sn.ditontineplateform.domaine.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto (Location location);

    Location toEntity (LocationDto locationDto);
}