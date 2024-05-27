package org.example.utils.Redis;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReidsData {

    private LocalDateTime expireTime;

    private Object data;
}
