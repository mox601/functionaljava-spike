package fm.mox.spikes.functionaljava.reader;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface UserRepository {
    User get(int id);
    User find(String username);
}