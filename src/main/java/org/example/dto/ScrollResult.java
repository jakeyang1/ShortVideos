package org.example.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
