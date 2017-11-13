package comp6231.project.farid.servers.serverDorval;

import java.time.LocalDate;

class CountController {

    private int counter;
    private LocalDate expireDate;

    CountController(LocalDate startDate) {
        counter = 0;
        expireDate = startDate.plusDays(7);
    }

    void incrementCounter() {
        this.counter++;
    }

    int getCounter() {
        return counter;
    }

    LocalDate getExpireDate() {
        return expireDate;
    }

    void reset(LocalDate currentTime) {
        System.out.println("RESET");
        expireDate = currentTime.plusDays(7);
        counter = 0;
    }

    void decrementCounter() {
        this.counter--;
    }
}
