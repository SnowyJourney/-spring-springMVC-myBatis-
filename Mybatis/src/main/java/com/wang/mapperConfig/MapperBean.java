package com.wang.mapperConfig;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MapperBean {

    private String interfaceName;

    private List<Function> functions;

    @Override
    public String toString() {
        return "MapperBean{" +
                "interfaceName='" + interfaceName + '\'' +
                ", functions=" + functions +
                '}';
    }
}
