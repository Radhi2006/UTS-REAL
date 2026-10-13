package com.radhi.utsreal.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

/** Lapisan service yang menghubungkan aplikasi web dengan Camunda engine. */
@Service
public class CutiService {

    public static final String PROCESS_KEY = "pengajuan-cuti";
    public static final String TASK_PERSETUJUAN = "Task_Persetujuan";

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public CutiService(RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    public ProcessInstance ajukanCuti(String nama, Integer jumlahHari, String alasan) {
        return runtimeService.startProcessInstanceByKey(PROCESS_KEY, Map.of(
                "nama", nama == null ? "" : nama.trim(),
                "jumlahHari", jumlahHari == null ? 0 : jumlahHari,
                "alasan", alasan == null ? "" : alasan.trim(),
                "status", "MENUNGGU PERSETUJUAN"));
    }

    /** Task "Persetujuan atasan" yang masih menunggu keputusan, beserta variabel prosesnya. */
    public List<TaskMenunggu> daftarMenungguPersetujuan() {
        return taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_KEY)
                .taskDefinitionKey(TASK_PERSETUJUAN)
                .orderByTaskCreateTime().desc()
                .list().stream()
                .map(t -> new TaskMenunggu(t, taskService.getVariables(t.getId())))
                .toList();
    }

    public void putuskan(String taskId, boolean disetujui) {
        taskService.complete(taskId, Map.of("disetujui", disetujui));
    }

    /** Riwayat semua pengajuan (yang masih berjalan maupun selesai) beserta variabelnya. */
    public List<Riwayat> riwayat() {
        return historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .list().stream()
                .sorted(Comparator.comparing(HistoricProcessInstance::getStartTime).reversed())
                .map(this::keRiwayat)
                .toList();
    }

    private Riwayat keRiwayat(HistoricProcessInstance pi) {
        Map<String, Object> vars = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(pi.getId())
                .list().stream()
                .filter(v -> v.getValue() != null)
                .collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue));
        return new Riwayat(pi.getId(), pi.getStartTime(), vars);
    }

    public record TaskMenunggu(Task task, Map<String, Object> vars) {
    }

    public record Riwayat(String id, Date mulai, Map<String, Object> vars) {
        public Object get(String nama) {
            return vars.getOrDefault(nama, "-");
        }
    }
}
