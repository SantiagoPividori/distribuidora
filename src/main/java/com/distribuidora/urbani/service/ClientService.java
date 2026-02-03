package com.distribuidora.urbani.service;

import com.distribuidora.urbani.dto.ClientRequest;
import com.distribuidora.urbani.entity.Client;
import com.distribuidora.urbani.exceptions.ResourceNotFoundException;
import com.distribuidora.urbani.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client save(ClientRequest request) {
        Client client = Client.builder()
                .id(UUID.randomUUID())
                .businessName(request.businessName())
                .firstName(request.firstname())
                .lastName(request.lastname())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .taxId(request.taxId())
                .build();

        return clientRepository.save(client);
    }

    public Client getClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found");
        }
        clientRepository.deleteById(id);
    }

    public Client updateClient(UUID id, ClientRequest request) {
        Client client = clientRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        client.setBusinessName(request.businessName());
        client.setFirstName(request.firstname());
        client.setLastName(request.lastname());
        client.setAddress(request.address());
        client.setPhoneNumber(request.phoneNumber());
        client.setTaxId(request.taxId());

        return clientRepository.save(client);
    }

    public List<Client> searchByNameOrCuit(String query) {
        return null;
    }
}
