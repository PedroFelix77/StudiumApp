// Arquivo: src/main/java/com/studium/studium_academico/infrastructure/config/DataInitializer.java

package com.studium.studium_academico.infrastructure.config;

import com.studium.studium_academico.business.dto.response.AddressResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Address;
import com.studium.studium_academico.infrastructure.entity.Institution;
import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.business.service.AddressService;
import com.studium.studium_academico.infrastructure.repository.InstitutionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

@Component
public class DataInitializer {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AddressService addressService;

    @PostConstruct
    @Transactional
    public void init() {
        if (institutionRepository.count() == 0) {
            criarInstituicaoPadrao();
        }
    }

    private void criarInstituicaoPadrao() {
        AddressRequestDTO addressDTO = new AddressRequestDTO(
                "Av. José Bonifácio",
                "590",
                "Próximo a CAIXA",
                "Arcoverde",
                "Pernambuco",
                "56506100"
        );

        Address address = addressService.createEntity(addressDTO);

        Institution institution = Institution.builder()
                .name("Studium Academy")
                .cnpj("12345678000199")
                .email("contato@studium.com")
                .phone("87 987654321")
                .address(address)
                .build();

        institutionRepository.save(institution);
        System.out.println("Instituição padrão criada com sucesso!");
    }
}