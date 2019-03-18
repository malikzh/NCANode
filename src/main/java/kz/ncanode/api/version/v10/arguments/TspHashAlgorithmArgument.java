package kz.ncanode.api.version.v10.arguments;

import kz.gov.pki.kalkan.tsp.TSPAlgorithms;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.util.ArrayList;

public class TspHashAlgorithmArgument extends ApiArgument {
    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private String hashAlgorithm = "GOST34311";
    private ArrayList<String> hashAlgorithms = null;

    public TspHashAlgorithmArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
        this.hashAlgorithms = new ArrayList<>();

        // Add default algorithms to list
        hashAlgorithms.add("MD5");
        hashAlgorithms.add("SHA1");
        hashAlgorithms.add("SHA224");
        hashAlgorithms.add("SHA256");
        hashAlgorithms.add("SHA384");
        hashAlgorithms.add("SHA512");
        hashAlgorithms.add("RIPEMD128");
        hashAlgorithms.add("RIPEMD160");
        hashAlgorithms.add("RIPEMD256");
        hashAlgorithms.add("GOST34311GT");
        hashAlgorithms.add("GOST34311");
    }

    @Override
    public void validate() throws InvalidArgumentException {
        boolean createTsp = (boolean)(params.get("createTsp") == null ? false : params.get("createTsp"));

        // Если не указан параметр createTsp, то смысла проверки алгоритма хэширования TSP нет
        if (!createTsp) {
            return;
        }

        String halg = (String)params.get("tspHashAlgorithm");

        if (halg != null) {
            if (hashAlgorithms.indexOf(halg) == -1) {
                throw new InvalidArgumentException("Unknown TSP hashing algorithm");
            } else {
                hashAlgorithm = halg;
            }
        } else if (halg == null && required) {
            throw new InvalidArgumentException("Argument 'tspHashAlgorithm' is required");
        }
    }

    @Override
    public Object get() {
        if (hashAlgorithm.equals("MD5")) {
            return TSPAlgorithms.MD5;
        }
        else if (hashAlgorithm.equals("SHA1")) {
            return TSPAlgorithms.SHA1;
        }
        else if (hashAlgorithm.equals("SHA224")) {
            return TSPAlgorithms.SHA224;
        }
        else if (hashAlgorithm.equals("SHA256")) {
            return TSPAlgorithms.SHA256;
        }
        else if (hashAlgorithm.equals("SHA384")) {
            return TSPAlgorithms.SHA384;
        }
        else if (hashAlgorithm.equals("SHA512")) {
            return TSPAlgorithms.SHA512;
        }
        else if (hashAlgorithm.equals("RIPEMD128")) {
            return TSPAlgorithms.RIPEMD128;
        }
        else if (hashAlgorithm.equals("RIPEMD160")) {
            return TSPAlgorithms.RIPEMD160;
        }
        else if (hashAlgorithm.equals("RIPEMD256")) {
            return TSPAlgorithms.RIPEMD256;
        }
        else if (hashAlgorithm.equals("GOST34311GT")) {
            return TSPAlgorithms.GOST34311GT;
        }
        else {
            return TSPAlgorithms.GOST34311;
        }
    }

    @Override
    public String name() {
        return "tspHashAlgorithm";
    }
}
