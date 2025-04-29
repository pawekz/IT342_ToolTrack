package edu.cit.tooltrack.dto;

import edu.cit.tooltrack.entity.ToolItems;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;


@NoArgsConstructor
@Getter
@Setter
public class ToolBorrowDTO {
        private int tool_id;
        private String category;
        private String name;
        private String qr_code;
        private String location;
        private String description;
        private Timestamp date_acquired;
        private String image_url;
        private Timestamp created_at;
        private Timestamp updated_at;

        public ToolBorrowDTO(ToolItems toolItem) {
                this.tool_id = toolItem.getTool_id();
                this.category = toolItem.getCategory();
                this.name = toolItem.getName();
                this.qr_code = toolItem.getQr_code();
                this.location = toolItem.getLocation();
                this.description = toolItem.getDescription();
                this.date_acquired = toolItem.getDate_acquired();
                this.image_url = toolItem.getImage_url();;
                this.created_at = toolItem.getCreated_at();
                this.updated_at = toolItem.getUpdated_at();
        }
}
