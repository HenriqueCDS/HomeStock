package com.stockflow.service;

import com.stockflow.domain.dto.company.CompanyRequestDTO;
import com.stockflow.domain.dto.company.CompanyResponseDTO;
import com.stockflow.domain.entity.Company;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.mapper.CompanyMapper;
import com.stockflow.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public CompanyResponseDTO getByTenantId(UUID tenantId) {
        Company company = companyRepository.findByTenantIdAndDeletedAtIsNull(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found for tenant"));
        return companyMapper.toResponse(company);
    }

    @Transactional
    public CompanyResponseDTO update(UUID tenantId, CompanyRequestDTO request) {
        Company company = companyRepository.findByTenantIdAndDeletedAtIsNull(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found for tenant"));

        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setAddress(request.address());

        return companyMapper.toResponse(companyRepository.save(company));
    }
}
