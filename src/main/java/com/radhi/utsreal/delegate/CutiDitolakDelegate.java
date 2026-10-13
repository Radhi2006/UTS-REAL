package com.radhi.utsreal.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service task "Catat cuti ditolak". Dipanggil dari dua jalur:
 * data tidak valid, atau atasan menolak.
 */
@Component("cutiDitolakDelegate")
public class CutiDitolakDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CutiDitolakDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        if (!execution.hasVariable("keterangan")) {
            execution.setVariable("keterangan", "Ditolak oleh atasan");
        }
        execution.setVariable("status", "DITOLAK");
        log.info("[NOTIFIKASI] Cuti {} DITOLAK: {}",
                execution.getVariable("nama"), execution.getVariable("keterangan"));
    }
}
