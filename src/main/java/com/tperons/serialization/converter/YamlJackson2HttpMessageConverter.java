package com.tperons.serialization.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

@SuppressWarnings({ "removal" })
public final class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    protected YamlJackson2HttpMessageConverter() {
        super(YAMLMapper.builder()
                .defaultPropertyInclusion(
                        JsonInclude.Value
                                .construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .build(),
                MediaType
                        .parseMediaType("application/yaml"));
    }

}
