package kz.ncanode.api.version.v20.controllers;

import kz.ncanode.Helper;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.Date;

@ApiController("node")
public class NodeController extends kz.ncanode.api.core.ApiController {
    @ApiMethod(url = "info")
    public void info(JSONObject resp) throws ApiErrorException {
        resp.put("version", getApiServiceProvider().info.getVersion());
        resp.put("kalkanVersion", getApiServiceProvider().kalkan.getVersion());
        resp.put("name", getApiServiceProvider().info.getFullName());
        resp.put("dateTime", Helper.dateTime(new Date()));
        resp.put("timezone", Calendar.getInstance().getTimeZone().toZoneId().toString());
        resp.put("status", ApiStatus.STATUS_OK);
        resp.put("message", "");
    }
}
