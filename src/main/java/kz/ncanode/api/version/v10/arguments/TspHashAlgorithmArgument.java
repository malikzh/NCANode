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
    private final boolean required;
    private String hashAlgorithm = "GOST34311";
    private final ArrayList<String> hashAlgorithms;

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
            if (!hashAlgorithms.contains(halg)) {
                throw new InvalidArgumentException("Unknown TSP hashing algorithm");
            } else {
                hashAlgorithm = halg;
            }
        } else if (required) {
            throw new InvalidArgumentException("Argument 'tspHashAlgorithm' is required");
        }
    }

    @Override
    public Object get() {
        switch (hashAlgorithm) {
            case "MD5":
                return TSPAlgorithms.MD5;
            case "SHA1":
                return TSPAlgorithms.SHA1;
            case "SHA224":
                return TSPAlgorithms.SHA224;
            case "SHA256":
                return TSPAlgorithms.SHA256;
            case "SHA384":
                return TSPAlgorithms.SHA384;
            case "SHA512":
                return TSPAlgorithms.SHA512;
            case "RIPEMD128":
                return TSPAlgorithms.RIPEMD128;
            case "RIPEMD160":
                return TSPAlgorithms.RIPEMD160;
            case "RIPEMD256":
                return TSPAlgorithms.RIPEMD256;
            case "GOST34311GT":
                return TSPAlgorithms.GOST34311GT;
            default:
                return TSPAlgorithms.GOST34311;
        }
    }

    @Override
    public String name() {
        return "tspHashAlgorithm";
    }
}
