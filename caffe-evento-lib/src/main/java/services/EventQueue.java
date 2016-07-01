package services;

/**
 * Created by chris on 7/1/16.
 */
public interface EventQueue extends ServiceChangedListener{
    void registerService(Service theService);
}
