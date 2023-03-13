package com.example.postgretest.init;

import com.example.postgretest.entity.base.Table;
import com.example.postgretest.service.impl.TsKvServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;

@Slf4j
@Component
public class InitServer implements CommandLineRunner {

	@Resource
	private TsKvServiceImpl tsKvService;

	@Autowired
	private Environment env;

	@Override
	public void run(String... args) throws Exception {
		// init timescale
		//
//		tsKvService.createDistributedHypertable("ts_kv","ts");
		tsKvService.createHypertable(Table.builder().tableName("ts_kv").column("ts").build());


        String ip = InetAddress.getLocalHost().getHostAddress();
        String httpPort = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");

        log.info("\n--------------------------------------------------------- \n" +
                "\t swagger is running! Access address: \n" +
                "\t http port at : \t {} \n" +
                "\t swagger Local: \t http://localhost:{}{}/doc.html \n" +
                "\t swagger web External: \t http://{}:{}{}/doc.html \n" +
                "--------------------------------------------------------- \n",
                httpPort, 
                httpPort, path,
                ip, httpPort, path);
	}

}
