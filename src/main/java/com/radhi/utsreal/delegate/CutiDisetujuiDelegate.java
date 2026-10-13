package com.radhi.utsreal.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Service task "Catat cuti disetujui". */
@Component("cutiDisetujuiDelegate")
public class CutiDisetujuiDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CutiDisetujuiDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("status", "DISETUJUI");
        execution.setVariable("keterangan", "Cuti disetujui oleh atasan");
        log.info("[NOTIFIKASI] Cuti {} selama {} hari DISETUJUI",
                execution.getVariable("nama"), execution.getVariable("jumlahHari"));
    }
}
