package sn.ditontineplateform.user.mapper;

import org.mapstruct.Mapper;
import sn.ditontineplateform.user.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto (Location location);

    Location toEntity (LocationDto locationDto);
}