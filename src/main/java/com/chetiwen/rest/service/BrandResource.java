package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;

import com.chetiwen.cache.BrandCache;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.Brand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@Path("/brand")
public class BrandResource {
    private static Logger logger = LoggerFactory.getLogger(BrandResource.class);

    @GET
    @Path("/list")
    @Produces("application/json;charset=UTF-8")
    public Response listAll() {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Brand list request ");
        try {
            BrandCache.getInstance().refreshCache();

            List<Brand> dataList = BrandCache.getInstance().getBrandMap().values()
                    .stream().collect(Collectors.toList());

            logger.info("returned {} row(s) data", dataList.size());
            return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(dataList)).build();
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
