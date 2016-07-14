package inrtfs;

public interface Observer {
    void update (float temperature, float humidity, int pressure);
    void update(String actiontxt);
}
