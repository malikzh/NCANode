package kz.ncanode.api.version.v10.arguments;

import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.util.ArrayList;

public class UseTsaPolicyArgument extends ApiArgument {
    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private String useTsaPolicy = "TSA_GOST_POLICY";
    private ArrayList<String> tsaPolicies = null;

    public UseTsaPolicyArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
        tsaPolicies = new ArrayList<>();

        tsaPolicies.add("TSA_GOST_POLICY");
        tsaPolicies.add("TSA_GOSTGT_POLICY");
    }


    @Override
    public void validate() throws InvalidArgumentException {
        boolean createTsp = (boolean)(params.get("createTsp") == null ? false : params.get("createTsp"));

        // Если не указан параметр createTsp, то смысла проверки алгоритма хэширования TSP нет
        if (!createTsp) {
            return;
        }

        String useTsa = (String)params.get("useTsaPolicy");

        if (useTsa != null) {
            if (tsaPolicies.indexOf(useTsa) == -1) {
                throw new InvalidArgumentException("Invalid parameter useTsaPolicy");
            } else {
                useTsaPolicy = useTsa;
            }
        } else if (useTsa == null && required) {
            throw new InvalidArgumentException("Argument 'useTsaPolicy' is required");
        }
    }

    @Override
    public Object get() {
        if (useTsaPolicy.equals("TSA_GOSTGT_POLICY")) {
            return KNCAObjectIdentifiers.tsa_gostgt_policy.getId();
        } else {
            return KNCAObjectIdentifiers.tsa_gost_policy.getId();
        }
    }

    @Override
    public String name() {
        return "useTsaPolicy";
    }
}
