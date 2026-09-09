package br.com.zattaz.partner.domain.exception;

import br.com.zattaz.common.exception.ZattazNotFoundException;
import java.util.UUID;

public class PartnerNotFoundException extends ZattazNotFoundException {

    public PartnerNotFoundException(UUID partnerId) {
        super("Partner not found: " + partnerId);
    }
}
