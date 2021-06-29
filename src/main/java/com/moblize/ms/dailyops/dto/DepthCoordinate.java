package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor
public class DepthCoordinate {
    @ProtoField(number = 1, collectionImplementation = ArrayList.class)
    @JsonIgnore
    List<Location> protoCoordiantes = new ArrayList<>();

}
