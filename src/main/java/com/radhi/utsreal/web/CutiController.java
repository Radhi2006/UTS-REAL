package com.radhi.utsreal.web;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.radhi.utsreal.service.CutiService;

@Controller
public class CutiController {

    private final CutiService cutiService;
    private final RuntimeService runtimeService;

    public CutiController(CutiService cutiService, RuntimeService runtimeService) {
        this.cutiService = cutiService;
        this.runtimeService = runtimeService;
    }

    /** Endpoint uji instalasi: menjalankan proses BPMN "hello-world". */
    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(defaultValue = "Mahasiswa") String nama) {
        var pi = runtimeService.createProcessInstanceByKey("hello-world")
                .setVariable("nama", nama)
                .executeWithVariablesInReturn();
        return pi.getVariables().get("pesan") + " (process instance: " + pi.getId() + ")";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("menunggu", cutiService.daftarMenungguPersetujuan());
        model.addAttribute("riwayat", cutiService.riwayat());
        return "index";
    }

    @PostMapping("/cuti")
    public String ajukan(@RequestParam String nama,
                         @RequestParam(required = false) Integer jumlahHari,
                         @RequestParam(required = false) String alasan,
                         RedirectAttributes ra) {
        var pi = cutiService.ajukanCuti(nama, jumlahHari, alasan);
        ra.addFlashAttribute("pesan", "Pengajuan cuti dikirim (process instance " + pi.getId() + ")");
        return "redirect:/";
    }

    @PostMapping("/task/{taskId}")
    public String putuskan(@PathVariable String taskId, @RequestParam boolean disetujui, RedirectAttributes ra) {
        cutiService.putuskan(taskId, disetujui);
        ra.addFlashAttribute("pesan", disetujui ? "Cuti disetujui" : "Cuti ditolak");
        return "redirect:/";
    }
}
