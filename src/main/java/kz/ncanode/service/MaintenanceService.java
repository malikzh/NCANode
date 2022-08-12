package kz.ncanode.service;

import org.springframework.stereotype.Service;

/**
 * Сервис - Maintenance.
 *
 * Содержит в себе методы для получения версии и общего состояния системы.
 */
@Service
public class MaintenanceService {
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    public String getName() {
        return getClass().getPackage().getImplementationTitle();
    }
}
