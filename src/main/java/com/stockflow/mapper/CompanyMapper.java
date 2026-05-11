package com.stockflow.mapper;

import com.stockflow.domain.dto.company.CompanyResponseDTO;
import com.stockflow.domain.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponseDTO toResponse(Company company);
}
