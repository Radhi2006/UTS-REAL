package com.radhi.utsreal.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service task "Validasi pengajuan": memeriksa data cuti sebelum diteruskan ke atasan.
 * Aturan: nama wajib diisi dan jumlah hari harus 1 - 12 (jatah cuti tahunan).
 */
@Component("validasiCutiDelegate")
public class ValidasiCutiDelegate implements JavaDelegate {

    public static final int MAKS_HARI_CUTI = 12;

    private static final Logger log = LoggerFactory.getLogger(ValidasiCutiDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String nama = (String) execution.getVariable("nama");
        Integer jumlahHari = (Integer) execution.getVariable("jumlahHari");

        String alasanTolak = null;
        if (nama == null || nama.isBlank()) {
            alasanTolak = "Nama karyawan kosong";
        } else if (jumlahHari == null || jumlahHari < 1) {
            alasanTolak = "Jumlah hari minimal 1";
        } else if (jumlahHari > MAKS_HARI_CUTI) {
            alasanTolak = "Jumlah hari melebihi jatah cuti (" + MAKS_HARI_CUTI + " hari)";
        }

        boolean valid = alasanTolak == null;
        execution.setVariable("valid", valid);
        if (!valid) {
            execution.setVariable("keterangan", alasanTolak);
        }
        log.info("Validasi cuti {} ({} hari): {}", nama, jumlahHari, valid ? "VALID" : alasanTolak);
    }
}
