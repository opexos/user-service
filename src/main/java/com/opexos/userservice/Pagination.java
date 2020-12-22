package com.opexos.userservice;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
public class Pagination {

    @ApiParam("Page number (default 0)")
    @Min(0)
    private int pageNum = 0;

    @ApiParam("Page size (default 20)")
    @Range(min = 1, max = 100)
    private int pageSize = 20;

}
