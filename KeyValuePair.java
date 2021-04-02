/**
 * @author jesse russell
 */
public class KeyValuePair<K extends Comparable, V> implements Comparable<KeyValuePair<K, V>>{
    public final K key;
    public  V value;
    
    public KeyValuePair(K key, V value) { this.key = key; this.value = value; }
    public KeyValuePair(K key) { this(key, null); }
    
    @Override
    public int compareTo(KeyValuePair<K, V> other) { return key.compareTo(other.key); }
}
