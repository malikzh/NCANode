package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Pkcs12ArrayDataType extends ApiDependencies implements InputType {
    JSONArray p12array = null;
    List<KeyStore> keys = new ArrayList<>();
    List<String> passwords = new ArrayList<>();
    List<String> aliases = new ArrayList<>();

    @Override
    public void validate() throws InvalidArgumentException {
        for (Object o : p12array) {
            JSONObject item = (JSONObject) o;

            try {
                keys.add(getApiServiceProvider().pki.loadKey(Base64.getDecoder().decode((String) item.get("p12")),
                        (String) item.get("password")));
                passwords.add((String) item.get("password"));
                aliases.add((String) item.get("alias"));
            } catch (Exception e) {
                throw new InvalidArgumentException(e.getMessage());
            }
        }
    }

    @Override
    public void input(Object data) {
        p12array = new JSONArray();
        p12array.add(data);
    }

    public int size() {
        return keys.size();
    }

    public KeyStore getKey(int index) {
        return keys.get(index);
    }

    public String getAlias(int index) {
        return aliases.get(index);
    }

    public String getPassword(int index) {
        return passwords.get(index);
    }
}
