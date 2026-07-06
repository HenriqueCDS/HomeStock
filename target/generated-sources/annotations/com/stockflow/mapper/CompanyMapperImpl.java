package com.stockflow.mapper;

import com.stockflow.domain.dto.company.CompanyResponseDTO;
import com.stockflow.domain.entity.Company;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-06T13:49:38-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class CompanyMapperImpl implements CompanyMapper {

    @Override
    public CompanyResponseDTO toResponse(Company company) {
        if ( company == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String cnpj = null;
        UUID tenantId = null;
        String email = null;
        String phone = null;
        String address = null;
        boolean active = false;
        LocalDateTime createdAt = null;

        id = company.getId();
        name = company.getName();
        cnpj = company.getCnpj();
        tenantId = company.getTenantId();
        email = company.getEmail();
        phone = company.getPhone();
        address = company.getAddress();
        active = company.isActive();
        createdAt = company.getCreatedAt();

        CompanyResponseDTO companyResponseDTO = new CompanyResponseDTO( id, name, cnpj, tenantId, email, phone, address, active, createdAt );

        return companyResponseDTO;
    }
}
