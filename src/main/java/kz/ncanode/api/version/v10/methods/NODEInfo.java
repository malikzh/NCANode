package kz.ncanode.api.version.v10.methods;

import kz.ncanode.Helper;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NODEInfo extends ApiMethod {
    public NODEInfo(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        JSONObject resp = new JSONObject();

        resp.put("version", man.info.getVersion());
        resp.put("kalkanVersion", man.kalkan.getVersion());
        resp.put("name", man.info.getFullName());
        resp.put("dateTime", Helper.dateTime(new Date()));
        resp.put("timezone", Calendar.getInstance().getTimeZone().toZoneId().toString());

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        return null;
    }
}
