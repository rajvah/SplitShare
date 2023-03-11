package com.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import liquibase.pro.packaged.D;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Harshit Rajvaidya
 */
public class SplitShareConfiguration extends Configuration{
    @NotNull
    @Valid
    private DataSourceFactory dataSourceFactory;

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        if(Objects.isNull(dataSourceFactory)){
            dataSourceFactory = new DataSourceFactory();
        }
        return dataSourceFactory;
    }


}
