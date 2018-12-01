package priv.yanyang.webim.service;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ClientServiceImplTest {

    Logger logger = Logger.getLogger(ClientServiceImpl.class);

    @Autowired
    ClientService clientServiceImpl;

    String clientIndexKey = "test_clientIndexKey";


    @Test
    public void updateClientIndex() {
        clientServiceImpl.updateClientIndex(clientIndexKey,111);
    }

    @Test
    public void getClientIndex() {
        Integer clientIndex = clientServiceImpl.getClientIndex(clientIndexKey);
        System.out.println("clientIndex = " + clientIndex);

        clientIndex = clientServiceImpl.getClientIndex(String.valueOf(Math.random()));
        System.out.println("clientIndex = " + clientIndex);
    }
}