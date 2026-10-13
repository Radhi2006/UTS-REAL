package com.radhi.utsreal.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service task pada proses "hello-world". Dipakai untuk mengetes bahwa
 * Camunda engine sudah terpasang dan bisa memanggil bean Spring.
 */
@Component("helloWorldDelegate")
public class HelloWorldDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String nama = (String) execution.getVariable("nama");
        String pesan = "Hello World dari Camunda + Spring Boot" + (nama != null ? ", " + nama : "") + "!";
        log.info(pesan);
        execution.setVariable("pesan", pesan);
    }
}
