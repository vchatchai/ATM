package com.excite.atm;

import java.math.BigDecimal;

public enum NoteType {
    Note1000(new BigDecimal(1000)),
    Note500(new BigDecimal(500)),
    Note100(new BigDecimal(100)),
    Note50(new BigDecimal(50)),
    Note20(new BigDecimal(20));
    private BigDecimal amount;

	private NoteType(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
    
    
}

