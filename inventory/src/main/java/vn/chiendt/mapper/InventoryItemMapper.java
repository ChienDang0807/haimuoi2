package vn.chiendt.mapper;

import org.mapstruct.Mapper;
import vn.chiendt.dto.response.InventoryItemResponse;
import vn.chiendt.model.InventoryItem;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    InventoryItemResponse toInventoryItemResponse(InventoryItem item);
}
