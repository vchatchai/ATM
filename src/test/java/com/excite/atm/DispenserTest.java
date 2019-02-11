package com.excite.atm;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static org.junit.Assert.assertEquals;


public class DispenserTest {

	private Dispenser dispenser = new Dispenser();
	private int Note1000 = 200;
	private int Note500 = 200;
	private int Note100 = 500;
	private int Note50 = 1000;
	private int Note20 = 5000;



	@Test
	public void testDispensingUnderValue() throws MoneyException {
		Map<NoteType, Integer> map = new HashMap<>();
		map.put(NoteType.Note1000, 1);
		map.put(NoteType.Note500, 1);
		map.put(NoteType.Note100, 1);

		BigDecimal amount = dispenser.deposit(map);
		amount = new BigDecimal(500);
		System.out.println("Deposit:" + amount);
 		Map<NoteType, Integer> result = dispenser.dispensing(amount);

			System.out.println(result);
			BigDecimal total = BigDecimal.ZERO;
			for (Entry<NoteType, Integer> note : result.entrySet()) {
				BigDecimal totalAmountInNote = note.getKey().getAmount().multiply(new BigDecimal(note.getValue()));
				total = total.add(totalAmountInNote);




			}


			assertEquals(amount, total);

	}

	@Test
	public void testDispensing() throws MoneyException {
		Map<NoteType, Integer> map = new HashMap<>();
		map.put(NoteType.Note1000, Note1000);
		map.put(NoteType.Note500, Note500);
		map.put(NoteType.Note100, Note100);
		map.put(NoteType.Note50, Note50);
		map.put(NoteType.Note20, Note20);

		BigDecimal amount = dispenser.deposit(map);
		System.out.println("Deposit:" + amount);


		System.out.println(dispenser.getTotalMoney());
		BigDecimal summary = BigDecimal.ZERO;
		Map<NoteType, Integer> totalNoteWithdraw = new HashMap<>();
		for (int count = 110; count < 2000; count = count+10) {
			System.out.println(count);
			amount = new BigDecimal(count);

			Map<NoteType, Integer> result = dispenser.dispensing(amount);

			System.out.println(result);
			BigDecimal total = BigDecimal.ZERO;
			for (Entry<NoteType, Integer> note : result.entrySet()) {
				BigDecimal totalAmountInNote = note.getKey().getAmount().multiply(new BigDecimal(note.getValue()));
				total = total.add(totalAmountInNote);

				Integer totalNotes = totalNoteWithdraw.get(note.getKey());
				if(Objects.isNull(totalNotes)){
					totalNotes = 0;
				}
				totalNotes = totalNotes + note.getValue();
				totalNoteWithdraw.put(note.getKey(),totalNotes);


			}


			summary = summary.add(total);
			System.out.println("s "+total);
			System.out.println("r "+dispenser.getTotalMoney());
			//test total withdraw
			assertEquals(amount, total);

			//test summary notes
			assertEquals(Note1000,getNote(totalNoteWithdraw,NoteType.Note1000)+getNote(dispenser.currentMoney,NoteType.Note1000));
			assertEquals(Note500,getNote(totalNoteWithdraw,NoteType.Note500)+getNote(dispenser.currentMoney,NoteType.Note500));
			assertEquals(Note100,getNote(totalNoteWithdraw,NoteType.Note100)+getNote(dispenser.currentMoney,NoteType.Note100));
			assertEquals(Note50,getNote(totalNoteWithdraw,NoteType.Note50)+getNote(dispenser.currentMoney,NoteType.Note50));
			assertEquals(Note20,getNote(totalNoteWithdraw,NoteType.Note20)+getNote(dispenser.currentMoney,NoteType.Note20));
		}

		System.out.println("summary "+summary);
		System.out.println("getTotalMoney "+dispenser.getTotalMoney());
	}

	private int getNote(Map<NoteType, Integer> notes,NoteType noteType){
		Integer note = notes.get(noteType);
		if(Objects.isNull(note)){
			return 0;
		}

		return note;


	}

}
