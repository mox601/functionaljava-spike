package fm.mox.spikes.functionaljava.reader;

import lombok.Value;
import lombok.experimental.Tolerate;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Value
public class User {
    int id;
    String username;
    User supervisor;
    @Tolerate
    public User(final int id, final String username) {
        this(id, username, null);
    }
}
