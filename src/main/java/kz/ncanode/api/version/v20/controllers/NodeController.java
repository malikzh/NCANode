package kz.ncanode.api.version.v20.controllers;

import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import org.json.simple.JSONObject;

@ApiController("node")
public class NodeController extends kz.ncanode.api.core.ApiController {
    @ApiMethod(url = "info")
    public void info(JSONObject response) throws ApiErrorException {
        response.put("paramtest", "test123");
    }
}
