package com.wang.mapperConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Function {
    private String sqlType;
    private String methodId;
    private String parameterType;
    private Object resultType;
    private String sql;
}
