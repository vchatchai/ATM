package com.excite.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DispenserTest {

	private Dispenser dispenser = new Dispenser();

	@BeforeEach
	public void setup() {
		Map<NoteType, Integer> map = new HashMap<>();
		map.put(NoteType.Note1000, 200);
		map.put(NoteType.Note500, 200);
		map.put(NoteType.Note100, 500);
		map.put(NoteType.Note50, 1000);
		map.put(NoteType.Note20, 5000);

		BigDecimal amount = dispenser.deposit(map);
		System.out.println("Deposit:" + amount);

	}

	@Test
	public void testDispensing1000() throws MoneyException {

		for (int count = 100; count < 2000; count = count+10) {
			System.out.println(count);
			BigDecimal amount = new BigDecimal(count);

			Map<NoteType, Integer> result = dispenser.dispensing(amount);

			System.out.println(result);
			BigDecimal total = BigDecimal.ZERO;
			for (Entry<NoteType, Integer> note : result.entrySet()) {
				BigDecimal totalAmountInNote = note.getKey().getAmount().multiply(new BigDecimal(note.getValue()));
				total = total.add(totalAmountInNote);

			}

			assertEquals(amount, total);
		}

	}

}
