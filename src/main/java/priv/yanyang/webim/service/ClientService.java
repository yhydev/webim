package priv.yanyang.webim.service;


import org.springframework.stereotype.Service;


@Service
public interface ClientService {


    void updateClientIndex(String clientIdIndexKey,long index);

    Integer getClientIndex(String clientIdIndexKey);



}
