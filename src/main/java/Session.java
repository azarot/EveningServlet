import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class Session {
    private Instant timestamp;
    private final Duration livingTime = Duration.ofMinutes(1);
    private final UUID id;
    private String name;

    public Session(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.timestamp = Instant.now();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getName() {
        if (isValid()) {
            return Optional.of(name);
        }
        return Optional.empty();
    }

    public boolean isValid() {
        return Duration.between(timestamp, Instant.now()).getSeconds() < livingTime.getSeconds();
    }

    public void updateTimestampIfValid() {
        if (isValid()) {
            timestamp = Instant.now();
        }
    }

    private UUID getId() {
        return id;
    }
}
