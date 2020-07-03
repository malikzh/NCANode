package kz.ncanode.api.version.v20.controllers;

import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.version.v20.models.InfoX509Model;
import org.json.simple.JSONObject;

@ApiController("info")
public class InfoController extends kz.ncanode.api.core.ApiController {

    @ApiMethod(url = "x509")
    public void x509(InfoX509Model model, JSONObject response) {
        response.put("param", "value");
    }
}
