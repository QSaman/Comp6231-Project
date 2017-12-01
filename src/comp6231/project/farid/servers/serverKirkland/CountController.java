package comp6231.project.farid.servers.serverKirkland;

import java.io.Serializable;
import java.time.LocalDate;

class CountController implements Serializable {

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
