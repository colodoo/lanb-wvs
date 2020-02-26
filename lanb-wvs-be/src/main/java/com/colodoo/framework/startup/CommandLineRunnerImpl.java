package com.colodoo.framework.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandLineRunnerImpl implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String url = "http://localhost:1234/";
        System.out.println("  _                _\n" +
                " | |    __ _ _ __ | |__\n" +
                " | |   / _` | '_ \\| '_ \\\n" +
                " | |__| (_| | | | | |_) |\n" +
                " |_____\\__,_|_| |_|_.__/\n");
        System.out.println("启动完毕，地址：" + url);
    }
}