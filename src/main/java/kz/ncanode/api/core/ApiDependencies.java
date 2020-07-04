package kz.ncanode.api.core;

import kz.ncanode.api.ApiServiceProvider;

public abstract class ApiDependencies {
    private ApiVersion apiVersion;
    private ApiServiceProvider apiServiceProvider;

    public void setDependencies(ApiVersion apiVersion, ApiServiceProvider apiServiceProvider) {
        this.apiVersion = apiVersion;
        this.apiServiceProvider = apiServiceProvider;
    }

    public ApiVersion getApiVersion() {
        return apiVersion;
    }

    public ApiServiceProvider getApiServiceProvider() {
        return apiServiceProvider;
    }
}
