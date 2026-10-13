package com.radhi.utsreal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.radhi.utsreal.service.CutiService;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE")
class UtsRealApplicationTests {

    @Autowired
    CutiService cutiService;
    @Autowired
    TaskService taskService;
    @Autowired
    HistoryService historyService;

    @Test
    void cutiValidMenungguLaluDisetujui() {
        var pi = cutiService.ajukanCuti("Budi", 3, "Liburan");

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        assertEquals("Task_Persetujuan", task.getTaskDefinitionKey());

        cutiService.putuskan(task.getId(), true);
        assertEquals("DISETUJUI", variabel(pi.getId(), "status"));
    }

    @Test
    void cutiDitolakAtasan() {
        var pi = cutiService.ajukanCuti("Sari", 2, "Acara keluarga");
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        cutiService.putuskan(task.getId(), false);
        assertEquals("DITOLAK", variabel(pi.getId(), "status"));
    }

    @Test
    void cutiMelebihiJatahLangsungDitolak() {
        var pi = cutiService.ajukanCuti("Andi", 20, "Keliling dunia");

        assertTrue(pi.isEnded());
        assertEquals("DITOLAK", variabel(pi.getId(), "status"));
    }

    private Object variabel(String processInstanceId, String nama) {
        HistoricVariableInstance v = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).variableName(nama).singleResult();
        return v.getValue();
    }
}
