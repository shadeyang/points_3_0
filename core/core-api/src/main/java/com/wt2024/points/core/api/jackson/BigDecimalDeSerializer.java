package com.wt2024.points.core.api.jackson;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/20 13:48
 * @project points3.0:com.wt2024.points.core.api.jackson
 */
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import java.io.IOException;
import java.math.BigDecimal;

@JsonComponent
public class BigDecimalDeSerializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        return new BigDecimal(jsonParser.getText().replaceAll(",",""));
    }
}