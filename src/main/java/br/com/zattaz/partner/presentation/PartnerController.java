package br.com.zattaz.partner.presentation;

import br.com.zattaz.partner.application.usecase.GetPartnerUseCase;
import br.com.zattaz.partner.application.usecase.ListPartnersUseCase;
import br.com.zattaz.partner.application.usecase.UpsertPartnerUseCase;
import br.com.zattaz.partner.presentation.mapper.PartnerRestMapper;
import br.com.zattaz.partner.presentation.model.PartnerPageResponse;
import br.com.zattaz.partner.presentation.model.PartnerResponse;
import br.com.zattaz.partner.presentation.model.PartnerUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners")
@Tag(name = "Parceiros")
public class PartnerController {

    private final UpsertPartnerUseCase upsertPartnerUseCase;
    private final GetPartnerUseCase getPartnerUseCase;
    private final ListPartnersUseCase listPartnersUseCase;
    private final PartnerRestMapper partnerRestMapper;

    @PutMapping("/{partnerId}")
    @Operation(summary = "Cria ou atualiza um parceiro informando os créditos")
    public PartnerResponse upsert(
            @PathVariable UUID partnerId, @Valid @RequestBody PartnerUpsertRequest body) {
        return partnerRestMapper.toResponse(
                upsertPartnerUseCase.execute(partnerRestMapper.toDomain(body, partnerId)));
    }

    @GetMapping("/{partnerId}")
    @Operation(summary = "Consulta um parceiro com seus créditos")
    public PartnerResponse get(@PathVariable UUID partnerId) {
        return partnerRestMapper.toResponse(getPartnerUseCase.execute(partnerId));
    }

    @GetMapping
    @Operation(summary = "Lista parceiros")
    public PartnerPageResponse list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return partnerRestMapper.toPageResponse(listPartnersUseCase.execute(page, size));
    }
}
