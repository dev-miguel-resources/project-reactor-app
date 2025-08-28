package com.reactor.reactor.services.impl;

import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Client;
import com.reactor.reactor.repositories.IClientRepo;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.services.IClientService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client, String> implements IClientService {

    private final IClientRepo repo;

    @Override
    protected IGenericRepo<Client, String> getRepo() {
        return repo;
    }

}
