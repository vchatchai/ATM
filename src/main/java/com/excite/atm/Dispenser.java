package com.excite.atm;

import java.math.BigDecimal;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dispenser {

    Map<NoteType, Integer> currentMoney = new HashMap<>();

    public BigDecimal deposit(Map<NoteType, Integer> amount) {

        for (Map.Entry<NoteType, Integer> entry : amount.entrySet()) {
            Integer value = currentMoney.get(entry.getKey());
            if (Objects.isNull(value)) {
                value = 0;
            }

            value = value + entry.getValue();

            currentMoney.put(entry.getKey(), value);
        }

        BigDecimal totalAmount = getTotalMoney(amount);

        return totalAmount;
    }

    public String printMoney() {
        StringBuilder builder = new StringBuilder();

        Formatter fmt = new Formatter(builder);
        fmt.format("Total Notes Remaining%n");
        for (Map.Entry<NoteType, Integer> entry : currentMoney.entrySet()) {
            if(entry.getValue() != 0){
                fmt.format("%s = %s%n", entry.getKey().toString(), entry.getValue());
            }
        }
        
        fmt.format("Total Amount Remaining: %s%n", getTotalMoney());
        return builder.toString();
    }

    public BigDecimal getTotalMoney() {
        return getTotalMoney(currentMoney);
    }

    public Map<NoteType, Integer> dispensing(BigDecimal amount) throws MoneyException {

        BigDecimal totalCurrentMoney = getTotalMoney(currentMoney);

        if (totalCurrentMoney.compareTo(amount) < 0) {
            throw new MoneyException("Money is not enough for withdraw.");
        }

        Map<NoteType, Integer> result = new HashMap<>();
        processing(amount, result, null);

        BigDecimal totalWithdraw = getTotalMoney(result);
        if (totalWithdraw.compareTo(amount) < 0) {
            for(Map.Entry<NoteType,Integer> entry: result.entrySet()){
                Integer note = currentMoney.get(entry.getKey());
                if(Objects.isNull(note)){
                    note = 0;
                }
                note = note + entry.getValue();
                currentMoney.put(entry.getKey(), note);
                entry.setValue(0);
            }

            for(Map.Entry<NoteType,Integer> entry: currentMoney.entrySet()){

                if(entry.getKey().getAmount().compareTo(amount) == 0){
                   pushMoney(result,entry.getKey(), getMoney(currentMoney, entry.getKey()));
                   return result;
                }

            }


            throw new MoneyException("Can't withdraw in this amount.");
        }

        return result;
    }

    private BigDecimal getTotalMoney(Map<NoteType, Integer> amount) {
        BigDecimal totalAmount = amount.entrySet().stream()
                .map(money -> money.getKey().getAmount().multiply(new BigDecimal(money.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount;
    }

    private void processing(BigDecimal withdraw, Map<NoteType, Integer> result, NoteType noteBefore) {
        BigDecimal amount = withdraw;
        for (NoteType note : NoteType.values()) {
            if (note.getAmount().compareTo(amount) < 0) {

                Long total = amount.longValue() / note.getAmount().longValue();
                for (; total > 0; total--) {
                    Integer money = getMoney(currentMoney, note);
                    pushMoney(result, note, money);

                }
                amount = withdraw.subtract(getTotalMoney(result));

            }
            if (note.getAmount().compareTo(amount) == 0) {

                if (NoteType.Note20.equals(note)) {
                    Integer money = getMoney(currentMoney, note);
                    pushMoney(result, note, money);
                    amount = getTotalMoney(result);
                    return;
                } else if (!result.isEmpty()) {

                    Integer money = getMoney(currentMoney, note);
                    pushMoney(result, note, money);
                    amount = getTotalMoney(result);
                    return;
                } else if (NoteType.Note50.equals(note)) {
                    Integer money = getMoney(currentMoney, note);
                    pushMoney(result, note, money);
                    amount = getTotalMoney(result);
                    return;
                }

            }
            if (NoteType.Note20.equals(note) && amount.longValue() < 20 && amount.longValue() > 0) {

                Integer money = getMoney(currentMoney, NoteType.Note50);
                pushMoney(result, NoteType.Note50, money);
                BigDecimal overAmount = getTotalMoney(result);
                overAmount = overAmount.subtract(withdraw);

                if (overAmount.longValue() % NoteType.Note100.getAmount().longValue() == 0) {

                    while (BigDecimal.ZERO.compareTo(overAmount) != 0) {

                        money = getMoney(result, NoteType.Note100);
                        if (money == 0) {
                            exchange(NoteType.Note100, result, currentMoney);
                        }
                        pushMoney(currentMoney, NoteType.Note100, money);
                        overAmount = getTotalMoney(result);
                        overAmount = overAmount.subtract(withdraw);
                    }
                }

                if (overAmount.longValue() % NoteType.Note20.getAmount().longValue() == 0) {
                    while (BigDecimal.ZERO.compareTo(overAmount) != 0) {

                        money = getMoney(result, NoteType.Note20);
                        if (money == 0) {
                            exchange(NoteType.Note20, result, currentMoney);
                        }
                        pushMoney(currentMoney, NoteType.Note20, money);
                        overAmount = getTotalMoney(result);
                        overAmount = overAmount.subtract(withdraw);
                    }
                }
            }

        }

    }

    private void exchange(NoteType note, Map<NoteType, Integer> result, Map<NoteType, Integer> currentMoney) {

        for (NoteType exchangeNote : NoteType.values()) {
            if (!exchangeNote.equals(note)) {
                Integer totalNote = result.get(exchangeNote);
                if (Objects.nonNull(totalNote) && totalNote > 0) {
                    for (int count = 1; count <= totalNote; count++) {
                        long exchangeNoteCount = exchangeNote.getAmount().longValue() * count;
                        if (exchangeNoteCount % note.getAmount().longValue() == 0) {
                            int total = (int) (exchangeNoteCount / note.getAmount().longValue());
                            getMoney(currentMoney, note,total);
                            pushMoney(result, note, total);

                            Integer money = getMoney(result, exchangeNote, count);
                            pushMoney(currentMoney, exchangeNote, money);

                        }
                    }
                }

            }
        }

    }

    private void pushMoney(Map<NoteType, Integer> result, NoteType note, Integer money) {
        Integer temp = result.get(note);
        if (Objects.isNull(temp)) {
            temp = 0;
        }
        result.put(note, temp + money);

    }

    private int getMoney(Map<NoteType, Integer> result, NoteType note) {
        return getMoney(result, note, 1);

    }

    private int getMoney(Map<NoteType, Integer> result, NoteType note, int count) {
        Integer money = result.get(note);
        if (Objects.isNull(money)) {
            return 0;
        }
        if (money == 0) {
            return 0;
        } else {
            money = money - count;
            result.put(note, money);
            return count;
        }

    }

}
